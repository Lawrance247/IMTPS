package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import models.ComparisonResult;
import models.Schedule;
import models.ScheduleMetrics;
import models.ScheduledTask;
import models.Task;
import services.Comparison;
import services.Scheduler;
import services.TaskManager;
import taskmanagement.AuthManager;
import taskmanagement.User;
import strategies.CategoryBalanceStrategy;
import strategies.EarliestDeadlineFirstStrategy;
import strategies.SchedulingStrategy;
import strategies.SlackFocusStrategy;
import strategies.WeightedFocusStrategy;
import utils.Category;
import utils.TaskStatus;
import utils.Weights;

public class ApiHandler {
    private final TaskManager taskManager;
    private final AuthManager authManager;
    private final Gson gson = new Gson();

    public ApiHandler(TaskManager taskManager) {
        this(taskManager, new AuthManager());
    }

    public ApiHandler(TaskManager taskManager, AuthManager authManager) {
        this.taskManager = taskManager;
        this.authManager = authManager;
    }

    public void handleRegister(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            JsonObject body = readBody(exchange);
            User user = authManager.register(
                    requiredString(body, "fullName"),
                    requiredString(body, "email"),
                    requiredString(body, "password"));

            sendJson(exchange, 201, userJson(user));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            JsonObject body = readBody(exchange);
            User user = authManager.login(
                    requiredString(body, "email"),
                    requiredString(body, "password"));

            sendJson(exchange, 200, userJson(user));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 401, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    public void handleHealth(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }
        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        sendJson(exchange, 200, response);
    }

