package com.promanage.dao;

import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;
import com.promanage.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    private static final String INSERT_SCHEDULE =
            "INSERT INTO scheduled_projects (project_id, day_number, day_name, week_start_date) " +
            "VALUES (?, ?, ?, CURRENT_DATE)";

    private static final String SELECT_LATEST_SCHEDULE =
            "SELECT sp.schedule_id, sp.day_number, sp.day_name, " +
            "       p.project_id, p.title, p.deadline, p.revenue " +
            "FROM scheduled_projects sp " +
            "JOIN projects p ON sp.project_id = p.project_id " +
            "WHERE sp.week_start_date = (SELECT MAX(week_start_date) FROM scheduled_projects) " +
            "ORDER BY sp.day_number";

    private static final String DELETE_LATEST_SCHEDULE =
            "DELETE FROM scheduled_projects WHERE week_start_date = " +
            "(SELECT MAX(week_start_date) FROM scheduled_projects)";

    private static final String SELECT_ALL_SCHEDULES =
            "SELECT sp.schedule_id, sp.day_number, sp.day_name, sp.week_start_date, " +
            "       p.project_id, p.title, p.deadline, p.revenue " +
            "FROM scheduled_projects sp " +
            "JOIN projects p ON sp.project_id = p.project_id " +
            "ORDER BY sp.week_start_date DESC, sp.day_number";

    public void saveSchedule(List<ScheduledProject> schedule) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement del = conn.prepareStatement(DELETE_LATEST_SCHEDULE)) {
            del.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SCHEDULE)) {
            for (ScheduledProject sp : schedule) {
                ps.setString(1, sp.getProject().getProjectId());
                ps.setInt(2, sp.getDayNumber());
                ps.setString(3, sp.getDayName());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<ScheduledProject> getLatestSchedule() throws SQLException {
        List<ScheduledProject> schedule = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_LATEST_SCHEDULE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                schedule.add(mapRow(rs));
            }
        }
        return schedule;
    }

    public List<ScheduledProject> getAllSchedules() throws SQLException {
        List<ScheduledProject> schedule = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SCHEDULES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                schedule.add(mapRow(rs));
            }
        }
        return schedule;
    }

    private ScheduledProject mapRow(ResultSet rs) throws SQLException {
        Project p = new Project(
                rs.getString("project_id"),
                rs.getString("title"),
                rs.getInt("deadline"),
                rs.getDouble("revenue")
        );
        ScheduledProject sp = new ScheduledProject(p, rs.getInt("day_number"));
        sp.setScheduleId(rs.getInt("schedule_id"));
        sp.setDayName(rs.getString("day_name"));
        return sp;
    }
}