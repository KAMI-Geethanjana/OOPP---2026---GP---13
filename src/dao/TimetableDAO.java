
package dao;

import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    // Get timetable records for one department
    public List<String[]> getTimetableByDepartment(
            int departmentId) throws SQLException {

        List<String[]> timetable = new ArrayList<>();

        String sql = """
                SELECT timetable_id, department_id, course_code,
                       day_of_week, start_time, end_time,
                       venue, session_type
                FROM timetable
                WHERE department_id = ?
                ORDER BY
                    FIELD(day_of_week,
                        'Monday', 'Tuesday', 'Wednesday',
                        'Thursday', 'Friday'),
                    start_time
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[8];

                    for (int i = 0; i < 8; i++) {
                        row[i] = rs.getString(i + 1);
                    }

                    timetable.add(row);
                }
            }
        }

        return timetable;
    }

    // Get all timetable records for testing or an overall timetable view
    public List<String[]> getAllTimetable() throws SQLException {

        List<String[]> timetable = new ArrayList<>();

        String sql = """
                SELECT timetable_id, department_id, course_code,
                       day_of_week, start_time, end_time,
                       venue, session_type
                FROM timetable
                ORDER BY
                    FIELD(day_of_week,
                        'Monday', 'Tuesday', 'Wednesday',
                        'Thursday', 'Friday'),
                    start_time
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] row = new String[8];

                for (int i = 0; i < 8; i++) {
                    row[i] = rs.getString(i + 1);
                }

                timetable.add(row);
            }
        }

        return timetable;
    }
}
