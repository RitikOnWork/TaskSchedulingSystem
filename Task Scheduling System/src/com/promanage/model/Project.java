package com.promanage.model;

public class Project {
    private int id;
    private String title;
    private int deadline;
    private int revenue;

    public Project(int id, String title, int deadline, int revenue) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.revenue = revenue;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDeadline() { return deadline; }
    public int getRevenue() { return revenue; }
}
