package dao;

import config.DBConn;
import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCourseDAO - Data Access Object for Academic Course Modules.
 * Implements FR-ADM-02 (Course CRUD) and FR-ADM-06 (Assign Lecturer to Course).
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminCourseDAO {

    /**
     * Retrieves all courses with department and assigned lecturer names.
     */
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_title, c.credit_theory, c.credit_practical, " +
                     "c.department_id, d.department_name, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM course c " +
                     "JOIN department d ON c.department_id = d.department_id " +
                     "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "ORDER BY c.course_code";

        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts a new course into the system.
     */
    public boolean addCourse(Course course) {
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates existing course details.
     */
    public boolean updateCourse(Course course) {
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a course by its unique course code.
     */
    public boolean deleteCourse(String courseCode) {
        String sql = "DELETE FROM course WHERE course_code = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Assigns a lecturer to teach a course module.
     */
    public boolean assignLecturer(String courseCode, int lecturerId) {
        String sql = "UPDATE course SET lecturer_id = ? WHERE course_code = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lecturerId);
            ps.setString(2, courseCode.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
