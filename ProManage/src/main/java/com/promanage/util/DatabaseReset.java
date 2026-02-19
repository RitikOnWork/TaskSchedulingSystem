package com.promanage.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseReset {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            System.out.println("Reading schema.sql...");
            String content = Files.readString(Path.of("src/main/resources/schema.sql"));

            // simple parser: remove lines starting with --, then split by ;
            StringBuilder cleanSql = new StringBuilder();
            for (String line : content.split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.startsWith("--") && !trimmed.isEmpty()) {
                    cleanSql.append(line).append("\n");
                }
            }

            String[] commands = cleanSql.toString().split(";");
            System.out.println("Found " + commands.length + " commands.");

            for (String cmd : commands) {
                if (!cmd.trim().isEmpty()) {
                    try {
                        stmt.execute(cmd.trim());
                    } catch (SQLException e) {
                        System.err.println("Error executing: " + cmd.substring(0, Math.min(30, cmd.length())) + "...");
                        // Don't print stack trace for "table does not exist" on drop
                        if (!e.getMessage().contains("does not exist")) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            System.out.println("Database Reset Complete.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
