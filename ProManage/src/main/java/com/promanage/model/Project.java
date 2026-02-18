package com.promanage.model;

public class Project {

    private String projectId;
    private String title;
    private int    deadline;
    private double revenue;

    public Project() {}

    public Project(String projectId, String title, int deadline, double revenue) {
        this.projectId = projectId;
        this.title     = title;
        this.deadline  = deadline;
        this.revenue   = revenue;
    }

    public String getProjectId()              { return projectId; }
    public void   setProjectId(String id)     { this.projectId = id; }

    public String getTitle()                  { return title; }
    public void   setTitle(String title)      { this.title = title; }

    public int    getDeadline()               { return deadline; }
    public void   setDeadline(int deadline)   { this.deadline = deadline; }

    public double getRevenue()                { return revenue; }
    public void   setRevenue(double revenue)  { this.revenue = revenue; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-40s | Deadline: Day %-2d | Revenue: Rs.%,.2f |",
                projectId, title, deadline, revenue);
    }
}