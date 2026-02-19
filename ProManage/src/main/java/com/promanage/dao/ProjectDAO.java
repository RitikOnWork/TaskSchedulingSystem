package com.promanage.dao;

import com.promanage.model.Project;
import com.promanage.util.DBConnection;
import com.promanage.util.IDGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    private static final String INSERT_PROJECT = "INSERT INTO projects (project_id, title, deadline, revenue, status) VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL = "SELECT project_id, title, deadline, revenue, status FROM projects ORDER BY created_at";

    private static final String SELECT_BY_ID = "SELECT project_id, title, deadline, revenue, status FROM projects WHERE project_id = ?";

    private static final String COUNT_PROJECTS = "SELECT COUNT(*) FROM projects";

    public Project addProject(String title, int deadline, double revenue) throws SQLException {
        String id = IDGenerator.generateProjectId();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT_PROJECT)) {
            ps.setString(1, id);
            ps.setString(2, title);
            ps.setInt(3, deadline);
            ps.setDouble(4, revenue);
            ps.setString(5, "PENDING");
            ps.executeUpdate();
        }
        return new Project(id, title, deadline, revenue, "PENDING");
    }

    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(mapRow(rs));
            }
        }
        return projects;
    }

    public Project getProjectById(String projectId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        }
        return null;
    }

    public int getTotalProjectCount() throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(COUNT_PROJECTS);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        return new Project(
                rs.getString("project_id"),
                rs.getString("title"),
                rs.getInt("deadline"),
                rs.getDouble("revenue"),
                rs.getString("status"));
    }
}