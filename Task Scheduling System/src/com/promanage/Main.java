package com.promanage;

import com.promanage.dao.ProjectDAO;
import com.promanage.service.SchedulerService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ProjectDAO dao = new ProjectDAO();
        SchedulerService scheduler = new SchedulerService();

        while (true) {
            System.out.println("\n=== ProManage Project Scheduler ===");
            System.out.println("1. Add Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Generate Weekly Schedule");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    System.out.print("Deadline (1-5): ");
                    int deadline = sc.nextInt();
                    System.out.print("Revenue: ");
                    int revenue = sc.nextInt();
                    dao.addProject(title, deadline, revenue);
                }
                case 2 -> dao.getAllProjects()
                        .forEach(p -> System.out.println(
                                p.getId() + " | " + p.getTitle() +
                                        " | Deadline: " + p.getDeadline() +
                                        " | Revenue: " + p.getRevenue()
                        ));
                case 3 -> scheduler.generateSchedule(dao.getAllProjects());
                case 4 -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }
}
