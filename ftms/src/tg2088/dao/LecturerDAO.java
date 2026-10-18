package tg2088.dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Result;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class LecturerDAO {

    public Lecturer getLecturerByUserId(int userId) {
        String sql = "SELECT l.lecturer_id, l.employee_id, l.department_id, d.department_name, " +
                     "u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no " +
                     "FROM lecturer l " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "JOIN department d ON l.department_id = d.department_id " +
                     "WHERE l.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Lecturer lec = new Lecturer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_no"),
                        rs.getInt("lecturer_id"),
                        rs.getString("employee_id"),
                        rs.getInt("department_id")
                );
                lec.setDepartmentName(rs.getString("department_name"));
                return lec;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Course> getCoursesByLecturer(int lecturerId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE lecturer_id = ? ORDER BY course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("credit_theory"),
                        rs.getInt("credit_practical"),
                        rs.getInt("department_id"),
                        rs.getInt("lecturer_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Result> getResultsByCourse(String courseCode) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, ug.reg_no, u.full_name, e.course_code, " +
                     "e.attempt_type, " +
                     "COALESCE(r.result_id, 0) AS result_id, " +
                     "COALESCE(r.ca_mark, 0.0) AS ca_mark, " +
                     "COALESCE(r.final_exam_mark, 0.0) AS final_exam_mark, " +
                     "COALESCE(r.total_mark, 0.0) AS total_mark, " +
                     "COALESCE(r.eligibility_status, 'ELIGIBLE') AS eligibility_status, " +
                     "COALESCE(r.grade, '-') AS grade, " +
                     "COALESCE(r.grade_point, 0.0) AS grade_point " +
                     "FROM enrollment e " +
                     "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                     "JOIN users u ON ug.user_id = u.user_id " +
                     "LEFT JOIN result r ON e.enrollment_id = r.enrollment_id " +
                     "WHERE e.course_code = ? " +
                     "ORDER BY ug.reg_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Result(
                        rs.getInt("result_id"),
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("full_name"),
                        rs.getString("course_code"),
                        rs.getString("attempt_type"),
                        rs.getDouble("ca_mark"),
                        rs.getDouble("final_exam_mark"),
                        rs.getDouble("total_mark"),
                        rs.getString("eligibility_status"),
                        rs.getString("grade"),
                        rs.getDouble("grade_point")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                                      double totalMark, String eligibilityStatus, String grade, double gradePoint) {
        String sql = "INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "ca_mark = VALUES(ca_mark), " +
                     "final_exam_mark = VALUES(final_exam_mark), " +
                     "total_mark = VALUES(total_mark), " +
                     "eligibility_status = VALUES(eligibility_status), " +
                     "grade = VALUES(grade), " +
                     "grade_point = VALUES(grade_point)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDouble(2, caMark);
            ps.setDouble(3, finalExamMark);
            ps.setDouble(4, totalMark);
            ps.setString(5, eligibilityStatus);
            ps.setString(6, grade);
            ps.setDouble(7, gradePoint);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<CourseMaterial> getCourseMaterials(String courseCode) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM course_material WHERE course_code = ? ORDER BY uploaded_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CourseMaterial(
                        rs.getInt("material_id"),
                        rs.getString("course_code"),
                        rs.getInt("lecturer_id"),
                        rs.getString("title"),
                        rs.getString("file_path"),
                        rs.getDate("uploaded_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean addCourseMaterial(CourseMaterial material) {
        String sql = "INSERT INTO course_material (course_code, lecturer_id, title, file_path, uploaded_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, material.getCourseCode());
            ps.setInt(2, material.getLecturerId());
            ps.setString(3, material.getTitle());
            ps.setString(4, material.getFilePath());
            ps.setDate(5, material.getUploadedDate());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteCourseMaterial(int materialId) {
        String sql = "DELETE FROM course_material WHERE material_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Map<String, Object>> getStudentAttendanceSummary(String courseCode) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT ug.student_id, ug.reg_no, u.full_name, " +
                     "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS attended_count, " +
                     "COUNT(a.attendance_id) AS total_marked, " +
                     "COALESCE(m.med_count, 0) AS approved_medicals " +
                     "FROM enrollment e " +
                     "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                     "JOIN users u ON ug.user_id = u.user_id " +
                     "LEFT JOIN attendance a ON a.student_id = ug.student_id AND a.course_code = e.course_code " +
                     "LEFT JOIN (SELECT student_id, course_code, COUNT(*) AS med_count FROM medical WHERE status = 'APPROVED' GROUP BY student_id, course_code) m " +
                     "  ON m.student_id = ug.student_id AND m.course_code = e.course_code " +
                     "WHERE e.course_code = ? " +
                     "GROUP BY ug.student_id, ug.reg_no, u.full_name, m.med_count " +
                     "ORDER BY ug.reg_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("student_id"));
                row.put("regNo", rs.getString("reg_no"));
                row.put("fullName", rs.getString("full_name"));
                
                int attended = rs.getInt("attended_count");
                int approvedMeds = rs.getInt("approved_medicals");
                // Per SRS: 15 sessions per component
                int totalSessions = 15;
                double rawPercentage = (attended * 100.0) / totalSessions;
                // Excused percentage treating approved medicals as excused
                double excusedPercentage = ((attended + approvedMeds) * 100.0) / totalSessions;
                if (excusedPercentage > 100.0) excusedPercentage = 100.0;

                row.put("attendedCount", attended);
                row.put("totalSessions", totalSessions);
                row.put("approvedMedicals", approvedMeds);
                row.put("rawPercentage", Math.round(rawPercentage * 10.0) / 10.0);
                row.put("excusedPercentage", Math.round(excusedPercentage * 10.0) / 10.0);
                row.put("isEligible", excusedPercentage >= 80.0);

                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean updateProfile(int userId, String fullName, String email, String phoneNo) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_no = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phoneNo);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
