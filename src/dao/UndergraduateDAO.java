package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UndergraduateDAO {
    public UndergraduateDAO getUndergraduateByUserId(int userId) {
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
            if (rs.next()){
                UndergraduateDAO ug = new UndergraduateDAO(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_no"),
                        rs.getString("student_id"),
                        rs.getString("reg_no"),
                        rs.getInt("batch_year"),
                        rs.getString("status"),
                        rs.getInt("department_id")
                );
                ug.setDepartmentName(rs.getString("department_name"));
                ug.setContactNo(rs.getString("contact_no"));
                return ug;



            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

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
        }

    }

}
