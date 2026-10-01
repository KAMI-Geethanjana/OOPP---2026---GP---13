package dao;

import config.DBConn;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<String> getCoursesByLecturer(int userId) {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT c.course_code FROM course c " +
                "JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                "WHERE l.user_id = ?";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                courses.add(rs.getString("course_code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}