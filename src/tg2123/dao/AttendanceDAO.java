
package tg2123.dao;

import tg2123.config.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<String[]> getStudentsForAttendance(
            String courseCode,
            Date sessionDate,
            String sessionType, int toId) throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql =
                "SELECT DISTINCT ug.student_id, u.full_name, " +
                        "COALESCE(a.status, 'NOT_MARKED') AS attendance_status " +
                        "FROM enrollment e " +
                        "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                        "JOIN users u ON ug.user_id = u.user_id " +
                        "JOIN course c ON c.course_code = e.course_code " +
                        "JOIN technical_officer t ON t.to_id = ? AND t.department_id = c.department_id " +
                        "LEFT JOIN attendance a ON " +
                        "a.student_id = ug.student_id " +
                        "AND a.course_code = e.course_code " +
                        "AND a.session_date = ? " +
                        "AND a.session_type = ? " +
                        "WHERE e.course_code = ? " +
                        "ORDER BY u.full_name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, toId);
            statement.setDate(2, sessionDate);
            statement.setString(3, sessionType);
            statement.setString(4, courseCode);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    students.add(new String[]{
                            String.valueOf(rs.getInt("student_id")),
                            rs.getString("full_name"),
                            rs.getString("attendance_status")
                    });
                }
            }
        }

        return students;
    }

    public boolean saveAttendance(
            int studentId,
            String courseCode,
            Date sessionDate,
            String sessionType,
            String status,
            int toId) throws SQLException {

        if (!"PRESENT".equals(status) && !"ABSENT".equals(status)) {
            throw new IllegalArgumentException(
                    "Status must be PRESENT or ABSENT.");
        }

        if (!"THEORY".equals(sessionType)
                && !"PRACTICAL".equals(sessionType)) {
            throw new IllegalArgumentException(
                    "Session must be THEORY or PRACTICAL.");
        }

        String sql =
                "INSERT INTO attendance " +
                        "(student_id, course_code, session_date, session_type, status, to_id) " +
                        "SELECT ug.student_id, c.course_code, ?, ?, ?, ? " +
                        "FROM undergraduate ug " +
                        "JOIN enrollment e ON e.student_id = ug.student_id AND e.course_code = ? " +
                        "JOIN course c ON c.course_code = e.course_code " +
                        "JOIN technical_officer t ON t.to_id = ? AND t.department_id = c.department_id " +
                        "WHERE ug.student_id = ? " +
                        "ON DUPLICATE KEY UPDATE status = VALUES(status), to_id = VALUES(to_id)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, sessionDate);
            statement.setString(2, sessionType);
            statement.setString(3, status);
            statement.setInt(4, toId);
            statement.setString(5, courseCode);
            statement.setInt(6, toId);
            statement.setInt(7, studentId);
            return statement.executeUpdate() > 0;
        }
    }
}
