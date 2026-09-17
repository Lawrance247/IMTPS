package api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import repositories.TaskDB;
import services.TaskManager;

public class Application {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new TaskManager(new TaskDB());
        ApiHandler apiHandler = new ApiHandler(taskManager);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/tasks", apiHandler::handleTasks);
        server.createContext("/api/schedules/generate", apiHandler::handleSchedule);
        server.createContext("/api/schedules/compare", apiHandler::handleComparison);
        server.createContext("/api/health", apiHandler::handleHealth);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("IMTPS API running on http://localhost:8080");
    }
}
