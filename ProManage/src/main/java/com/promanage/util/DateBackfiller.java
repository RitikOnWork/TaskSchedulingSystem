package com.promanage.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateBackfiller {

    public void backfillDates() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String selectSQL = "SELECT project_id, title FROM projects WHERE created_at >= CURRENT_DATE AND title LIKE 'Week%'";
        String updateSQL = "UPDATE projects SET created_at = ?, completion_date = ?, status = 'COMPLETED' WHERE project_id = ?";

        Pattern weekPattern = Pattern.compile("Week(\\d+)");
        LocalDate today = LocalDate.now();
        // Assuming Week 10 is current week
        int currentWeekNum = 10;

        System.out.println("  [DATA FIX] Starting date backfill based on 'WeekX' titles...");

        try (PreparedStatement select = conn.prepareStatement(selectSQL);
                PreparedStatement update = conn.prepareStatement(updateSQL);
                ResultSet rs = select.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                String id = rs.getString("project_id");
                String title = rs.getString("title");

                Matcher m = weekPattern.matcher(title);
                if (m.find()) {
                    int projectWeek = Integer.parseInt(m.group(1));
                    int weeksAgo = currentWeekNum - projectWeek;

                    // Week 10 = 0 weeks ago
                    // Week 1 = 9 weeks ago
                    LocalDate createdDate = today.minusWeeks(weeksAgo).minusDays(1); // Set to start of that week approx
                    LocalDate completionDate = createdDate.plusDays(3);

                    update.setObject(1, java.sql.Timestamp.valueOf(createdDate.atStartOfDay()));
                    update.setObject(2, java.sql.Date.valueOf(completionDate));
                    update.setString(3, id);
                    update.addBatch();
                    count++;
                }
            }
            update.executeBatch();
            System.out.println("  [DATA FIX] Updated dates for " + count + " projects.");
        }
    }
}
