package tg2082.dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Medical;
import common.model.Result;
import common.model.Timetable;
import tg2082.model.Undergraduate;
import tg2088.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UndergraduateDAO {

    public Undergraduate getUndergraduateByUserId(int userId) {
        String sql = "SELECT ug.student_id, ug.reg_no, ug.batch_year, ug.status, ug.department_id, ug.contact_no, " +
                "d.department_name, u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no, u.profile_pic " +
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
                ug.setProfilePic(rs.getString("profile_pic"));
                return ug;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves attendance summary per course for an undergraduate.
     * Computes raw attendance % and excused attendance % taking approved medicals into account.
     */
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

    /**
     * Gets student medical submission records.
     */
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

    /**
     * Submits a new medical certificate request.
     */
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

    /**
     * Gets enrolled courses for student.
     */
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

    /**
     * Gets course materials uploaded by lecturers for enrolled courses.
     */
    public List<CourseMaterial> getCourseMaterialsForStudent(int studentId) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT cm.* FROM enrollment e " +
                "JOIN course_material cm ON e.course_code = cm.course_code " +
                "WHERE e.student_id = ? ORDER BY cm.uploaded_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
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

    /**
     * Retrieves academic results, grades, and GPV for student.
     */
    public List<Result> getResultsForStudent(int studentId) {
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
                "WHERE e.student_id = ? ORDER BY e.course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
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

    /** Calculate credit-weighted GPA from published grades, excluding ungraded courses. */
    public double calculateGpa(int studentId) {
        String sql = "SELECT e.course_code, r.grade, r.grade_point, " +
                "(c.credit_theory + c.credit_practical) AS credits " +
                "FROM enrollment e JOIN course c ON e.course_code = c.course_code " +
                "JOIN result r ON r.enrollment_id = e.enrollment_id " +
                "WHERE e.student_id = ? AND r.grade IS NOT NULL " +
                "AND r.grade NOT IN ('-', 'N/A')";
        double weighted = 0;
        int credits = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int credit = rs.getInt("credits");
                    if (credit <= 0) continue;
                    weighted += rs.getDouble("grade_point") * credit;
                    credits += credit;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits == 0 ? 0.0 : weighted / credits;
    }

    /**
     * Gets timetable for student's enrolled courses.
     */
    public List<Timetable> getTimetableForStudent(int studentId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.* FROM enrollment e " +
                "JOIN timetable t ON e.course_code = t.course_code " +
                "WHERE e.student_id = ? " +
                "ORDER BY FIELD(t.day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday'), t.start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getInt("department_id"),
                        rs.getString("course_code"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("venue"),
                        rs.getString("session_type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Updates only undergraduate contact details, not academic information. */
    public boolean updateProfile(int userId, String contactNo, String email) {
        String sqlUser = "UPDATE users SET email = ?, phone_no = ? WHERE user_id = ?";
        String sqlUg = "UPDATE undergraduate SET contact_no = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser);
                 PreparedStatement psUg = conn.prepareStatement(sqlUg)) {
                psUser.setString(1, email);
                psUser.setString(2, contactNo);
                psUser.setInt(3, userId);
                if (psUser.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
                psUg.setString(1, contactNo);
                psUg.setInt(2, userId);
                if (psUg.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Saves the profile picture path in the existing users.profile_pic column. */
    public boolean updateProfilePicture(int userId, String picturePath) {
        String sql = "UPDATE users SET profile_pic = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, picturePath);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
