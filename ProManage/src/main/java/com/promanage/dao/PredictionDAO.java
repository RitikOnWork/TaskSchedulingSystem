package com.promanage.dao;

import com.promanage.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionDAO {

    // Fetch weekly aggregated data: Week Start Date -> {Total Revenue, Project
    // Count}
    // We group by week to have data points for regression
    private static final String WEEKLY_STATS_QUERY = "SELECT DATE_TRUNC('week', created_at) as week_start, " +
            "       SUM(revenue) as total_revenue, " +
            "       COUNT(*) as project_count " +
            "FROM projects " +
            "WHERE created_at >= CURRENT_DATE - INTERVAL '6 MONTHS' " +
            "GROUP BY week_start " +
            "ORDER BY week_start ASC";

    // Fetch all deadlines to analyze distribution
    private static final String DEADLINE_DIST_QUERY = "SELECT deadline FROM projects WHERE created_at >= CURRENT_DATE - INTERVAL '6 MONTHS'";

    public Map<LocalDate, double[]> getWeeklyStats() throws SQLException {
        Map<LocalDate, double[]> stats = new HashMap<>(); // Date -> [Revenue, Count]
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(WEEKLY_STATS_QUERY);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("week_start").toLocalDate();
                double revenue = rs.getDouble("total_revenue");
                double count = rs.getInt("project_count");
                stats.put(date, new double[] { revenue, count });
            }
        }
        return stats;
    }

    public List<Integer> getHistoricalDeadlines() throws SQLException {
        List<Integer> deadlines = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DEADLINE_DIST_QUERY);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                deadlines.add(rs.getInt("deadline"));
            }
        }
        return deadlines;
    }

    public List<Double> getAllProjectRevenues() throws SQLException {
        List<Double> revenues = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        // Just fetch all completed projects to calculate distribution
        String sql = "SELECT revenue FROM projects";

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                revenues.add(rs.getDouble("revenue"));
            }
        }
        return revenues;
    }
}
