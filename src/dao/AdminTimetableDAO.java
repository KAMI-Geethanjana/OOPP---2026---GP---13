package dao;

import config.DBConn;
import model.Timetable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminTimetableDAO - Data Access Object for Academic and Examination Schedules.
 * Implements FR-ADM-04 (Create and maintain timetables for lectures, practicals, examinations).
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminTimetableDAO {

    /**
     * Retrieves all timetable entries with course titles and department names.
     */
    public List<Timetable> getAllTimetables() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.course_code, c.course_title, " +
                     "t.department_id, d.department_name, " +
                     "t.session_type, t.day_of_week, t.start_time, t.end_time, t.venue " +
                     "FROM timetable t " +
                     "JOIN course c ON t.course_code = c.course_code " +
                     "JOIN department d ON t.department_id = d.department_id " +
                     "ORDER BY FIELD(t.day_of_week, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'), t.start_time";

        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timetable t = new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("session_type"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("venue")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new timetable entry.
     */
    public boolean addTimetableEntry(Timetable t) {
        String sql = "INSERT INTO timetable (course_code, department_id, session_type, day_of_week, start_time, end_time, venue) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getCourseCode().trim().toUpperCase());
            ps.setInt(2, t.getDepartmentId());
            ps.setString(3, t.getSessionType().toUpperCase().trim());
            ps.setString(4, t.getDayOfWeek().toUpperCase().trim());
            ps.setTime(5, t.getStartTime());
            ps.setTime(6, t.getEndTime());
            ps.setString(7, t.getVenue().trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing timetable slot.
     */
    public boolean updateTimetableEntry(Timetable t) {
        String sql = "UPDATE timetable SET course_code = ?, department_id = ?, session_type = ?, " +
                     "day_of_week = ?, start_time = ?, end_time = ?, venue = ? WHERE timetable_id = ?";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getCourseCode().trim().toUpperCase());
            ps.setInt(2, t.getDepartmentId());
            ps.setString(3, t.getSessionType().toUpperCase().trim());
            ps.setString(4, t.getDayOfWeek().toUpperCase().trim());
            ps.setTime(5, t.getStartTime());
            ps.setTime(6, t.getEndTime());
            ps.setString(7, t.getVenue().trim());
            ps.setInt(8, t.getTimetableId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a timetable entry by ID.
     */
    public boolean deleteTimetableEntry(int timetableId) {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