    public void handleTasks(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        try {
            String method = exchange.getRequestMethod();
            String id = taskId(exchange.getRequestURI());

            if ("GET".equals(method) && id == null) {
                List<JsonObject> response = new ArrayList<>();
                for (Task task : taskManager.getAllTasks()) response.add(taskJson(task));
                sendJson(exchange, 200, response);
                return;
            }

            if ("GET".equals(method) && id != null) {
                Task task = taskManager.getTaskById(id);
                if (task == null) {
                    sendError(exchange, 404, "Task not found");
                    return;
                }
                sendJson(exchange, 200, taskJson(task));
                return;
            }

            if ("POST".equals(method) && id == null) {
                JsonObject body = readBody(exchange);
                Task task = taskManager.createTask(
                        requiredString(body, "title"),
                        optionalString(body, "description"),
                        Category.valueOf(requiredString(body, "category").toUpperCase()),
                        LocalDateTime.parse(requiredString(body, "deadline")),
                        body.get("durationMinutes").getAsInt(),
                        Weights.valueOf(requiredString(body, "importance").toUpperCase()),
                        Weights.valueOf(requiredString(body, "effort").toUpperCase()));
                sendJson(exchange, 201, taskJson(task));
                return;
            }

            if ("PUT".equals(method) && id != null) {
                Task task = taskManager.getTaskById(id);
                if (task == null) {
                    sendError(exchange, 404, "Task not found");
                    return;
                }
                JsonObject body = readBody(exchange);
                task.setTitle(requiredString(body, "title"));
                task.setDescription(optionalString(body, "description"));
                task.setCategory(Category.valueOf(requiredString(body, "category").toUpperCase()));
                task.setDeadline(LocalDateTime.parse(requiredString(body, "deadline")));
                task.setDurationMinutes(body.get("durationMinutes").getAsInt());
                task.setImportance(Weights.valueOf(requiredString(body, "importance").toUpperCase()));
                task.setEffort(Weights.valueOf(requiredString(body, "effort").toUpperCase()));
                if (body.has("status")) {
                    task.setStatus(TaskStatus.valueOf(body.get("status").getAsString().toUpperCase()));
                }
                taskManager.updateTask(task);
                sendJson(exchange, 200, taskJson(task));
                return;
            }

            if ("DELETE".equals(method) && id != null) {
                taskManager.deleteTask(id);

                JsonObject response = new JsonObject();
                response.addProperty("deleted", true);
                response.addProperty("id", id);
                sendJson(exchange, 200, response);
                return;
            }

            sendError(exchange, 405, "Method not allowed");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (NoSuchElementException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    public void handleSchedule(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            JsonObject body = readBody(exchange);
            String strategyName = requiredString(body, "strategy").toLowerCase();
            SchedulingStrategy strategy;

            Category focus = body.has("focusCategory")
                    ? Category.valueOf(body.get("focusCategory").getAsString().toUpperCase())
                    : Category.ACADEMIC;

            switch (strategyName) {
                case "deadline":
                    strategy = new EarliestDeadlineFirstStrategy();
                    break;
                case "focus":
                    strategy = new WeightedFocusStrategy(focus);
                    break;
                case "slack":
                    strategy = new SlackFocusStrategy(focus);
                    break;
                case "balance":
                    strategy = new CategoryBalanceStrategy(focus);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown strategy: " + strategyName);
            }

            LocalTime start = body.has("dayStart") ? LocalTime.parse(body.get("dayStart").getAsString()) : LocalTime.of(8, 0);
            LocalTime end = body.has("dayEnd") ? LocalTime.parse(body.get("dayEnd").getAsString()) : LocalTime.of(20, 0);
            LocalDateTime scheduleStart = body.has("scheduleStart")
                    ? LocalDateTime.parse(body.get("scheduleStart").getAsString())
                    : null;

            Scheduler scheduler = new Scheduler(strategy, start, end);
            Schedule schedule = scheduler.generateSchedule(taskManager.getAllTasks(), scheduleStart);

            JsonObject response = new JsonObject();
            response.addProperty("strategy", strategy.getStrategyName());
            List<JsonObject> items = new ArrayList<>();
            for (ScheduledTask scheduled : schedule.getScheduledTasks()) {
                JsonObject item = new JsonObject();
                item.add("task", taskJson(scheduled.getTask()));
                item.addProperty("startTime", scheduled.getStartTime().toString());
                item.addProperty("endTime", scheduled.getEndTime().toString());
                item.addProperty("deadlineClash", scheduled.hasDeadlineClash());
                items.add(item);
            }
            response.add("items", gson.toJsonTree(items));
            response.addProperty("totalScheduledMinutes", schedule.getTotalScheduledMinutes());
            response.addProperty("deadlineClashes", schedule.getDeadlineClashes().size());
            sendJson(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    public void handleComparison(HttpExchange exchange) throws IOException {
        if (preflight(exchange)) return;
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            JsonObject body = readBody(exchange);

            String strategyAName = requiredString(body, "strategyA").toLowerCase();
            String strategyBName = requiredString(body, "strategyB").toLowerCase();

            Category focus = body.has("focusCategory")
                    ? Category.valueOf(body.get("focusCategory").getAsString().toUpperCase())
                    : Category.ACADEMIC;

            SchedulingStrategy strategyA = createStrategy(strategyAName, focus);
            SchedulingStrategy strategyB = createStrategy(strategyBName, focus);

            LocalTime start = body.has("dayStart")
                    ? LocalTime.parse(body.get("dayStart").getAsString())
                    : LocalTime.of(8, 0);
            LocalTime end = body.has("dayEnd")
                    ? LocalTime.parse(body.get("dayEnd").getAsString())
                    : LocalTime.of(20, 0);
            LocalDateTime scheduleStart = body.has("scheduleStart")
                    ? LocalDateTime.parse(body.get("scheduleStart").getAsString())
                    : null;

            Schedule scheduleA = new Scheduler(strategyA, start, end)
                    .generateSchedule(taskManager.getAllTasks(), scheduleStart);
            Schedule scheduleB = new Scheduler(strategyB, start, end)
                    .generateSchedule(taskManager.getAllTasks(), scheduleStart);

            ComparisonResult comparison = new Comparison(scheduleA, scheduleB, focus).compare();

            JsonObject response = new JsonObject();
            response.addProperty("strategyA", strategyA.getStrategyName());
            response.addProperty("strategyB", strategyB.getStrategyName());
            response.add("metricsA", metricsJson(comparison.getScheduleAMetrics()));
            response.add("metricsB", metricsJson(comparison.getScheduleBMetrics()));

            sendJson(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    private SchedulingStrategy createStrategy(String strategyName, Category focus) {
        switch (strategyName) {
            case "deadline":
                return new EarliestDeadlineFirstStrategy();
            case "focus":
                return new WeightedFocusStrategy(focus);
            case "slack":
                return new SlackFocusStrategy(focus);
            case "balance":
                return new CategoryBalanceStrategy(focus);
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
    }

    private JsonObject metricsJson(ScheduleMetrics metrics) {
        JsonObject json = new JsonObject();
        json.addProperty("deadlineClashes", metrics.getDeadlineClashes());
        json.addProperty("totalLateness", metrics.getTotalLateness());
        json.addProperty("averageSlack", metrics.getAverageSlack());
        json.addProperty("weightedLateness", metrics.getWeightedLateness());
        json.addProperty("focusAlignment", metrics.getFocusAlignment());
        return json;
    }

    private JsonObject userJson(User user) {
        JsonObject json = new JsonObject();
        json.addProperty("id", user.getId());
        json.addProperty("fullName", user.getFullName());
        json.addProperty("email", user.getEmail());
        json.addProperty("createdAt", user.getCreatedAt().toString());
        return json;
    }

    private JsonObject taskJson(Task task) {
        JsonObject json = new JsonObject();
        json.addProperty("id", task.getId());
        json.addProperty("title", task.getTitle());
        json.addProperty("description", task.getDescription());
        json.addProperty("category", task.getCategory().name());
        json.addProperty("deadline", task.getDeadline().toString());
        json.addProperty("durationMinutes", task.getDurationMinutes());
        json.addProperty("importance", task.getImportance().name());
        json.addProperty("effort", task.getEffort().name());
        json.addProperty("status", task.getStatus().name());
        return json;
    }

    private String taskId(URI uri) {
        String path = uri.getPath();
        String base = "/api/tasks";
        if (path.equals(base) || path.equals(base + "/")) return null;
        if (!path.startsWith(base + "/")) return null;
        String id = path.substring((base + "/").length());
        return id.isBlank() || id.contains("/") ? null : id;
    }

    private JsonObject readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            if (text.isBlank()) throw new IllegalArgumentException("Request body is required");
            return JsonParser.parseString(text).getAsJsonObject();
        }
    }

    private String requiredString(JsonObject body, String name) {
        if (!body.has(name) || body.get(name).isJsonNull() || body.get(name).getAsString().isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return body.get(name).getAsString();
    }

    private String optionalString(JsonObject body, String name) {
        return body.has(name) && !body.get(name).isJsonNull() ? body.get(name).getAsString() : "";
    }

    private boolean preflight(HttpExchange exchange) throws IOException {
        addCors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }

    private void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("error", message == null ? "Request failed" : message);
        sendJson(exchange, status, error);
    }

    private void sendJson(HttpExchange exchange, int status, Object value) throws IOException {
        addCors(exchange);
        byte[] bytes = gson.toJson(value).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
