package api;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import repositories.DatabaseConnection;
import repositories.TaskDB;
import services.TaskManager;

/**
 * End-to-end backend test: HTTP API -> services -> SQLite.
 * Uses the Maven test database configured in pom.xml, not the real imtps.db.
 */
@TestMethodOrder(OrderAnnotation.class)
class ApiIntegrationTest {
    private static HttpServer server;
    private static HttpClient client;
    private static String baseUrl;

    private static final String runId = UUID.randomUUID().toString().substring(0, 8);
    private static final String emailA = "api-a-" + runId + "@example.com";
    private static final String emailB = "api-b-" + runId + "@example.com";
    private static final String password = "StrongPass123!";

    private static String tokenA;
    private static String tokenB;
    private static String taskAId;
    private static String noteAId;
    private static final String scheduleDate = LocalDate.now().plusDays(1).toString();

    @BeforeAll
    static void startServer() throws Exception {
        DatabaseConnection.initializeDatabase();
        ApiHandler handler = new ApiHandler(new TaskManager(new TaskDB()));

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/auth/register", handler::handleRegister);
        server.createContext("/api/auth/login", handler::handleLogin);
        server.createContext("/api/auth/logout", handler::handleLogout);
        server.createContext("/api/tasks", handler::handleTasks);
        server.createContext("/api/notes", handler::handleNotes);
        server.createContext("/api/settings", handler::handleSettings);
        server.createContext("/api/schedules/generate", handler::handleSchedule);
        server.createContext("/api/schedules/current", handler::handleCurrentSchedule);
        server.createContext("/api/schedules/compare", handler::handleComparison);
        server.createContext("/api/health", handler::handleHealth);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    @Order(1)
    void healthEndpointWorks() throws Exception {
        HttpResponse<String> response = request("GET", "/api/health", null, null);
        assertEquals(200, response.statusCode());
        assertEquals("ok", json(response).get("status").getAsString());
    }

    @Test
    @Order(2)
    void registerLoginAndProtectedApiWork() throws Exception {
        assertEquals(201, register("API User A", emailA).statusCode());
        assertEquals(201, register("API User B", emailB).statusCode());

        tokenA = login(emailA);
        tokenB = login(emailB);
        assertNotNull(tokenA);
        assertNotNull(tokenB);

        // Protected endpoint must reject requests without a session.
        assertEquals(401, request("GET", "/api/tasks", null, null).statusCode());
    }

    @Test
    @Order(3)
    void taskCrudPersistsAndIsIsolatedByUser() throws Exception {
        String deadline = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0).toString();
        String createBody = "{" +
                "\"title\":\"API Persistence Task\"," +
                "\"description\":\"Created by integration test\"," +
                "\"category\":\"ACADEMIC\"," +
                "\"deadline\":\"" + deadline + "\"," +
                "\"durationMinutes\":60," +
                "\"importance\":\"HIGH\"," +
                "\"effort\":\"MEDIUM\"}";

        HttpResponse<String> created = request("POST", "/api/tasks", tokenA, createBody);
        assertEquals(201, created.statusCode(), created.body());
        taskAId = json(created).get("id").getAsString();

        JsonArray tasksA = JsonParser.parseString(request("GET", "/api/tasks", tokenA, null).body()).getAsJsonArray();
        assertTrue(containsId(tasksA, taskAId));

        JsonArray tasksB = JsonParser.parseString(request("GET", "/api/tasks", tokenB, null).body()).getAsJsonArray();
        assertFalse(containsId(tasksB, taskAId), "User B must not see User A's task");
        assertEquals(404, request("GET", "/api/tasks/" + taskAId, tokenB, null).statusCode());

        String updateBody = createBody.replace("API Persistence Task", "Updated API Task")
                .replace("\"durationMinutes\":60", "\"durationMinutes\":90")
                .replace("}", ",\"status\":\"IN_PROGRESS\"}");
        HttpResponse<String> updated = request("PUT", "/api/tasks/" + taskAId, tokenA, updateBody);
        assertEquals(200, updated.statusCode(), updated.body());
        assertEquals("Updated API Task", json(updated).get("title").getAsString());
    }

    @Test
    @Order(4)
    void notesPersistAndAreIsolatedByUser() throws Exception {
        HttpResponse<String> created = request("POST", "/api/notes", tokenA,
                "{\"title\":\"API Note\",\"body\":\"Persistent note body\",\"tag\":\"Test\"}");
        assertEquals(201, created.statusCode(), created.body());
        noteAId = json(created).get("id").getAsString();

        JsonArray notesA = JsonParser.parseString(request("GET", "/api/notes", tokenA, null).body()).getAsJsonArray();
        assertTrue(containsId(notesA, noteAId));

        JsonArray notesB = JsonParser.parseString(request("GET", "/api/notes", tokenB, null).body()).getAsJsonArray();
        assertFalse(containsId(notesB, noteAId));
    }

