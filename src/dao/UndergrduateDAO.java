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



}