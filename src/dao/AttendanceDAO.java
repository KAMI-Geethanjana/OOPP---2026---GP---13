
package dao;

import config.DBConnection;

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
            String sessionType) throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql =
                "SELECT ug.student_id, u.full_name, " +
                        "COALESCE(a.status, 'NOT_MARKED') AS attendance_status " +
                        "FROM enrollment e " +
                        "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                        "JOIN users u ON ug.user_id = u.user_id " +
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

            statement.setDate(1, sessionDate);
            statement.setString(2, sessionType);
            statement.setString(3, courseCode);

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

        String officerCheckSql =
                "SELECT to_id FROM technical_officer WHERE to_id = ?";

        String studentCheckSql =
                "SELECT student_id FROM undergraduate WHERE student_id = ?";

        String courseCheckSql =
                "SELECT course_code FROM course WHERE course_code = ?";

        String attendanceCheckSql =
                "SELECT attendance_id FROM attendance " +
                        "WHERE student_id = ? " +
                        "AND course_code = ? " +
                        "AND session_date = ? " +
                        "AND session_type = ?";

        try (Connection connection = DBConnection.getConnection()) {

            // Verify the technical officer exists.
            try (PreparedStatement check =
                         connection.prepareStatement(officerCheckSql)) {

                check.setInt(1, toId);

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Invalid Technical Officer ID: " + toId +
                                        ". Check the logged-in officer's ID.");
                    }
                }
            }

            // Verify the student exists.
            try (PreparedStatement check =
                         connection.prepareStatement(studentCheckSql)) {

                check.setInt(1, studentId);

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Student ID " + studentId +
                                        " does not exist in undergraduate.");
                    }
                }
            }

            // Verify the course exists.
            try (PreparedStatement check =
                         connection.prepareStatement(courseCheckSql)) {

                check.setString(1, courseCode);

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Course code " + courseCode +
                                        " does not exist in course.");
                    }
                }
            }

            // Check whether attendance already exists.
            try (PreparedStatement check =
                         connection.prepareStatement(attendanceCheckSql)) {

                check.setInt(1, studentId);
                check.setString(2, courseCode);
                check.setDate(3, sessionDate);
                check.setString(4, sessionType);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {

                        int attendanceId = rs.getInt("attendance_id");

                        String updateSql =
                                "UPDATE attendance " +
                                        "SET status = ?, to_id = ? " +
                                        "WHERE attendance_id = ?";

                        try (PreparedStatement update =
                                     connection.prepareStatement(updateSql)) {

                            update.setString(1, status);
                            update.setInt(2, toId);
                            update.setInt(3, attendanceId);

                            return update.executeUpdate() > 0;
                        }
                    }
                }
            }

            // Insert attendance when no existing record is found.
            String insertSql =
                    "INSERT INTO attendance " +
                            "(student_id, course_code, session_date, " +
                            "session_type, status, to_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insert =
                         connection.prepareStatement(insertSql)) {

                insert.setInt(1, studentId);
                insert.setString(2, courseCode);
                insert.setDate(3, sessionDate);
                insert.setString(4, sessionType);
                insert.setString(5, status);
                insert.setInt(6, toId);

                return insert.executeUpdate() > 0;
            }
        }
    }
}
