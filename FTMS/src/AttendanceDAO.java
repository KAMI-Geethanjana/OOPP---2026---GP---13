import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // =========================================================
    // 1. CREATE ATTENDANCE SESSION
    // =========================================================

    public int createSession(AttendanceSession session) {

        String sql = "INSERT INTO attendance_session " +
                "(course_code, session_date, session_type, session_number) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, session.getCourseCode());
            stmt.setString(2, session.getSessionDate());
            stmt.setString(3, session.getSessionType());
            stmt.setInt(4, session.getSessionNumber());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {
                    int sessionId = rs.getInt(1);
                    session.setSessionId(sessionId);
                    return sessionId;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error creating attendance session.");
            e.printStackTrace();
        }

        return -1;
    }


    // =========================================================
    // 2. GET SESSION BY ID
    // =========================================================

    public AttendanceSession getSessionById(int sessionId) {

        String sql = "SELECT session_id, course_code, session_date, " +
                "session_type, session_number " +
                "FROM attendance_session " +
                "WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    AttendanceSession session = new AttendanceSession();

                    session.setSessionId(rs.getInt("session_id"));
                    session.setCourseCode(rs.getString("course_code"));
                    session.setSessionDate(rs.getString("session_date"));
                    session.setSessionType(rs.getString("session_type"));
                    session.setSessionNumber(rs.getInt("session_number"));

                    return session;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving attendance session.");
            e.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // 3. GET SESSIONS FOR A COURSE
    // =========================================================

    public List<AttendanceSession> getSessionsByCourse(String courseCode) {

        List<AttendanceSession> sessions = new ArrayList<>();

        String sql = "SELECT session_id, course_code, session_date, " +
                "session_type, session_number " +
                "FROM attendance_session " +
                "WHERE course_code = ? " +
                "ORDER BY session_type, session_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    AttendanceSession session = new AttendanceSession();

                    session.setSessionId(rs.getInt("session_id"));
                    session.setCourseCode(rs.getString("course_code"));
                    session.setSessionDate(rs.getString("session_date"));
                    session.setSessionType(rs.getString("session_type"));
                    session.setSessionNumber(rs.getInt("session_number"));

                    sessions.add(session);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving course sessions.");
            e.printStackTrace();
        }

        return sessions;
    }


    // =========================================================
    // 4. SAVE ONE STUDENT'S ATTENDANCE
    // =========================================================

    public boolean saveAttendance(Attendance attendance) {

        String sql = "INSERT INTO attendance " +
                "(session_id, student_id, status) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attendance.getSessionId());
            stmt.setString(2, attendance.getStudentId());
            stmt.setString(3, attendance.getStatus());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error saving attendance.");
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // 5. SAVE MULTIPLE STUDENTS' ATTENDANCE
    // =========================================================

    public boolean saveAttendanceList(List<Attendance> attendanceList) {

        String sql = "INSERT INTO attendance " +
                "(session_id, student_id, status) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Attendance attendance : attendanceList) {

                stmt.setInt(1, attendance.getSessionId());
                stmt.setString(2, attendance.getStudentId());
                stmt.setString(3, attendance.getStatus());

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

            return true;

        } catch (SQLException e) {

            System.out.println("Error saving attendance list.");
            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // 6. UPDATE ATTENDANCE
    // =========================================================

    public boolean updateAttendance(Attendance attendance) {

        String sql = "UPDATE attendance " +
                "SET status = ? " +
                "WHERE attendance_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, attendance.getStatus());
            stmt.setInt(2, attendance.getAttendanceId());

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating attendance.");
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // 7. GET ATTENDANCE FOR A SESSION
    // =========================================================

    public List<Attendance> getAttendanceBySession(int sessionId) {

        List<Attendance> attendanceList = new ArrayList<>();

        String sql = "SELECT attendance_id, session_id, student_id, status " +
                "FROM attendance " +
                "WHERE session_id = ? " +
                "ORDER BY student_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Attendance attendance = new Attendance();

                    attendance.setAttendanceId(
                            rs.getInt("attendance_id")
                    );

                    attendance.setSessionId(
                            rs.getInt("session_id")
                    );

                    attendance.setStudentId(
                            rs.getString("student_id")
                    );

                    attendance.setStatus(
                            rs.getString("status")
                    );

                    attendanceList.add(attendance);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving session attendance.");
            e.printStackTrace();
        }

        return attendanceList;
    }


    // =========================================================
    // 8. GET ONE STUDENT'S ATTENDANCE RECORDS
    // =========================================================

    public List<Attendance> getAttendanceByStudent(String studentId) {

        List<Attendance> attendanceList = new ArrayList<>();

        String sql = "SELECT a.attendance_id, a.session_id, " +
                "a.student_id, a.status " +
                "FROM attendance a " +
                "WHERE a.student_id = ? " +
                "ORDER BY a.session_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Attendance attendance = new Attendance();

                    attendance.setAttendanceId(
                            rs.getInt("attendance_id")
                    );

                    attendance.setSessionId(
                            rs.getInt("session_id")
                    );

                    attendance.setStudentId(
                            rs.getString("student_id")
                    );

                    attendance.setStatus(
                            rs.getString("status")
                    );

                    attendanceList.add(attendance);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving student attendance.");
            e.printStackTrace();
        }

        return attendanceList;
    }


    // =========================================================
    // 9. GET ATTENDANCE PERCENTAGE FOR ONE STUDENT
    // =========================================================

    public double getAttendancePercentage(
            String studentId,
            String courseCode,
            String sessionType) {

        String sql =
                "SELECT " +
                        "COUNT(*) AS total_sessions, " +
                        "SUM(a.status = 'PRESENT') AS present_sessions " +
                        "FROM attendance a " +
                        "JOIN attendance_session s " +
                        "ON a.session_id = s.session_id " +
                        "WHERE a.student_id = ? " +
                        "AND s.course_code = ? " +
                        "AND s.session_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            stmt.setString(3, sessionType);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    int totalSessions =
                            rs.getInt("total_sessions");

                    int presentSessions =
                            rs.getInt("present_sessions");

                    if (totalSessions == 0) {
                        return 0.0;
                    }

                    return ((double) presentSessions
                            / totalSessions) * 100;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating attendance percentage.");
            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // 10. GET COMBINED ATTENDANCE PERCENTAGE
    // =========================================================

    public double getCombinedAttendancePercentage(
            String studentId,
            String courseCode) {

        String sql =
                "SELECT " +
                        "COUNT(*) AS total_sessions, " +
                        "SUM(a.status = 'PRESENT') AS present_sessions " +
                        "FROM attendance a " +
                        "JOIN attendance_session s " +
                        "ON a.session_id = s.session_id " +
                        "WHERE a.student_id = ? " +
                        "AND s.course_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    int totalSessions =
                            rs.getInt("total_sessions");

                    int presentSessions =
                            rs.getInt("present_sessions");

                    if (totalSessions == 0) {
                        return 0.0;
                    }

                    return ((double) presentSessions
                            / totalSessions) * 100;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating combined attendance.");
            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // 11. GET ATTENDANCE SUMMARY FOR WHOLE BATCH
    // =========================================================

    public ResultSet getBatchAttendanceSummary(String courseCode) {

        String sql =
                "SELECT " +
                        "a.student_id, " +
                        "COUNT(*) AS total_sessions, " +
                        "SUM(a.status = 'PRESENT') AS present_sessions, " +
                        "ROUND(" +
                        "SUM(a.status = 'PRESENT') / COUNT(*) * 100, 2" +
                        ") AS attendance_percentage " +
                        "FROM attendance a " +
                        "JOIN attendance_session s " +
                        "ON a.session_id = s.session_id " +
                        "WHERE s.course_code = ? " +
                        "GROUP BY a.student_id " +
                        "ORDER BY a.student_id";

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt =
                    conn.prepareStatement(sql);

            stmt.setString(1, courseCode);

            return stmt.executeQuery();

        } catch (SQLException e) {
            System.out.println("Error retrieving batch attendance.");
            e.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // 12. GET ATTENDANCE STATUS
    // =========================================================

    public String getAttendanceStatus(
            String studentId,
            String courseCode) {

        double percentage =
                getCombinedAttendancePercentage(
                        studentId,
                        courseCode
                );

        if (percentage > 80) {
            return "MORE_THAN_80";

        } else if (percentage == 80) {
            return "EXACTLY_80";

        } else {
            return "LESS_THAN_80";
        }
    }
}