package com.promanage.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IDGenerator {

    private static final String SEQUENCE_QUERY = "SELECT NEXTVAL('project_seq')";

    private IDGenerator() {}

    public static String generateProjectId() throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SEQUENCE_QUERY);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long seqVal = rs.getLong(1);
                return String.format("PROJ-%03d", seqVal);
            }
            throw new SQLException("Failed to fetch next sequence value.");
        }
    }
}