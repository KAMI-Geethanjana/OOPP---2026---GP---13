package dao;

import config.DBConn;
import exception.AdminOperationException;
import model.Timetable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminTimetableDAO - Data Access Object for Academic and Examination Schedules.
 * Demonstrates:
 * 1. JDBC PreparedStatements & ResultSet parsing (Lesson 07)
 * 2. Method Overloading (getAllTimetables with/without department filter, deleteTimetableEntry by int vs object - Lesson 03 Part 02)
 * 3. Custom Exception Handling (throws AdminOperationException - Lesson 05)
 */
public class AdminTimetableDAO {

    /**
     * Retrieves all timetable entries across all departments.
     */
    public List<Timetable> getAllTimetables() throws AdminOperationException {
        return getAllTimetables(-1);
    }

    /**
     * Method Overloading: Retrieves timetable entries filtered by department ID.
     * Demonstrates Lesson 03 Part 02 (Method Overloading by parameter list).
     */
    public List<Timetable> getAllTimetables(int departmentId) throws AdminOperationException {
        List<Timetable> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.timetable_id, t.course_code, c.course_title, " +
                "t.department_id, d.department_name, " +
                "t.session_type, t.day_of_week, t.start_time, t.end_time, t.venue " +
                "FROM timetable t " +
                "JOIN course c ON t.course_code = c.course_code " +
                "JOIN department d ON t.department_id = d.department_id "
        );

        if (departmentId > 0) {
            sql.append("WHERE t.department_id = ? ");
        }
        sql.append("ORDER BY FIELD(t.day_of_week, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'), t.start_time");

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (departmentId > 0) {
                ps.setInt(1, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving timetables: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Adds a new timetable entry.
     */
    public boolean addTimetableEntry(Timetable t) throws AdminOperationException {
        if (t == null) {
            throw new AdminOperationException("Timetable entry cannot be null.");
        }

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
            throw new AdminOperationException("Database error adding timetable entry: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing timetable slot.
     */
    public boolean updateTimetableEntry(Timetable t) throws AdminOperationException {
        if (t == null) {
            throw new AdminOperationException("Timetable entry cannot be null.");
        }

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
            throw new AdminOperationException("Database error updating timetable entry: " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes a timetable entry by ID.
     */
    public boolean deleteTimetableEntry(int timetableId) throws AdminOperationException {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting timetable entry ID " + timetableId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes a timetable entry by Timetable object.
     * Demonstrates Lesson 03 Part 02 (Method Overloading by parameter type).
     */
    public boolean deleteTimetableEntry(Timetable t) throws AdminOperationException {
        if (t == null) return false;
        return deleteTimetableEntry(t.getTimetableId());
    }
}
