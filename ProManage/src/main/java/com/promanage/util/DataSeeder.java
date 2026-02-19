package com.promanage.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class DataSeeder {

    private static final Random random = new Random();

    public void seedHistoricalData() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String insertSQL = "INSERT INTO projects (project_id, title, deadline, revenue, created_at, completion_date, status, project_type) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("  [SEED] Clearing old data...");
        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM scheduled_projects");
            stmt.executeUpdate("DELETE FROM projects");
        }

        System.out.println("  [SEED] Generating 6 months of historical data...");
        conn.setAutoCommit(false); // Batch insert

        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            LocalDate today = LocalDate.now();
            int projectCount = 0;

            // Generate data for past 10 weeks
            for (int i = 0; i < 10; i++) {
                LocalDate weekStart = today.minusWeeks(10 - i);

                // Randomly generate 3 to 8 projects per week
                // Simulate a trend: slight increase in projects over time
                int numProjects = 3 + random.nextInt(6) + (i / 8);

                for (int j = 0; j < numProjects; j++) {
                    String projectId = "HIST-" + (projectCount++);
                    String title = "Historical Project " + projectCount;
                    int deadline = 1 + random.nextInt(5);

                    // Simulate revenue trend: Base 20k + random, increasing slightly over weeks
                    double revenue = 20000 + random.nextInt(80000) + (i * 1000);

                    LocalDate createdDate = weekStart.plusDays(random.nextInt(5));
                    LocalDate completionDate = createdDate.plusDays(1 + random.nextInt(10));

                    ps.setString(1, projectId);
                    ps.setString(2, title);
                    ps.setInt(3, deadline);
                    ps.setDouble(4, revenue);
                    ps.setObject(5, java.sql.Timestamp.valueOf(createdDate.atStartOfDay()));
                    ps.setObject(6, java.sql.Date.valueOf(completionDate));
                    ps.setString(7, "COMPLETED");
                    ps.setString(8, "GENERAL");

                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
            System.out.println("  [SEED] Successfully inserted " + projectCount + " historical projects.");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
