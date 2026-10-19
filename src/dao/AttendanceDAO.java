
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

    /**
     * Get students enrolled in a course and their attendance
     * for the specified session.
     */
    public List<String[]> getStudentsForAttendance(
            String courseCode,
            Date sessionDate,
            String sessionType) throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql = """
                SELECT
                    ug.student_id,
                    u.full_name,
                    COALESCE(a.status, 'NOT_MARKED') AS attendance_status
                FROM enrollment e
                JOIN undergraduate ug
                    ON e.student_id = ug.student_id
                JOIN users u
                    ON ug.user_id = u.user_id
                JOIN attendance_session s
                    ON s.course_code = e.course_code
                    AND s.session_date = ?
                    AND s.session_type = ?
                LEFT JOIN attendance a
                    ON a.session_id = s.session_id
                    AND a.student_id = ug.student_id
                WHERE e.course_code = ?
                ORDER BY u.full_name
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, sessionDate);
            ps.setString(2, sessionType);
            ps.setString(3, courseCode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new String[]{
                            rs.getString("student_id"),
                            rs.getString("full_name"),
                            rs.getString("attendance_status")
                    });
                }
            }
        }

        return students;
    }

    /**
     * Save attendance for a student in an existing session.
     * Updates the record if it exists; otherwise inserts it.
     */
    public boolean saveAttendance(
            String studentId,
            String courseCode,
            Date sessionDate,
            String sessionType,
            String status,
            int toId) throws SQLException {

        if (!"PRESENT".equals(status) && !"ABSENT".equals(status)) {
            throw new IllegalArgumentException(
                    "Attendance status must be PRESENT or ABSENT.");
        }

        String findSessionSql = """
                SELECT session_id
                FROM attendance_session
                WHERE course_code = ?
                  AND session_date = ?
                  AND session_type = ?
                """;

        String checkSql = """
                SELECT attendance_id
                FROM attendance
                WHERE session_id = ?
                  AND student_id = ?
                """;

        String updateSql = """
                UPDATE attendance
                SET status = ?
                WHERE session_id = ?
                  AND student_id = ?
                """;

        String insertSql = """
                INSERT INTO attendance (session_id, student_id, status)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection()) {

            // Find the existing session.
            int sessionId;

            try (PreparedStatement ps =
                         con.prepareStatement(findSessionSql)) {

                ps.setString(1, courseCode);
                ps.setDate(2, sessionDate);
                ps.setString(3, sessionType);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "No matching attendance session found. "
                                        + "Create the session before saving attendance.");
                    }

                    sessionId = rs.getInt("session_id");
                }
            }

            // Check whether attendance already exists.
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                ps.setInt(1, sessionId);
                ps.setString(2, studentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        // Update the existing attendance record.
                        try (PreparedStatement update =
                                     con.prepareStatement(updateSql)) {

                            update.setString(1, status);
                            update.setInt(2, sessionId);
                            update.setString(3, studentId);

                            return update.executeUpdate() > 0;
                        }
                    }
                }
            }

            // Insert attendance when no record exists.
            try (PreparedStatement insert =
                         con.prepareStatement(insertSql)) {

                insert.setInt(1, sessionId);
                insert.setString(2, studentId);
                insert.setString(3, status);

                return insert.executeUpdate() > 0;
            }
        }
    }
}
