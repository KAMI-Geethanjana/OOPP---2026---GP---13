package tg2088.dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Result;
import tg2088.exception.LecturerOperationException;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * Lesson 04 (Interfaces) & Lesson 07 (JDBC Connectivity)
 * ============================================================================
 * Reference:
 * - Lesson 04, Slide 17-18: 'class LecturerDAO implements ILecturerDAO'
 * - Lesson 07, Slide 25: 05 Basic Steps to Use a Database in Java
 *   1. Register the Driver Class
 *   2. Create a Connection
 *   3. Create a Statement (PreparedStatement)
 *   4. Execute Queries (executeQuery, executeUpdate)
 *   5. Close the Connection and Resources
 * - Lesson 07, Slide 33-34: Parameterized Queries with PreparedStatement (?)
 * - Lesson 07, Slide 35-36: ResultSet traversal & Column Index / Column Name access
 * - Lesson 03 Part 02, Slide 13-17: Method Overloading (Compile-Time Polymorphism)
 * - Lesson 05, Slide 25-37: Exception Handling (try-catch-finally, custom exceptions)
 * ============================================================================
 */
public class LecturerDAO implements ILecturerDAO {

    // ========================================================================
    // Static Initializer Block (Lesson 02 Part 02, Slide 29-30)
    // Ensures JDBC Driver is loaded when class is first referenced
    // ========================================================================
    static {
        try {
            // Step 1: Register the Driver Class (Lesson 07, Slide 26)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver registration error in LecturerDAO: " + e.getMessage());
        }
    }

    public LecturerDAO() {
        // Invoking interface default method (Lesson 04, Slide 32)
        printModuleStatus();
    }