    @Test
    @Order(5)
    void settingsPersistAndAreIsolatedByUser() throws Exception {
        String settings = "{\"theme\":\"light\",\"defaultStrategy\":\"deadline\"," +
                "\"defaultFocusMinutes\":45,\"dailyReminder\":false}";
        assertEquals(200, request("PUT", "/api/settings", tokenA, settings).statusCode());

        JsonObject a = json(request("GET", "/api/settings", tokenA, null));
        assertEquals("light", a.get("theme").getAsString());
        assertEquals("deadline", a.get("defaultStrategy").getAsString());
        assertEquals(45, a.get("defaultFocusMinutes").getAsInt());

        JsonObject b = json(request("GET", "/api/settings", tokenB, null));
        assertNotEquals("light", b.get("theme").getAsString(), "User B must keep separate settings");
    }

    @Test
    @Order(6)
    void allSchedulingStrategiesAndComparisonWork() throws Exception {
        for (String strategy : new String[] {"deadline", "focus", "slack", "balance"}) {
            String body = scheduleBody(strategy);
            HttpResponse<String> response = request("POST", "/api/schedules/generate", tokenA, body);
            assertEquals(200, response.statusCode(), strategy + ": " + response.body());
            assertTrue(json(response).has("items"));
        }

        String comparison = "{" +
                "\"strategyA\":\"deadline\"," +
                "\"strategyB\":\"focus\"," +
                "\"focusCategory\":\"ACADEMIC\"," +
                "\"dayStart\":\"08:00\"," +
                "\"dayEnd\":\"20:00\"," +
                "\"scheduleStart\":\"" + scheduleDate + "T08:00:00\"}";
        HttpResponse<String> compared = request("POST", "/api/schedules/compare", tokenA, comparison);
        assertEquals(200, compared.statusCode(), compared.body());
        assertTrue(json(compared).has("metricsA"));
        assertTrue(json(compared).has("metricsB"));
    }

    @Test
    @Order(7)
    void savedSchedulePersistsAndIsIsolatedByUser() throws Exception {
        // Save a known final schedule for A.
        assertEquals(200, request("POST", "/api/schedules/generate", tokenA, scheduleBody("deadline")).statusCode());

        HttpResponse<String> savedA = request("GET", "/api/schedules/current?date=" + scheduleDate, tokenA, null);
        assertEquals(200, savedA.statusCode(), savedA.body());
        assertEquals(scheduleDate, json(savedA).get("date").getAsString());

        // B has not generated a schedule for this date.
        assertEquals(404, request("GET", "/api/schedules/current?date=" + scheduleDate, tokenB, null).statusCode());
    }

    @Test
    @Order(8)
    void logoutInvalidatesSession() throws Exception {
        assertEquals(200, request("POST", "/api/auth/logout", tokenA, "{}").statusCode());
        assertEquals(401, request("GET", "/api/tasks", tokenA, null).statusCode());

        // User B remains logged in, proving logout is session-specific.
        assertEquals(200, request("GET", "/api/tasks", tokenB, null).statusCode());
    }

    private static HttpResponse<String> register(String name, String email) throws Exception {
        return request("POST", "/api/auth/register", null,
                "{\"fullName\":\"" + name + "\",\"email\":\"" + email +
                "\",\"password\":\"" + password + "\"}");
    }

    private static String login(String email) throws Exception {
        HttpResponse<String> response = request("POST", "/api/auth/login", null,
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");
        assertEquals(200, response.statusCode(), response.body());
        return json(response).get("token").getAsString();
    }

    private static String scheduleBody(String strategy) {
        return "{" +
                "\"strategy\":\"" + strategy + "\"," +
                "\"focusCategory\":\"ACADEMIC\"," +
                "\"dayStart\":\"08:00\"," +
                "\"dayEnd\":\"20:00\"," +
                "\"scheduleStart\":\"" + scheduleDate + "T08:00:00\"}";
    }

    private static HttpResponse<String> request(String method, String path, String token, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Accept", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static JsonObject json(HttpResponse<String> response) {
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static boolean containsId(JsonArray array, String id) {
        for (int i = 0; i < array.size(); i++) {
            if (id.equals(array.get(i).getAsJsonObject().get("id").getAsString())) return true;
        }
        return false;
    }
}
