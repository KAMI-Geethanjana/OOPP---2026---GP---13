package dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Medical;
import common.model.Result;
import common.model.Timetable;
import model.Undergraduate;
import model.Undergraduate;
import model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndergrduateDAO {

    public Undergraduate getUndergraduateByUserId(int userId) {
        String sql = "SELECT ug.student_id, ug.reg_no, ug.batch_year, ug.status, ug.department_id, ug.contact_no, " +
                "d.department_name, u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no " +
                "FROM undergraduate ug " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN department d ON ug.department_id = d.department_id " +
                "WHERE ug.user_id = ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Undergraduate ug = new Undergraduate(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_no"),
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getInt("batch_year"),
                        rs.getString("status"),
                        rs.getInt("department_id")
                );
                ug.setDepartmentName(rs.getString("department_name"));
                ug.setContactNo(rs.getString("contact_no"));
                return ug;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Map<String, Object>> getAttendanceSummary(int studentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT e.course_code, c.course_title, " +
                "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, " +
                "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count, " +
                "COALESCE(m.med_count, 0) AS approved_medicals " +
                "FROM enrollment e " +
                "JOIN course c ON e.course_code = c.course_code " +
                "LEFT JOIN attendance a ON a.student_id = e.student_id AND a.course_code = e.course_code " +
                "LEFT JOIN (SELECT student_id, course_code, COUNT(*) AS med_count FROM medical WHERE status = 'APPROVED' GROUP BY student_id, course_code) m " +
                "  ON m.student_id = e.student_id AND m.course_code = e.course_code " +
                "WHERE e.student_id = ? " +
                "GROUP BY e.course_code, c.course_title, m.med_count " +
                "ORDER BY e.course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("courseCode", rs.getString("course_code"));
                map.put("courseTitle", rs.getString("course_title"));

                int present = rs.getInt("present_count");
                int absent = rs.getInt("absent_count");
                int approvedMeds = rs.getInt("approved_medicals");
                int totalSessions = 15; // Per brief: 15 sessions per component

                double rawPercentage = (present * 100.0) / totalSessions;
                double excusedPercentage = ((present + approvedMeds) * 100.0) / totalSessions;
                if (excusedPercentage > 100.0) excusedPercentage = 100.0;

                map.put("presentCount", present);
                map.put("totalSessions", totalSessions);
                map.put("approvedMedicals", approvedMeds);
                map.put("rawPercentage", Math.round(rawPercentage * 10.0) / 10.0);
                map.put("excusedPercentage", Math.round(excusedPercentage * 10.0) / 10.0);
                map.put("isEligible", excusedPercentage >= 80.0);

                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Medical> getMedicalRecords(int studentId) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE student_id = ? ORDER BY medical_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Medical(
                        rs.getInt("medical_id"),
                        rs.getInt("student_id"),
                        rs.getString("course_code"),
                        rs.getDate("medical_date"),
                        rs.getString("reason"),
                        rs.getString("document_path"),
                        rs.getString("status"),
                        (Integer) rs.getObject("reviewed_by_to_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean submitMedical(Medical m) {
        String sql = "INSERT INTO medical (student_id, course_code, medical_date, reason, document_path, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, m.getStudentId());
            ps.setString(2, m.getCourseCode());
            ps.setDate(3, m.getMedicalDate());
            ps.setString(4, m.getReason());
            ps.setString(5, m.getDocumentPath());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Course> getEnrolledCourses(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS lecturer_name FROM enrollment e " +
                "JOIN course c ON e.course_code = c.course_code " +
                "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id " +
                "WHERE e.student_id = ? ORDER BY c.course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("credit_theory"),
                        rs.getInt("credit_practical"),
                        rs.getInt("department_id"),
                        (Integer) rs.getObject("lecturer_id")
                );
                c.setLecturerName(rs.getString("lecturer_name"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}