    /**
     * Retrieves Lecturer by user ID.
     * Demonstrates JDBC Steps 1 to 5 clearly.
     */
    @Override
    public Lecturer getLecturerByUserId(int userId) {
        String sql = "SELECT l.lecturer_id, l.employee_id, l.department_id, d.department_name, " +
                     "u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no " +
                     "FROM lecturer l " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "JOIN department d ON l.department_id = d.department_id " +
                     "WHERE l.user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Step 2: Create a Connection (Lesson 07, Slide 27)
            conn = DBConnection.getConnection();

            // Step 3: Create a PreparedStatement (Lesson 07, Slide 29 & 34)
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            // Step 4: Execute Query (Lesson 07, Slide 30-31)
            rs = ps.executeQuery();

            // Step 4b: Traverse through ResultSet (Lesson 07, Slide 35-36)
            if (rs.next()) {
                // Accessing columns using Column Name (Lesson 07, Slide 36)
                Lecturer lec = new Lecturer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_no"),
                        rs.getInt("lecturer_id"),
                        rs.getString("employee_id"),
                        rs.getInt("department_id")
                );
                lec.setDepartmentName(rs.getString("department_name"));
                return lec;
            }

        } catch (SQLException e) {
            // Lesson 05 Slide 22 & Lesson 07 Slide 37: Printing stack trace on SQLException
            System.err.println("SQLException in getLecturerByUserId: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Step 5: Close Resources in finally block (Lesson 05 Slide 33 & Lesson 07 Slide 32)
            closeQuietly(rs);
            closeQuietly(ps);
        }
        return null;
    }

    /**
     * Retrieves all courses assigned to this lecturer.
     */
    @Override
    public List<Course> getCoursesByLecturer(int lecturerId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE lecturer_id = ? ORDER BY course_code";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lecturerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                // Demonstrates Column Index access as well as Column Name access (Lesson 07, Slide 36)
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("credit_theory"),
                        rs.getInt("credit_practical"),
                        rs.getInt("department_id"),
                        rs.getInt("lecturer_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getCoursesByLecturer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
        return list;
    }

    /**
     * Retrieves results for a course.
     * Method 1 of Overloaded pair (Lesson 03 Part 02, Slide 13-15).
     */
    @Override
    public List<Result> getResultsByCourse(String courseCode) {
        return getResultsByCourse(courseCode, null);
    }

    /**
     * Overloaded method: Retrieves results for a course filtered by eligibility status.
     * Method 2 of Overloaded pair (Static Polymorphism - Lesson 03 Part 02, Slide 14-17).
     */
    @Override
    public List<Result> getResultsByCourse(String courseCode, String eligibilityFilter) {
        List<Result> list = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT e.enrollment_id, e.student_id, ug.reg_no, u.full_name, e.course_code, " +
                "e.attempt_type, " +
                "COALESCE(r.result_id, 0) AS result_id, " +
                "COALESCE(r.ca_mark, 0.0) AS ca_mark, " +
                "COALESCE(r.final_exam_mark, 0.0) AS final_exam_mark, " +
                "COALESCE(r.total_mark, 0.0) AS total_mark, " +
                "COALESCE(r.eligibility_status, 'ELIGIBLE') AS eligibility_status, " +
                "COALESCE(r.grade, '-') AS grade, " +
                "COALESCE(r.grade_point, 0.0) AS grade_point " +
                "FROM enrollment e " +
                "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "LEFT JOIN result r ON e.enrollment_id = r.enrollment_id " +
                "WHERE e.course_code = ? "
        );

        if (eligibilityFilter != null && !eligibilityFilter.trim().isEmpty()) {
            sqlBuilder.append("AND r.eligibility_status = ? ");
        }
        sqlBuilder.append("ORDER BY ug.reg_no");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sqlBuilder.toString());
            ps.setString(1, courseCode);
            if (eligibilityFilter != null && !eligibilityFilter.trim().isEmpty()) {
                ps.setString(2, eligibilityFilter.trim());
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Result(
                        rs.getInt("result_id"),
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("full_name"),
                        rs.getString("course_code"),
                        rs.getString("attempt_type"),
                        rs.getDouble("ca_mark"),
                        rs.getDouble("final_exam_mark"),
                        rs.getDouble("total_mark"),
                        rs.getString("eligibility_status"),
                        rs.getString("grade"),
                        rs.getDouble("grade_point")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getResultsByCourse: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
        return list;
    }

    /**
     * Persists calculated marks and UGC grades into database.
     * Demonstrates 'throws LecturerOperationException' (Lesson 05, Slide 40-42).
     */
    @Override
    public boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                                      double totalMark, String eligibilityStatus, String grade,
                                      double gradePoint) throws LecturerOperationException {
        // Business rule validation using custom exception (Lesson 05, Slide 44)
        if (enrollmentId <= 0) {
            throw new LecturerOperationException("Invalid Enrollment ID: " + enrollmentId);
        }
        if (caMark < 0.0 || caMark > 100.0 || finalExamMark < 0.0 || finalExamMark > 100.0) {
            throw new LecturerOperationException("Marks out of range [0-100]. CA: " + caMark + ", Final: " + finalExamMark);
        }

        String sql = "INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "ca_mark = VALUES(ca_mark), " +
                     "final_exam_mark = VALUES(final_exam_mark), " +
                     "total_mark = VALUES(total_mark), " +
                     "eligibility_status = VALUES(eligibility_status), " +
                     "grade = VALUES(grade), " +
                     "grade_point = VALUES(grade_point)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            // PreparedStatement parameter index starts at 1 (Lesson 07, Slide 29)
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, caMark);
            ps.setDouble(3, finalExamMark);
            ps.setDouble(4, totalMark);
            ps.setString(5, eligibilityStatus != null ? eligibilityStatus : "NOT_ELIGIBLE");
            ps.setString(6, grade != null ? grade : "-");
            ps.setDouble(7, gradePoint);

            // executeUpdate returns the number of affected rows (Lesson 07, Slide 30)
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("SQLException in saveOrUpdateResult: " + e.getMessage());
            e.printStackTrace();
            throw new LecturerOperationException("Failed to save student result due to database error.", e);
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * Retrieves course materials for a given course.
     */
    @Override
    public List<CourseMaterial> getCourseMaterials(String courseCode) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM course_material WHERE course_code = ? ORDER BY uploaded_date DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, courseCode);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new CourseMaterial(
                        rs.getInt("material_id"),
                        rs.getString("course_code"),
                        rs.getInt("lecturer_id"),
                        rs.getString("title"),
                        rs.getString("file_path"),
                        rs.getDate("uploaded_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getCourseMaterials: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
        return list;
    }

    /**
     * Uploads new course material record.
     */
    @Override
    public boolean addCourseMaterial(CourseMaterial material) throws LecturerOperationException {
        if (material == null || material.getTitle() == null || material.getTitle().trim().isEmpty()) {
            throw new LecturerOperationException("Course Material Title cannot be empty!");
        }

        String sql = "INSERT INTO course_material (course_code, lecturer_id, title, file_path, uploaded_date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, material.getCourseCode());
            ps.setInt(2, material.getLecturerId());
            ps.setString(3, material.getTitle());
            ps.setString(4, material.getFilePath());
            ps.setDate(5, material.getUploadedDate());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("SQLException in addCourseMaterial: " + e.getMessage());
            e.printStackTrace();
            throw new LecturerOperationException("Failed to save course material to database.", e);
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * Deletes course material record by ID.
     */
    @Override
    public boolean deleteCourseMaterial(int materialId) {
        String sql = "DELETE FROM course_material WHERE material_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQLException in deleteCourseMaterial: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * Retrieves aggregated student attendance and approved medical records.
     */
    @Override
    public List<Map<String, Object>> getStudentAttendanceSummary(String courseCode) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT ug.student_id, ug.reg_no, u.full_name, " +
                     "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS attended_count, " +
                     "COUNT(a.attendance_id) AS total_marked, " +
                     "COALESCE(m.med_count, 0) AS approved_medicals " +
                     "FROM enrollment e " +
                     "JOIN undergraduate ug ON e.student_id = ug.student_id " +
                     "JOIN users u ON ug.user_id = u.user_id " +
                     "LEFT JOIN attendance a ON a.student_id = ug.student_id AND a.course_code = e.course_code " +
                     "LEFT JOIN (SELECT student_id, course_code, COUNT(*) AS med_count FROM medical WHERE status = 'APPROVED' GROUP BY student_id, course_code) m " +
                     "  ON m.student_id = ug.student_id AND m.course_code = e.course_code " +
                     "WHERE e.course_code = ? " +
                     "GROUP BY ug.student_id, ug.reg_no, u.full_name, m.med_count " +
                     "ORDER BY ug.reg_no";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, courseCode);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("student_id"));
                row.put("regNo", rs.getString("reg_no"));
                row.put("fullName", rs.getString("full_name"));

                int attended = rs.getInt("attended_count");
                int approvedMeds = rs.getInt("approved_medicals");
                int totalSessions = STANDARD_SESSIONS_COUNT; // from ILecturerDAO constants
                double rawPercentage = (attended * 100.0) / totalSessions;
                double excusedPercentage = ((attended + approvedMeds) * 100.0) / totalSessions;
                if (excusedPercentage > 100.0) excusedPercentage = 100.0;

                row.put("attendedCount", attended);
                row.put("totalSessions", totalSessions);
                row.put("approvedMedicals", approvedMeds);
                row.put("rawPercentage", Math.round(rawPercentage * 10.0) / 10.0);
                row.put("excusedPercentage", Math.round(excusedPercentage * 10.0) / 10.0);
                row.put("isEligible", excusedPercentage >= MIN_ATTENDANCE_REQUIREMENT);

                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getStudentAttendanceSummary: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
        return list;
    }

    /**
     * Updates profile information for the lecturer user.
     */
    @Override
    public boolean updateProfile(int userId, String fullName, String email, String phoneNo) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_no = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phoneNo);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQLException in updateProfile: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(ps);
        }
    }

    // ========================================================================
    // Helper method to safely close JDBC AutoCloseable resources in finally
    // (Lesson 05, Slide 33 & 36 - Why use finally block)
    // ========================================================================
    private void closeQuietly(AutoCloseable res) {
        if (res != null) {
            try {
                res.close();
            } catch (Exception e) {
                System.err.println("Resource close warning: " + e.getMessage());
            }
        }
    }
}
