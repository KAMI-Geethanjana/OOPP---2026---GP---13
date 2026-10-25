package dao;

import config.DBConn;
import exception.AdminOperationException;
import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCourseDAO - Data Access Object for Academic Course Modules.
 * - Database Handling (JDBC Connection, PreparedStatement, ResultSet)
 * - Method Overloading (getAllCourses with/without department filter, searchCourses)
 * - Exception Handling (throws AdminOperationException)
 * - Proper resource management with try-with-resources
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

        try (Connection conn = DBConn.getConn();
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
     * Inserts a new course into the system using PreparedStatement.
     */
    public boolean addCourse(Course course) throws AdminOperationException {
        if (course == null || course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new AdminOperationException("Course details cannot be empty.");
        }

        String sql = "INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseCode().trim().toUpperCase());
            ps.setString(2, course.getCourseTitle().trim());
            ps.setInt(3, course.getCreditTheory());
            ps.setInt(4, course.getCreditPractical());
            ps.setInt(5, course.getDepartmentId());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                ps.setInt(6, course.getLecturerId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error adding course " + course.getCourseCode() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Updates existing course details.
     */
    public boolean updateCourse(Course course) throws AdminOperationException {
        if (course == null || course.getCourseCode() == null) {
            throw new AdminOperationException("Course details cannot be null.");
        }

        String sql = "UPDATE course SET course_title = ?, credit_theory = ?, credit_practical = ?, department_id = ?, lecturer_id = ? " +
                     "WHERE course_code = ?";

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseTitle().trim());
            ps.setInt(2, course.getCreditTheory());
            ps.setInt(3, course.getCreditPractical());
            ps.setInt(4, course.getDepartmentId());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                ps.setInt(5, course.getLecturerId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, course.getCourseCode().trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating course: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a course by its unique course code.
     */
    public boolean deleteCourse(String courseCode) throws AdminOperationException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new AdminOperationException("Course code cannot be empty.");
        }

        String sql = "DELETE FROM course WHERE course_code = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting course " + courseCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Assigns a lecturer to teach a course module.
     */
    public boolean assignLecturer(String courseCode, int lecturerId) throws AdminOperationException {
        String sql = "UPDATE course SET lecturer_id = ? WHERE course_code = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lecturerId);
            ps.setString(2, courseCode.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error assigning lecturer: " + e.getMessage(), e);
        }
    }
}
