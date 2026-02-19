package com.promanage.api;

import com.google.gson.Gson;
import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;
import com.promanage.service.AcceptanceService;
import com.promanage.service.PredictionService;
import com.promanage.service.ProjectService;
import com.promanage.service.SchedulerService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class APIServer {

    private static final int PORT = 8080;
    private static final Gson gson = new Gson();

    // Services
    private static final ProjectService projectService = new ProjectService();
    private static final PredictionService predictionService = new PredictionService();
    private static final AcceptanceService acceptanceService = new AcceptanceService();
    private static final SchedulerService schedulerService = new SchedulerService(); // Assuming state per request or
                                                                                     // shared?

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/projects", new ProjectHandler());
        server.createContext("/api/schedule", new ScheduleHandler());
        server.createContext("/api/acceptance/evaluate", new AcceptanceHandler());
        server.createContext("/api/acceptance/session", new SessionHandler());
        server.createContext("/api/predict", new PredictionHandler());

        server.setExecutor(null); // Default executor
        System.out.println("  [API] Server started on port " + PORT);
        server.start();
    }

    // ── Helpers ──
    private static void sendResponse(HttpExchange exchange, int statusCode, Object responseBody) throws IOException {
        String json = gson.toJson(responseBody);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        // CORS Headers for React Dev Server
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ── Handlers ──

    static class ProjectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    List<Project> projects = projectService.getAllProjects();
                    sendResponse(exchange, 200, projects);
                } catch (SQLException e) {
                    sendResponse(exchange, 500, Map.of("error", e.getMessage()));
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                // Add project logic
            }
        }
    }

    static class ScheduleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    // Check if we have accepted projects in session
                    List<Project> accepted = acceptanceService.getAcceptedProjects();
                    List<ScheduledProject> schedule;
                    if (!accepted.isEmpty()) {
                        schedule = schedulerService.generateOptimalSchedule(accepted);
                    } else {
                        schedule = projectService.getLatestSchedule();
                    }
                    sendResponse(exchange, 200, schedule);
                } catch (SQLException e) {
                    sendResponse(exchange, 500, Map.of("error", e.getMessage()));
                }
            }
        }
    }

    static class SessionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) { // Preflight
                sendResponse(exchange, 204, "");
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    acceptanceService.startNewWeekSession();
                    sendResponse(exchange, 200, Map.of("message", "New session started"));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500, Map.of("error", e.getMessage()));
                }
            }
        }
    }

    static class AcceptanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) { // Preflight
                sendResponse(exchange, 204, "");
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Read body
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    ProjectRequest req = gson.fromJson(body, ProjectRequest.class);

                    AcceptanceService.AcceptanceResult result = acceptanceService.evaluateIncomingProject(req.title,
                            req.revenue, req.deadline);
                    sendResponse(exchange, 200, result);
                } catch (SQLException e) {
                    e.printStackTrace(); // PRINT ERROR TO CONSOLE
                    sendResponse(exchange, 500, Map.of("error", e.getMessage()));
                }
            }
        }
    }

    static class PredictionHandler implements HttpHandler {
        @Override // Prediction logic
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    PredictionService.PredictionResult res = predictionService.predictNextWeek();
                    String rec = predictionService.getRecommendation(res.predictedRevenue, res.currentAvgRevenue);
                    // Combine into one DTO
                    Map<String, Object> response = Map.of(
                            "prediction", res,
                            "recommendation", rec);
                    sendResponse(exchange, 200, response);
                } catch (SQLException e) {
                    sendResponse(exchange, 500, Map.of("error", e.getMessage()));
                }
            }
        }
    }

    // DTOs
    static class ProjectRequest {
        String title;
        int deadline;
        double revenue;
    }
}
