package tg2086.dao;

import common.config.DBConnection;
import common.model.Course;
import tg2086.exception.AdminOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCourseDAO - Data Access Object for Academic Course Modules.
 * - Database Handling (JDBC Connection, PreparedStatement, ResultSet)
 * - Method Overloading (getAllCourses with/without department filter, searchCourses)
 * - Exception Handling (throws AdminOperationException)
 * - Proper resource management with try-with-resources
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminCourseDAO {

    /**
     * Retrieves all courses across all departments.
     */
    public List<Course> getAllCourses() throws AdminOperationException {
        return getAllCourses(-1);
    }

    /**
     * Method Overloading: Retrieves courses optionally filtered by department ID.
     */
    public List<Course> getAllCourses(int departmentId) throws AdminOperationException {
        List<Course> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.course_code, c.course_title, c.credit_theory, c.credit_practical, " +
                "c.department_id, d.department_name, " +
                "c.lecturer_id, u.full_name AS lecturer_name " +
                "FROM course c " +
                "JOIN department d ON c.department_id = d.department_id " +
                "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id "
        );

        if (departmentId > 0) {
            sql.append("WHERE c.department_id = ? ");
        }
        sql.append("ORDER BY c.course_code");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (departmentId > 0) {
                ps.setInt(1, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer lecId = rs.getObject("lecturer_id") != null ? rs.getInt("lecturer_id") : null;
                    String lecName = rs.getString("lecturer_name");
                    if (lecName == null) lecName = "Unassigned";

                    Course course = new Course(
                            rs.getString("course_code"),
                            rs.getString("course_title"),
                            rs.getInt("credit_theory"),
                            rs.getInt("credit_practical"),
                            rs.getInt("department_id"),
                            rs.getString("department_name"),
                            lecId,
                            lecName
                    );
                    list.add(course);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving courses: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Method Overloading: Search courses by code or title keyword.
     */
    public List<Course> searchCourses(String keyword) throws AdminOperationException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_title, c.credit_theory, c.credit_practical, " +
                     "c.department_id, d.department_name, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM course c " +
                     "JOIN department d ON c.department_id = d.department_id " +
                     "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "WHERE c.course_code LIKE ? OR c.course_title LIKE ? " +
                     "ORDER BY c.course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String term = "%" + (keyword != null ? keyword.trim() : "") + "%";
            ps.setString(1, term);
            ps.setString(2, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer lecId = rs.getObject("lecturer_id") != null ? rs.getInt("lecturer_id") : null;
                    String lecName = rs.getString("lecturer_name");
                    if (lecName == null) lecName = "Unassigned";

                    Course course = new Course(
                            rs.getString("course_code"),
                            rs.getString("course_title"),
                            rs.getInt("credit_theory"),
                            rs.getInt("credit_practical"),
                            rs.getInt("department_id"),
                            rs.getString("department_name"),
                            lecId,
                            lecName
                    );
                    list.add(course);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error searching courses: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Creates a new academic course.
     */
    public boolean addCourse(String code, String title, int theoryCredits, int practicalCredits,
                             int departmentId, Integer lecturerId) throws AdminOperationException {
        String sql = "INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase());
            ps.setString(2, title.trim());
            ps.setInt(3, theoryCredits);
            ps.setInt(4, practicalCredits);
            ps.setInt(5, departmentId);

            if (lecturerId != null && lecturerId > 0) {
                ps.setInt(6, lecturerId);
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error adding course: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing academic course.
     */
    public boolean updateCourse(String code, String title, int theoryCredits, int practicalCredits,
                                int departmentId, Integer lecturerId) throws AdminOperationException {
        String sql = "UPDATE course SET course_title = ?, credit_theory = ?, credit_practical = ?, department_id = ?, lecturer_id = ? " +
                     "WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setInt(2, theoryCredits);
            ps.setInt(3, practicalCredits);
            ps.setInt(4, departmentId);

            if (lecturerId != null && lecturerId > 0) {
                ps.setInt(5, lecturerId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, code.trim().toUpperCase());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating course: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a course by its unique course code.
     */
    public boolean deleteCourse(String code) throws AdminOperationException {
        String sql = "DELETE FROM course WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting course: " + e.getMessage(), e);
        }
    }
}
