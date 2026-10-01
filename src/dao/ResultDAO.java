package dao;

import config.DBConn;
import model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    // Course Code එකට අදාළව සියලුම සිසුන්ගේ ලකුණු ලබා ගැනීම
    public List<Result> getResultsByCourse(String courseCode) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id AS index_no, u.full_name, e.course_code, " +
                "r.ca_mark AS ca_marks, r.final_exam_mark AS final_marks, r.total_mark AS total_marks, r.grade " +
                "FROM enrollment e " +
                "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "LEFT JOIN result r ON e.enrollment_id = r.enrollment_id " +
                "WHERE e.course_code = ?";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Result(
                        rs.getInt("enrollment_id"),
                        rs.getString("index_no"),
                        rs.getString("full_name"),
                        rs.getString("course_code"),
                        rs.getDouble("ca_marks"),
                        rs.getDouble("final_marks"),
                        rs.getDouble("total_marks"),
                        rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // සිසුවෙකුගේ Marks Database එකට ඇතුළත් කිරීම හෝ Update කිරීම
    public boolean saveOrUpdateMarks(int enrollmentId, double caMarks, double finalMarks, double totalMarks, String grade) {
        String sql = "INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, grade) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "ca_mark = VALUES(ca_mark), " +
                "final_exam_mark = VALUES(final_exam_mark), " +
                "total_mark = VALUES(total_mark), " +
                "grade = VALUES(grade)";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDouble(2, caMarks);
            ps.setDouble(3, finalMarks);
            ps.setDouble(4, totalMarks);
            ps.setString(5, grade);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}