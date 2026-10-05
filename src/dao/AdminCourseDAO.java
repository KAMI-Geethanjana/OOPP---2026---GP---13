package dao;

import config.DBConn;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminCourseDAO {
    
    public List<String[]> getAllCourses() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id FROM course";
        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        String.valueOf(rs.getInt("credit_theory")),
                        String.valueOf(rs.getInt("credit_practical")),
                        String.valueOf(rs.getInt("department_id")),
                        String.valueOf(rs.getInt("lecturer_id"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
