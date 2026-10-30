package tg2123.dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Medical;
import common.model.Timetable;
import tg2123.model.TechnicalOfficer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for the Technical Officer module.
 * Keeps SQL/database code separate from the Swing user interface.
 */
public class TechnicalOfficerDAO implements TechnicalOfficerRepository {

    public TechnicalOfficer getTechnicalOfficerByUserId(int userId) {
        String sql = "SELECT t.to_id, t.employee_id, t.department_id, d.department_name, " +
                "u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no, " +
                "u.profile_pic, u.is_active, u.failed_logins " +
                "FROM technical_officer t " +
                "JOIN users u ON t.user_id = u.user_id " +
                "JOIN department d ON t.department_id = d.department_id " +
                "WHERE t.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                TechnicalOfficer officer = new TechnicalOfficer(
                        rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("password_hash"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone_no"),
                        rs.getInt("to_id"), rs.getString("employee_id"),
                        rs.getInt("department_id"));
                officer.setDepartmentName(rs.getString("department_name"));
                officer.setProfilePic(rs.getString("profile_pic"));
                officer.setActive(rs.getBoolean("is_active"));
                officer.setFailedLogins(rs.getInt("failed_logins"));
                return officer;
            }
        } catch (SQLException ex) {
            reportDatabaseError("load Technical Officer profile", ex);
            return null;
        }
    }

    public List<Course> getCoursesByDepartment(int departmentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_code, course_title, credit_theory, credit_practical, " +
                "department_id, lecturer_id FROM course WHERE department_id = ? ORDER BY course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object lecturerValue = rs.getObject("lecturer_id");
                    Integer lecturerId = lecturerValue == null ? null : ((Number) lecturerValue).intValue();
                    courses.add(new Course(rs.getString("course_code"), rs.getString("course_title"),
                            rs.getInt("credit_theory"), rs.getInt("credit_practical"),
                            rs.getInt("department_id"), lecturerId));
                }
            }
        } catch (SQLException ex) {
            reportDatabaseError("load departmental courses", ex);
        }
        return courses;
    }

    /** Loads students enrolled in a course belonging to this officer's department. */
    public List<Map<String, Object>> getStudentsForAttendance(String courseCode, Date sessionDate,
                                                              String sessionType, int departmentId) {
        List<Map<String, Object>> students = new ArrayList<>();
        if (courseCode == null || sessionDate == null || !isSessionType(sessionType)) return students;

        String sql = "SELECT DISTINCT ug.student_id, ug.reg_no, u.full_name, " +
                "COALESCE(a.status, 'NOT_MARKED') AS status " +
                "FROM enrollment e " +
                "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN course c ON c.course_code = e.course_code " +
                "LEFT JOIN attendance a ON a.student_id = ug.student_id " +
                " AND a.course_code = e.course_code AND a.session_date = ? AND a.session_type = ? " +
                "WHERE e.course_code = ? AND c.department_id = ? " +
                "ORDER BY ug.reg_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, sessionDate);
            ps.setString(2, sessionType);
            ps.setString(3, courseCode);
            ps.setInt(4, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("fullName", rs.getString("full_name"));
                    row.put("status", rs.getString("status"));
                    students.add(row);
                }
            }
        } catch (SQLException ex) {
            reportDatabaseError("load attendance list", ex);
        }
        return students;
    }

    /**
     * Inserts or updates one attendance record. The SQL verifies that the officer and
     * course belong to the same department and that the student is enrolled in the course.
     */
    public boolean saveAttendance(int studentId, String courseCode, Date sessionDate,
                                  String sessionType, String status, int toId) {
        if (studentId <= 0 || toId <= 0 || courseCode == null || sessionDate == null
                || !isSessionType(sessionType) || !isAttendanceStatus(status)) {
            return false;
        }

        String sql = "INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) " +
                "SELECT ?, c.course_code, ?, ?, ?, ? FROM course c " +
                "JOIN technical_officer t ON t.department_id = c.department_id " +
                "WHERE c.course_code = ? AND t.to_id = ? " +
                "AND EXISTS (SELECT 1 FROM enrollment e WHERE e.student_id = ? AND e.course_code = c.course_code) " +
                "ON DUPLICATE KEY UPDATE status = VALUES(status), to_id = VALUES(to_id)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, sessionDate);
            ps.setString(3, sessionType);
            ps.setString(4, status);
            ps.setInt(5, toId);
            ps.setString(6, courseCode);
            ps.setInt(7, toId);
            ps.setInt(8, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            reportDatabaseError("save attendance", ex);
            return false;
        }
    }

    /** Returns medical requests only for courses in the officer's own department. */
    public List<Medical> getMedicalRequestsByDepartment(int departmentId) {
        List<Medical> medicals = new ArrayList<>();
        String sql = "SELECT m.*, ug.reg_no, u.full_name FROM medical m " +
                "JOIN undergraduate ug ON m.student_id = ug.student_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN course c ON c.course_code = m.course_code " +
                "WHERE c.department_id = ? ORDER BY m.medical_date DESC, m.medical_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medical medical = new Medical(rs.getInt("medical_id"), rs.getInt("student_id"),
                            rs.getString("course_code"), rs.getDate("medical_date"), rs.getString("reason"),
                            rs.getString("document_path"), rs.getString("status"),
                            (Integer) rs.getObject("reviewed_by_to_id"));
                    medical.setStudentRegNo(rs.getString("reg_no"));
                    medical.setStudentName(rs.getString("full_name"));
                    medicals.add(medical);
                }
            }
        } catch (SQLException ex) {
            reportDatabaseError("load medical requests", ex);
        }
        return medicals;
    }

    /** Changes only a pending request belonging to this officer's department. */
    public boolean updateMedicalStatus(int medicalId, String newStatus, int toId, int departmentId) {
        if (medicalId <= 0 || toId <= 0 || !isMedicalStatus(newStatus)) return false;
        String sql = "UPDATE medical m JOIN course c ON c.course_code = m.course_code " +
                "JOIN technical_officer t ON t.department_id = c.department_id " +
                "SET m.status = ?, m.reviewed_by_to_id = ? " +
                "WHERE m.medical_id = ? AND c.department_id = ? AND t.to_id = ? AND m.status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, toId);
            ps.setInt(3, medicalId);
            ps.setInt(4, departmentId);
            ps.setInt(5, toId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            reportDatabaseError("update medical request", ex);
            return false;
        }
    }

    public List<Timetable> getTimetableByDepartment(int departmentId) {
        List<Timetable> timetable = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE department_id = ? " +
                "ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday'), start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    timetable.add(new Timetable(rs.getInt("timetable_id"), rs.getInt("department_id"),
                            rs.getString("course_code"), rs.getString("day_of_week"),
                            rs.getTime("start_time"), rs.getTime("end_time"),
                            rs.getString("venue"), rs.getString("session_type")));
                }
            }
        } catch (SQLException ex) {
            reportDatabaseError("load departmental timetable", ex);
        }
        return timetable;
    }

    /** Updates permitted profile fields only; username and password are not changed here. */
    public boolean updateProfile(int userId, String fullName, String email, String phoneNo) {
        if (userId <= 0 || fullName == null || fullName.trim().isEmpty()) return false;
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_no = ? " +
                "WHERE user_id = ? AND role = 'TECH_OFFICER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, blankToNull(email));
            ps.setString(3, blankToNull(phoneNo));
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            reportDatabaseError("update Technical Officer profile", ex);
            return false;
        }
    }

    public boolean updateProfilePhoto(int userId, String photoPath) {
        if (userId <= 0 || photoPath == null || photoPath.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE users SET profile_pic = ? " +
                "WHERE user_id = ? AND role = 'TECH_OFFICER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, photoPath);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            reportDatabaseError("update profile photo", ex);
            return false;
        }
    }

    private boolean isSessionType(String value) {
        return "THEORY".equals(value) || "PRACTICAL".equals(value);
    }

    private boolean isAttendanceStatus(String value) {
        return "PRESENT".equals(value) || "ABSENT".equals(value);
    }

    private boolean isMedicalStatus(String value) {
        return "APPROVED".equals(value) || "REJECTED".equals(value);
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void reportDatabaseError(String action, SQLException exception) {
        System.err.println("Could not " + action + ": " + exception.getMessage());
    }
}
