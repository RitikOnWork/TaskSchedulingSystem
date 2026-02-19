package com.promanage.util;

import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdater {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // 1. Drop the constraint on 'projects'
            // Postgres default name usually: projects_deadline_check
            // We try to drop it.
            try {
                System.out.println("Dropping constraint projects_deadline_check...");
                stmt.execute("ALTER TABLE projects DROP CONSTRAINT projects_deadline_check");
                System.out.println("Constraint dropped.");
            } catch (Exception e) {
                System.out.println("Could not drop constraint (might not exist or different name): " + e.getMessage());
            }

            // 2. Drop the constraint on 'scheduled_projects' (if any) - originally checked
            // day_number <= 5
            try {
                System.out.println("Dropping constraint scheduled_projects_day_number_check...");
                stmt.execute("ALTER TABLE scheduled_projects DROP CONSTRAINT scheduled_projects_day_number_check");
                System.out.println("Constraint dropped.");
            } catch (Exception e) {
                System.out.println("Could not drop constraint (might not exist or different name): " + e.getMessage());
            }

            // 3. Add new flexible constraint? Actually user said "Any number", so >= 1 is
            // enough.
            // We already defined it in schema.sql as >= 1.
            // Let's add that back to be safe, or just leave it.
            try {
                stmt.execute("ALTER TABLE projects ADD CONSTRAINT projects_deadline_check CHECK (deadline >= 1)");
                System.out.println("New constraint added to projects.");
            } catch (Exception e) {
                System.out.println("Info: " + e.getMessage());
            }

            System.out.println("Schema update complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
