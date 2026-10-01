package dao;

import config.DBConn;
import model.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    // Course එකට අදාළ Results සහ Student ගේ නම ලබා ගැනීම
    public List<Result> getResultsByCourse(String courseCode) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, COALESCE(u.full_name, '-') AS full_name, e.course_code, " +
                "COALESCE(r.ca_mark, 0) AS ca_mark, COALESCE(r.final_exam_mark, 0) AS final_exam_mark, " +
                "COALESCE(r.total_mark, 0) AS total_mark, COALESCE(r.grade, '-') AS grade, " +
                "COALESCE(e.attempt_type, 'PROPER') AS attempt_type " +
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
                        rs.getString("student_id"),
                        rs.getString("full_name"), // දැන් නම හරියටම Load වෙයි
                        rs.getString("course_code"),
                        rs.getDouble("ca_mark"),
                        rs.getDouble("final_exam_mark"),
                        rs.getDouble("total_mark"),
                        rs.getString("grade"),
                        rs.getString("attempt_type")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Marks සහ Results Save/Update කිරීම
    public void saveOrUpdateMarks(int enrollmentId, double caMark, double finalExamMark, double totalMark, String grade) {
        String resultSql = "INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, grade) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE ca_mark = VALUES(ca_mark), final_exam_mark = VALUES(final_exam_mark), " +
                "total_mark = VALUES(total_mark), grade = VALUES(grade)";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(resultSql)) {

            ps.setInt(1, enrollmentId);
            ps.setDouble(2, caMark);
            ps.setDouble(3, finalExamMark);
            ps.setDouble(4, totalMark);
            ps.setString(5, grade);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}