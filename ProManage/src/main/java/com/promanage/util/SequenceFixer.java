package com.promanage.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SequenceFixer {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            System.out.println("Checking/Creating sequence 'project_seq'...");
            // Start at 101 since we seeded 100 projects
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS project_seq START 101");

            System.out.println("Sequence 'project_seq' ensured. Database ready.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
