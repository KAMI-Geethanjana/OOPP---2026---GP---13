package tg2086.dao;

import common.config.DBConnection;
import common.model.Course;
import tg2086.exception.AdminOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Retrieves all registered lecturers to populate the lecturer assignment dropdown.
     * Includes automatic fallback healing to ensure any user registered with role 'LECTURER'
     * has a corresponding entry in the lecturer table.
     */
    public List<Map<String, Object>> getLecturersForAssignment() throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();

        // Auto-heal: Ensure any users with role 'LECTURER' have a record in lecturer table
        String healSql = "INSERT INTO lecturer (user_id, employee_id, department_id) " +
                         "SELECT u.user_id, CONCAT('EMP/LEC/', u.user_id), 1 " +
                         "FROM users u " +
                         "LEFT JOIN lecturer l ON u.user_id = l.user_id " +
                         "WHERE u.role = 'LECTURER' AND l.lecturer_id IS NULL";

        String querySql = "SELECT l.lecturer_id, u.user_id, u.username, u.full_name, " +
                          "COALESCE(l.employee_id, '') AS employee_id, " +
                          "COALESCE(d.department_name, '') AS department_name " +
                          "FROM users u " +
                          "JOIN lecturer l ON u.user_id = l.user_id " +
                          "LEFT JOIN department d ON l.department_id = d.department_id " +
                          "WHERE u.role = 'LECTURER' AND u.is_active = 1 " +
                          "ORDER BY u.full_name ASC";

        try (Connection conn = DBConnection.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(healSql);
            } catch (SQLException ignored) {
                // If unique constraint or department FK prevents heal, continue with existing records
            }

            try (PreparedStatement ps = conn.prepareStatement(querySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lecturerId", rs.getInt("lecturer_id"));
                    map.put("userId", rs.getInt("user_id"));
                    map.put("username", rs.getString("username"));
                    map.put("fullName", rs.getString("full_name"));
                    map.put("employeeId", rs.getString("employee_id"));
                    map.put("departmentName", rs.getString("department_name"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving lecturers for assignment: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Directly assign or unassign a lecturer to a specific course code.
     */
    public boolean assignLecturerToCourse(String courseCode, Integer lecturerId) throws AdminOperationException {
        String sql = "UPDATE course SET lecturer_id = ? WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (lecturerId != null && lecturerId > 0) {
                ps.setInt(1, lecturerId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, courseCode.trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Failed to assign lecturer to course: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // UNDERGRADUATE COURSE REGISTRATION & ENROLLMENT (TG2086)
    // =========================================================================

    /**
     * Retrieves all active undergraduates filtered by department ID (or all if departmentId <= 0).
     */
    public List<Map<String, Object>> getUndergraduatesByDepartment(int departmentId) throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();

        String healSql = "INSERT INTO undergraduate (user_id, reg_no, batch_year, department_id) " +
                         "SELECT u.user_id, CONCAT('TG/', u.username), 2024, 1 " +
                         "FROM users u " +
                         "LEFT JOIN undergraduate ug ON u.user_id = ug.user_id " +
                         "WHERE u.role = 'UNDERGRADUATE' AND ug.student_id IS NULL";

        String querySql = "SELECT ug.student_id, ug.user_id, ug.reg_no, ug.batch_year, " +
                          "COALESCE(ug.status, 'ACTIVE') AS status, ug.department_id, " +
                          "u.full_name, u.username, u.email, d.department_name " +
                          "FROM undergraduate ug " +
                          "JOIN users u ON ug.user_id = u.user_id " +
                          "JOIN department d ON ug.department_id = d.department_id " +
                          "WHERE (? <= 0 OR ug.department_id = ?) AND u.is_active = 1 " +
                          "ORDER BY ug.reg_no ASC";

        try (Connection conn = DBConnection.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(healSql);
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setInt(1, departmentId);
                ps.setInt(2, departmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("studentId", rs.getInt("student_id"));
                        map.put("userId", rs.getInt("user_id"));
                        map.put("regNo", rs.getString("reg_no"));
                        map.put("batchYear", rs.getInt("batch_year"));
                        map.put("status", rs.getString("status"));
                        map.put("departmentId", rs.getInt("department_id"));
                        map.put("fullName", rs.getString("full_name"));
                        map.put("username", rs.getString("username"));
                        map.put("email", rs.getString("email"));
                        map.put("departmentName", rs.getString("department_name"));
                        list.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving undergraduates: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Retrieves all course modules currently registered/enrolled for a specific undergraduate student.
     */
    public List<Map<String, Object>> getStudentEnrolledCourses(int studentId) throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.course_code, e.academic_year, e.semester, e.attempt_type, " +
                     "c.course_title, c.credit_theory, c.credit_practical, " +
                     "(c.credit_theory + c.credit_practical) AS total_credits, " +
                     "c.department_id, d.department_name, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_code = c.course_code " +
                     "JOIN department d ON c.department_id = d.department_id " +
                     "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY e.academic_year DESC, e.semester ASC, c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("enrollmentId", rs.getInt("enrollment_id"));
                    map.put("studentId", rs.getInt("student_id"));
                    map.put("courseCode", rs.getString("course_code"));
                    map.put("academicYear", rs.getInt("academic_year"));
                    map.put("semester", rs.getInt("semester"));
                    map.put("attemptType", rs.getString("attempt_type"));
                    map.put("courseTitle", rs.getString("course_title"));
                    map.put("creditTheory", rs.getInt("credit_theory"));
                    map.put("creditPractical", rs.getInt("credit_practical"));
                    map.put("totalCredits", rs.getInt("total_credits"));
                    map.put("departmentId", rs.getInt("department_id"));
                    map.put("departmentName", rs.getString("department_name"));
                    map.put("lecturerId", rs.getObject("lecturer_id") != null ? rs.getInt("lecturer_id") : null);
                    map.put("lecturerName", rs.getString("lecturer_name") != null ? rs.getString("lecturer_name") : "Unassigned");
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving student enrolled courses: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Retrieves all courses for a department with an indicator of whether the given student is already enrolled.
     */
    public List<Map<String, Object>> getDepartmentCoursesWithEnrollment(int departmentId, int studentId) throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_title, c.credit_theory, c.credit_practical, " +
                     "(c.credit_theory + c.credit_practical) AS total_credits, " +
                     "c.department_id, d.department_name, " +
                     "c.lecturer_id, u.full_name AS lecturer_name, " +
                     "e.enrollment_id, e.academic_year, e.semester, e.attempt_type, " +
                     "CASE WHEN e.enrollment_id IS NOT NULL THEN 1 ELSE 0 END AS is_enrolled " +
                     "FROM course c " +
                     "JOIN department d ON c.department_id = d.department_id " +
                     "LEFT JOIN lecturer l ON c.lecturer_id = l.lecturer_id " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "LEFT JOIN enrollment e ON c.course_code = e.course_code AND e.student_id = ? " +
                     "WHERE (? <= 0 OR c.department_id = ?) " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, departmentId);
            ps.setInt(3, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseCode", rs.getString("course_code"));
                    map.put("courseTitle", rs.getString("course_title"));
                    map.put("creditTheory", rs.getInt("credit_theory"));
                    map.put("creditPractical", rs.getInt("credit_practical"));
                    map.put("totalCredits", rs.getInt("total_credits"));
                    map.put("departmentId", rs.getInt("department_id"));
                    map.put("departmentName", rs.getString("department_name"));
                    map.put("lecturerId", rs.getObject("lecturer_id") != null ? rs.getInt("lecturer_id") : null);
                    map.put("lecturerName", rs.getString("lecturer_name") != null ? rs.getString("lecturer_name") : "Unassigned");
                    map.put("isEnrolled", rs.getInt("is_enrolled") == 1);
                    map.put("enrollmentId", rs.getObject("enrollment_id") != null ? rs.getInt("enrollment_id") : null);
                    map.put("academicYear", rs.getObject("academic_year") != null ? rs.getInt("academic_year") : null);
                    map.put("semester", rs.getObject("semester") != null ? rs.getInt("semester") : null);
                    map.put("attemptType", rs.getString("attempt_type"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving department courses with enrollment: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Enrolls an undergraduate student into a course module.
     */
    public boolean enrollStudentInCourse(int studentId, String courseCode, int academicYear, int semester, String attemptType) throws AdminOperationException {
        String checkSql = "SELECT enrollment_id FROM enrollment WHERE student_id = ? AND course_code = ? AND academic_year = ? AND semester = ?";
        String insertSql = "INSERT INTO enrollment (student_id, course_code, academic_year, semester, attempt_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, studentId);
                psCheck.setString(2, courseCode.trim().toUpperCase());
                psCheck.setInt(3, academicYear);
                psCheck.setInt(4, semester);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        throw new AdminOperationException("Student is already registered for " + courseCode + " in Academic Year " + academicYear + " (Semester " + semester + ")!");
                    }
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, studentId);
                psInsert.setString(2, courseCode.trim().toUpperCase());
                psInsert.setInt(3, academicYear);
                psInsert.setInt(4, semester);
                psInsert.setString(5, (attemptType != null && !attemptType.trim().isEmpty()) ? attemptType.toUpperCase().trim() : "PROPER");
                return psInsert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Failed to register student for course: " + e.getMessage(), e);
        }
    }

    /**
     * Unenrolls / drops a student registration by enrollment ID.
     */
    public boolean unenrollStudentFromCourse(int enrollmentId) throws AdminOperationException {
        String sql = "DELETE FROM enrollment WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Failed to unenroll student: " + e.getMessage(), e);
        }
    }

    /**
     * Batch enrolls an undergraduate in all courses of their department for a given year & semester.
     */
    public int enrollStudentInAllDepartmentCourses(int studentId, int departmentId, int academicYear, int semester, String attemptType) throws AdminOperationException {
        List<Map<String, Object>> courses = getDepartmentCoursesWithEnrollment(departmentId, studentId);
        int enrolledCount = 0;
        for (Map<String, Object> c : courses) {
            boolean isEnrolled = Boolean.TRUE.equals(c.get("isEnrolled"));
            if (!isEnrolled) {
                String code = (String) c.get("courseCode");
                try {
                    if (enrollStudentInCourse(studentId, code, academicYear, semester, attemptType)) {
                        enrolledCount++;
                    }
                } catch (Exception ignored) {}
            }
        }
        return enrolledCount;
    }
}
