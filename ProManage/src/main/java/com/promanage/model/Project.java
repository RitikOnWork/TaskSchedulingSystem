package com.promanage.model;

public class Project {

    private String projectId;
    private String title;
    private int deadline;
    private double revenue;
    private String status; // New field

    public Project() {
    }

    public Project(String projectId, String title, int deadline, double revenue) {
        this.projectId = projectId;
        this.title = title;
        this.deadline = deadline;
        this.revenue = revenue;
        this.status = "PENDING"; // Default
    }

    // Overloaded constructor for full mapping
    public Project(String projectId, String title, int deadline, double revenue, String status) {
        this(projectId, title, deadline, revenue);
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String id) {
        this.projectId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("| %-10s | %-40s | Day %-2d | Rs.%,.2f | %-10s |",
                projectId, title, deadline, revenue, status);
    }
}