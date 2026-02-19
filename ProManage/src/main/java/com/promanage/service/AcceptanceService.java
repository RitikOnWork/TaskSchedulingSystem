package com.promanage.service;

import com.promanage.dao.PredictionDAO;
import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;

import com.promanage.dao.ProjectDAO;

import java.util.ArrayList;

import java.sql.SQLException;
import java.util.List;

public class AcceptanceService {

    private final PredictionDAO predictionDAO;
    private final ForecastingEngine forecastingEngine = new ForecastingEngine();

    public AcceptanceService() {
        this.predictionDAO = new PredictionDAO();
    }

    private final List<Project> acceptedProjects = new ArrayList<>();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final SchedulerService schedulerService = new SchedulerService();

    // Reset state for a new planning session
    public void startNewWeekSession() {
        acceptedProjects.clear();
    }

    public static class AcceptanceResult {
        public final boolean accepted;
        public final String reason;

        public AcceptanceResult(boolean accepted, String reason) {
            this.accepted = accepted;
            this.reason = reason;
        }
    }

    public AcceptanceResult evaluateIncomingProject(String title, double revenue, int deadline) throws SQLException {
        // 1. Capacity Check: Max 5 projects/week
        if (acceptedProjects.size() >= 5) {
            return new AcceptanceResult(false, "REJECTED: Weekly capacity (5 projects) is full.");
        }

        // 2. Feasibility Check: Can it fit with existing accepted projects?
        // We simulate a schedule with the new project added.
        List<Project> testList = new ArrayList<>(acceptedProjects);
        Project newProject = new Project("TEST-ID", title, deadline, revenue);
        testList.add(newProject);

        List<ScheduledProject> feasibleSchedule = schedulerService.generateOptimalSchedule(testList);

        // A simpler rule: Can we schedule ALL accepted projects + this new one?
        if (feasibleSchedule.size() < testList.size()) {
            return new AcceptanceResult(false,
                    "REJECTED: Deadline conflict. Cannot schedule this with already accepted projects.");
        }

        // 3. Profitability Check (Dynamic Threshold)
        // STRICT RULE: Revenue must be >= Average.

        List<Double> historicalRevenues = predictionDAO.getAllProjectRevenues();
        double mean = 0;
        if (!historicalRevenues.isEmpty()) {
            mean = forecastingEngine.calculateAverage(historicalRevenues);
        }

        if (revenue >= mean) {
            // Standard Rule: Accept if >= Average
            // Persist to DB
            try {
                Project saved = projectDAO.addProject(title, deadline, revenue);
                acceptedProjects.add(saved);
                return new AcceptanceResult(true,
                        String.format("ACCEPTED: Revenue (%.0f) is above Average (%.0f). Slots used: %d/5",
                                revenue, mean, acceptedProjects.size()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new AcceptanceResult(false, "ERROR: Database Persistence Failed: " + e.getMessage());
            }
        } else {
            // Rejection Path
            return new AcceptanceResult(false, String
                    .format("REJECTED: Revenue (%.0f) is below Historical Average (%.0f). (Rule: Revenue >= Average)",
                            revenue, mean));
        }
    }

    public List<Project> getAcceptedProjects() {
        return new ArrayList<>(acceptedProjects);
    }
}
