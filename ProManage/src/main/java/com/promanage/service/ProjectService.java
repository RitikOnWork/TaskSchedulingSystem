package com.promanage.service;

import com.promanage.dao.ProjectDAO;
import com.promanage.dao.ScheduleDAO;
import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;

import java.sql.SQLException;
import java.util.List;

public class ProjectService {

    private final ProjectDAO       projectDAO       = new ProjectDAO();
    private final ScheduleDAO      scheduleDAO      = new ScheduleDAO();
    private final SchedulerService schedulerService = new SchedulerService();

    public Project addProject(String title, int deadline, double revenue) throws SQLException {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Project title cannot be empty.");
        if (deadline < 1 || deadline > 5)
            throw new IllegalArgumentException("Deadline must be between 1 and 5 working days.");
        if (revenue <= 0)
            throw new IllegalArgumentException("Revenue must be a positive value.");

        return projectDAO.addProject(title.trim(), deadline, revenue);
    }

    public List<Project> getAllProjects() throws SQLException {
        return projectDAO.getAllProjects();
    }

    public Project getProjectById(String projectId) throws SQLException {
        if (projectId == null || projectId.trim().isEmpty())
            throw new IllegalArgumentException("Project ID cannot be empty.");
        return projectDAO.getProjectById(projectId.trim().toUpperCase());
    }

    public int getTotalProjectCount() throws SQLException {
        return projectDAO.getTotalProjectCount();
    }

    public List<ScheduledProject> generateAndSaveSchedule() throws SQLException {
        List<Project> allProjects = projectDAO.getAllProjects();
        if (allProjects.isEmpty())
            throw new IllegalStateException("No projects found. Please add projects first.");

        List<ScheduledProject> optimal = schedulerService.generateOptimalSchedule(allProjects);
        scheduleDAO.saveSchedule(optimal);
        return optimal;
    }

    public List<ScheduledProject> getLatestSchedule() throws SQLException {
        return scheduleDAO.getLatestSchedule();
    }

    public List<ScheduledProject> getAllSchedules() throws SQLException {
        return scheduleDAO.getAllSchedules();
    }

    public double calculateTotalRevenue(List<ScheduledProject> schedule) {
        return schedulerService.calculateTotalRevenue(schedule);
    }

    public List<Project> getUnscheduledProjects(List<ScheduledProject> schedule) throws SQLException {
        return schedulerService.getUnscheduledProjects(getAllProjects(), schedule);
    }
}