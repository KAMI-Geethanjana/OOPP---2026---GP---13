
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
            String courseCode, Date sessionDate, String sessionType)
            throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql = """
                SELECT ug.undergraduate_id, u.full_name,
                       COALESCE(a.status, 'PRESENT') AS attendance_status
                FROM enrollment e
                JOIN undergraduate ug
                    ON e.undergraduate_id = ug.undergraduate_id
                JOIN users u ON ug.user_id = u.user_id
                LEFT JOIN attendance a
                    ON a.student_id = ug.undergraduate_id
                    AND a.course_code = ?
                    AND a.session_date = ?
                    AND a.session_type = ?
                WHERE e.course_code = ?
                ORDER BY u.full_name
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ps.setDate(2, sessionDate);
            ps.setString(3, sessionType);
            ps.setString(4, courseCode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new String[]{
                            rs.getString("undergraduate_id"),
                            rs.getString("full_name"),
                            rs.getString("attendance_status")
                    });
                }
            }
        }

        return students;
    }

    public boolean saveAttendance(
            int studentId, String courseCode,
            Date sessionDate, String sessionType,
            String status, int toId) throws SQLException {

        String checkSql = """
                SELECT attendance_id
                FROM attendance
                WHERE student_id = ?
                  AND course_code = ?
                  AND session_date = ?
                  AND session_type = ?
                """;

        String insertSql = """
                INSERT INTO attendance
                    (student_id, course_code, session_date,
                     session_type, status, recorded_by)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String updateSql = """
                UPDATE attendance
                SET status = ?, recorded_by = ?
                WHERE student_id = ?
                  AND course_code = ?
                  AND session_date = ?
                  AND session_type = ?
                """;

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement check =
                         con.prepareStatement(checkSql)) {

                check.setInt(1, studentId);
                check.setString(2, courseCode);
                check.setDate(3, sessionDate);
                check.setString(4, sessionType);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement update =
                                     con.prepareStatement(updateSql)) {

                            update.setString(1, status);
                            update.setInt(2, toId);
                            update.setInt(3, studentId);
                            update.setString(4, courseCode);
                            update.setDate(5, sessionDate);
                            update.setString(6, sessionType);

                            return update.executeUpdate() > 0;
                        }
                    }
                }
            }

            try (PreparedStatement insert =
                         con.prepareStatement(insertSql)) {

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
