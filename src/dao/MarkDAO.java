package dao;

import config.DBConn;
import model.Mark;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    // 1. Add or Update Mark
    public boolean saveOrUpdateMark(Mark mark) {
        String query = "INSERT INTO mark (student_id, course_code, ca_marks, final_exam_marks, total_marks, eligible) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE ca_marks=?, final_exam_marks=?, total_marks=?, eligible=?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, mark.getStudentId());
            stmt.setString(2, mark.getCourseCode());
            stmt.setDouble(3, mark.getCaMark());
            stmt.setDouble(4, mark.getFinalExamMark());
            stmt.setDouble(5, mark.getTotalMark());
            stmt.setBoolean(6, mark.isEligible());

            // For update query
            stmt.setDouble(7, mark.getCaMark());
            stmt.setDouble(8, mark.getFinalExamMark());
            stmt.setDouble(9, mark.getTotalMark());
            stmt.setBoolean(10, mark.isEligible());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Get Marks for a Specific Student
    public List<Mark> getMarksByStudent(String studentId) {
        List<Mark> markList = new ArrayList<>();
        String query = "SELECT * FROM mark WHERE student_id = ?";

        try (Connection conn = DBConn.getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Mark mark = new Mark(
                        rs.getInt("mark_id"),
                        rs.getString("student_id"),
                        rs.getString("course_code"),
                        rs.getDouble("ca_marks"),
                        rs.getDouble("final_exam_marks"),
                        rs.getDouble("total_marks"),
                        rs.getBoolean("eligible")
                );
                markList.add(mark);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return markList;
    }
}