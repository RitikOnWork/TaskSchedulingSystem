package com.promanage;

import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;
import com.promanage.service.ProjectService;
import com.promanage.util.DBConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProManageApp {

    private static final ProjectService service = new ProjectService();
    private static final Scanner        scanner = new Scanner(System.in);

    private static final String LINE  = "-".repeat(78);
    private static final String DLINE = "=".repeat(78);

    public static void main(String[] args) {
        printBanner();
        try {
            DBConnection.getConnection();
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt("  Enter your choice: ");
                switch (choice) {
                    case 1  -> addProject();
                    case 2  -> viewAllProjects();
                    case 3  -> searchProjectById();
                    case 4  -> generateSchedule();
                    case 5  -> viewLatestSchedule();
                    case 6  -> viewAllSchedules();
                    case 0  -> running = false;
                    default -> System.out.println("\n  [!] Invalid option. Enter 0 to 6.\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("\n[FATAL] DB connection failed: " + e.getMessage());
            System.err.println("Check credentials in DBConnection.java");
        } finally {
            DBConnection.closeConnection();
            System.out.println("\n  Goodbye! — ProManage Solutions Pvt. Ltd.\n");
            scanner.close();
        }
    }

    // ── Option 1 ──────────────────────────────────────────────────────────────
    private static void addProject() {
        printHeader("ADD NEW PROJECT");
        try {
            System.out.print("  Enter project title       : ");
            String title = scanner.nextLine().trim();

            int deadline = readInt("  Enter deadline (1-5 days)  : ");
            if (deadline < 1 || deadline > 5) {
                System.out.println("  [!] Deadline must be 1 to 5."); return;
            }

            double revenue = readDouble("  Enter expected revenue (Rs.): ");
            if (revenue <= 0) {
                System.out.println("  [!] Revenue must be positive."); return;
            }

            Project p = service.addProject(title, deadline, revenue);
            System.out.println("\n  [OK] Project added successfully!");
            System.out.println(LINE);
            System.out.printf("  Project ID  : %s%n",           p.getProjectId());
            System.out.printf("  Title       : %s%n",           p.getTitle());
            System.out.printf("  Deadline    : Must complete by Day %d%n", p.getDeadline());
            System.out.printf("  Revenue     : Rs.%,.2f%n",     p.getRevenue());
            System.out.println(LINE + "\n");

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Validation Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Option 2 ──────────────────────────────────────────────────────────────
    private static void viewAllProjects() {
        printHeader("ALL PROJECTS");
        try {
            List<Project> projects = service.getAllProjects();
            if (projects.isEmpty()) {
                System.out.println("  No projects found. Add some first.\n"); return;
            }
            System.out.printf("  Total: %d project(s)%n%n", projects.size());
            System.out.printf("  %-10s  %-38s  %-10s  %s%n",
                    "Project ID", "Title", "Deadline", "Revenue");
            System.out.println("  " + LINE);
            for (Project p : projects) {
                System.out.printf("  %-10s  %-38s  %-10s  Rs.%,.2f%n",
                        p.getProjectId(),
                        trunc(p.getTitle(), 38),
                        "Day " + p.getDeadline(),
                        p.getRevenue());
            }
            System.out.println("  " + LINE + "\n");
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Option 3 ──────────────────────────────────────────────────────────────
    private static void searchProjectById() {
        printHeader("SEARCH PROJECT BY ID");
        try {
            System.out.print("  Enter Project ID (e.g. PROJ-001): ");
            String id = scanner.nextLine().trim().toUpperCase();
            Project p = service.getProjectById(id);
            if (p == null) {
                System.out.println("  [!] Project '" + id + "' not found.\n"); return;
            }
            System.out.println("\n" + LINE);
            System.out.printf("  Project ID  : %s%n",           p.getProjectId());
            System.out.printf("  Title       : %s%n",           p.getTitle());
            System.out.printf("  Deadline    : Must complete by Day %d%n", p.getDeadline());
            System.out.printf("  Revenue     : Rs.%,.2f%n",     p.getRevenue());
            System.out.println(LINE + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Option 4 ──────────────────────────────────────────────────────────────
    private static void generateSchedule() {
        printHeader("GENERATE OPTIMAL WEEKLY SCHEDULE");
        System.out.println("  Algorithm  : Dynamic Programming (0/1 Knapsack on bitmask)");
        System.out.println("  Constraint : Max 5 projects | 1 per day | Deadline respected\n");
        try {
            List<Project> all = service.getAllProjects();
            if (all.isEmpty()) {
                System.out.println("  [!] No projects to schedule. Add projects first.\n"); return;
            }
            System.out.println("  Processing " + all.size() + " project(s)...\n");

            List<ScheduledProject> schedule = service.generateAndSaveSchedule();
            double total = service.calculateTotalRevenue(schedule);
            List<Project> missed = service.getUnscheduledProjects(schedule);

            System.out.println(DLINE);
            System.out.printf("  %-6s  %-11s  %-10s  %-30s  %-10s  %s%n",
                    "Day", "Day Name", "Proj ID", "Title", "Deadline", "Revenue");
            System.out.println(LINE);
            for (ScheduledProject sp : schedule) {
                Project p = sp.getProject();
                System.out.printf("  Day %-2d  %-11s  %-10s  %-30s  Day %-6d  Rs.%,.2f%n",
                        sp.getDayNumber(),
                        sp.getDayName(),
                        p.getProjectId(),
                        trunc(p.getTitle(), 30),
                        p.getDeadline(),
                        p.getRevenue());
            }
            System.out.println(LINE);
            System.out.printf("  %-56s Total Revenue : Rs.%,.2f%n", "", total);
            System.out.printf("  %-56s Scheduled     : %d / 5%n",   "", schedule.size());
            System.out.println(DLINE);

            if (!missed.isEmpty()) {
                System.out.println("\n  Projects NOT scheduled (deadline conflict / slot limit):");
                for (Project p : missed) {
                    System.out.printf("  [X] [%s] %s  (Deadline: Day %d | Revenue: Rs.%,.2f)%n",
                            p.getProjectId(), p.getTitle(), p.getDeadline(), p.getRevenue());
                }
            }
            System.out.println("\n  [OK] Schedule saved to database.\n");

        } catch (IllegalStateException e) {
            System.out.println("  [!] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Option 5 ──────────────────────────────────────────────────────────────
    private static void viewLatestSchedule() {
        printHeader("LATEST WEEKLY SCHEDULE");
        try {
            List<ScheduledProject> schedule = service.getLatestSchedule();
            if (schedule.isEmpty()) {
                System.out.println("  No schedule yet. Use Option 4 to generate one.\n"); return;
            }
            double total = service.calculateTotalRevenue(schedule);
            System.out.println(DLINE);
            System.out.printf("  %-6s  %-11s  %-10s  %-30s  %s%n",
                    "Day", "Day Name", "Proj ID", "Title", "Revenue");
            System.out.println(LINE);
            for (ScheduledProject sp : schedule) {
                Project p = sp.getProject();
                System.out.printf("  Day %-2d  %-11s  %-10s  %-30s  Rs.%,.2f%n",
                        sp.getDayNumber(), sp.getDayName(),
                        p.getProjectId(), trunc(p.getTitle(), 30), p.getRevenue());
            }
            System.out.println(LINE);
            System.out.printf("  %-54s Total Revenue : Rs.%,.2f%n", "", total);
            System.out.println(DLINE + "\n");
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Option 6 ──────────────────────────────────────────────────────────────
    private static void viewAllSchedules() {
        printHeader("ALL HISTORICAL SCHEDULES");
        try {
            List<ScheduledProject> all = service.getAllSchedules();
            if (all.isEmpty()) {
                System.out.println("  No schedule history found.\n"); return;
            }
            System.out.printf("  %-6s  %-10s  %-6s  %-11s  %-28s  %s%n",
                    "Sch.ID", "Proj ID", "Day", "Day Name", "Title", "Revenue");
            System.out.println(LINE);
            for (ScheduledProject sp : all) {
                Project p = sp.getProject();
                System.out.printf("  %-6d  %-10s  Day %-2d  %-11s  %-28s  Rs.%,.2f%n",
                        sp.getScheduleId(), p.getProjectId(),
                        sp.getDayNumber(), sp.getDayName(),
                        trunc(p.getTitle(), 28), p.getRevenue());
            }
            System.out.println(LINE + "\n");
        } catch (SQLException e) {
            System.out.println("  [X] Database Error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static void printBanner() {
        System.out.println("\n" + DLINE);
        System.out.println("        ProManage Solutions Pvt. Ltd.");
        System.out.println("        Automated Project Scheduling System");
        System.out.println("        Algorithm: Dynamic Programming | DB: PostgreSQL");
        System.out.println(DLINE + "\n");
    }

    private static void printMenu() {
        System.out.println("\n" + LINE);
        System.out.println("  MAIN MENU");
        System.out.println(LINE);
        System.out.println("  1  ->  Add New Project");
        System.out.println("  2  ->  View All Projects");
        System.out.println("  3  ->  Search Project by ID");
        System.out.println("  4  ->  Generate Optimal Weekly Schedule  [RECOMMENDED]");
        System.out.println("  5  ->  View Latest Schedule");
        System.out.println("  6  ->  View All Historical Schedules");
        System.out.println("  0  ->  Exit");
        System.out.println(LINE);
    }

    private static void printHeader(String title) {
        System.out.println("\n" + LINE);
        System.out.println("  " + title);
        System.out.println(LINE);
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Enter a valid integer."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Enter a valid number."); }
        }
    }

    private static String trunc(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}