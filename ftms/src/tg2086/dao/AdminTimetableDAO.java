package tg2086.dao;

import common.config.DBConnection;
import common.model.Timetable;
import tg2086.exception.AdminOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminTimetableDAO - Data Access Object for Academic and Examination Schedules.
 * - JDBC PreparedStatements & ResultSet parsing
 * - Method Overloading (getAllTimetables with/without department filter, deleteTimetableEntry by int vs object)
 * - Custom Exception Handling (throws AdminOperationException)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
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

        try (Connection conn = DBConnection.getConnection();
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
     * Inserts a new timetable schedule slot.
     */
    public boolean addTimetableEntry(String courseCode, int departmentId, String sessionType,
                                     String dayOfWeek, Time startTime, Time endTime, String venue)
            throws AdminOperationException {
        String sql = "INSERT INTO timetable (course_code, department_id, session_type, day_of_week, start_time, end_time, venue) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode.trim().toUpperCase());
            ps.setInt(2, departmentId);
            ps.setString(3, sessionType.trim().toUpperCase());
            ps.setString(4, dayOfWeek.trim().toUpperCase());
            ps.setTime(5, startTime);
            ps.setTime(6, endTime);
            ps.setString(7, venue.trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error inserting timetable schedule: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing timetable schedule slot.
     */
    public boolean updateTimetableEntry(int timetableId, String courseCode, int departmentId, String sessionType,
                                        String dayOfWeek, Time startTime, Time endTime, String venue)
            throws AdminOperationException {
        String sql = "UPDATE timetable SET course_code = ?, department_id = ?, session_type = ?, day_of_week = ?, " +
                     "start_time = ?, end_time = ?, venue = ? WHERE timetable_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode.trim().toUpperCase());
            ps.setInt(2, departmentId);
            ps.setString(3, sessionType.trim().toUpperCase());
            ps.setString(4, dayOfWeek.trim().toUpperCase());
            ps.setTime(5, startTime);
            ps.setTime(6, endTime);
            ps.setString(7, venue.trim());
            ps.setInt(8, timetableId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating timetable slot " + timetableId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Delete timetable entry by timetable ID.
     */
    public boolean deleteTimetableEntry(int timetableId) throws AdminOperationException {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting timetable slot " + timetableId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Delete timetable entry by Timetable entity.
     */
    public boolean deleteTimetableEntry(Timetable t) throws AdminOperationException {
        if (t == null) {
            throw new AdminOperationException("Cannot delete null timetable reference.");
        }
        return deleteTimetableEntry(t.getTimetableId());
    }
}
