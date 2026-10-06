package dao;

import config.DBConn;
import model.Timetable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTimetableDAO {
    public boolean addTimetableEntry(Timetable t) {
        String sql = "INSERT INTO timetable (course_code, department_id, session_type, day_of_week, start_time, end_time, venue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getCourseCode());
            ps.setInt(2, t.getDepartmentId());
            ps.setString(3, t.getSessionType());
            ps.setString(4, t.getDayOfWeek());
            ps.setTime(5, t.getStartTime());
            ps.setTime(6, t.getEndTime());
            ps.setString(7, t.getVenue());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Timetable> getAllTimetables() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable";
        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getString("course_code"),
                        rs.getInt("department_id"),
                        rs.getString("session_type"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("venue")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
