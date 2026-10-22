package dao;

import config.DBConn;
import exception.AdminOperationException;
import model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminUserDAO - Data Access Object for complete User Profile Lifecycle.
 * Demonstrates:
 * 1. Interface Implementation (implements UserOperations - Lesson 04)
 * 2. Exception Handling (throws custom AdminOperationException - Lesson 05)
 * 3. Method Overloading (deleteUser by ID vs username, getAllUsers with/without filter - Lesson 03 Part 02)
 * 4. Database Handling (JDBC transactions with rollback, PreparedStatements - Lesson 07)
 * 5. Default and static interface methods invocation (Lesson 04 Slide 32-33)
 */
public class AdminUserDAO implements UserOperations {

    /**
     * Atomically creates a user and their role-specific record (Undergraduate, Lecturer, Technical Officer).
     */
    @Override
    public boolean createUser(String username, String password, String fullName, String email,
                              String phoneNo, String role, String extraId, int departmentId, String batch)
            throws AdminOperationException {

        // Validate role using static method from UserOperations interface (Lesson 04 Slide 33)
        if (!UserOperations.isValidRole(role)) {
            throw new AdminOperationException("Invalid user role specified: " + role);
        }

        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, phone_no, role, is_active, failed_logins) " +
                               "VALUES (?, ?, ?, ?, ?, ?, 1, 0)";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psRole = null;

        try {
            conn = DBConn.getConn();
            conn.setAutoCommit(false); // Begin JDBC transaction (Lesson 07 Slide 41)

            psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username.trim());
            psUser.setString(2, (password != null && !password.isEmpty()) ? password.trim() : DEFAULT_PASSWORD);
            psUser.setString(3, fullName.trim());
            psUser.setString(4, email != null ? email.trim() : "");
            psUser.setString(5, phoneNo != null ? phoneNo.trim() : "");
            psUser.setString(6, role.toUpperCase().trim());

            int affected = psUser.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }

            int generatedUserId = -1;
            try (ResultSet rs = psUser.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getInt(1);
                }
            }

            if (generatedUserId == -1) {
                conn.rollback();
                return false;
            }

            // Insert into specialized child table based on role
            switch (role.toUpperCase().trim()) {
                case "UNDERGRADUATE":
                    String insertUgSql = "INSERT INTO undergraduate (student_id, user_id, department_id, batch) VALUES (?, ?, ?, ?)";
                    psRole = conn.prepareStatement(insertUgSql);
                    psRole.setString(1, extraId != null && !extraId.isEmpty() ? extraId.trim() : "TG/" + username);
                    psRole.setInt(2, generatedUserId);
                    psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                    psRole.setString(4, batch != null && !batch.isEmpty() ? batch.trim() : "2023/2024");
                    psRole.executeUpdate();
                    break;

                case "LECTURER":
                    String insertLecSql = "INSERT INTO lecturer (user_id, employee_id, department_id) VALUES (?, ?, ?)";
                    psRole = conn.prepareStatement(insertLecSql);
                    psRole.setInt(1, generatedUserId);
                    psRole.setString(2, extraId != null && !extraId.isEmpty() ? extraId.trim() : "EMP/" + username);
                    psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                    psRole.executeUpdate();
                    break;

                case "TECH_OFFICER":
                    String insertToSql = "INSERT INTO technical_officer (user_id, employee_id, department_id) VALUES (?, ?, ?)";
                    psRole = conn.prepareStatement(insertToSql);
                    psRole.setInt(1, generatedUserId);
                    psRole.setString(2, extraId != null && !extraId.isEmpty() ? extraId.trim() : "TO/" + username);
                    psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                    psRole.executeUpdate();
                    break;

                case "ADMIN":
                    // Admin only requires entry in users table
                    break;
            }

            conn.commit(); // Commit transaction

            // Invoke default interface method (Lesson 04 Slide 32)
            logAudit("CREATE_USER", "Created user " + username + " with role " + role);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure (Lesson 07 Slide 41)
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new AdminOperationException("Database error creating user: " + e.getMessage(), e);
        } finally {
            // Clean up resources in finally block (Lesson 05 Slide 33-36)
            try {
                if (psRole != null) psRole.close();
                if (psUser != null) psUser.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Interface method implementation: Full user update
     */
    @Override
    public boolean updateUser(int userId, String fullName, String email, String phoneNo,
                              String role, boolean isActive, String extraId, int departmentId, String batch)
            throws AdminOperationException {
        return updateUser(userId, fullName, email, phoneNo, isActive);
    }

    /**
     * Overloaded method: Updates common profile attributes and active status for any user.
     */
    public boolean updateUser(int userId, String fullName, String email, String phoneNo, boolean isActive)
            throws AdminOperationException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_no = ?, is_active = ? WHERE user_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, email != null ? email.trim() : "");
            ps.setString(3, phoneNo != null ? phoneNo.trim() : "");
            ps.setInt(4, isActive ? 1 : 0);
            ps.setInt(5, userId);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logAudit("UPDATE_USER", "Updated user ID: " + userId);
            }
            return success;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes user by ID (Lesson 03 Part 02)
     */
    @Override
    public boolean deleteUser(int userId) throws AdminOperationException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logAudit("DELETE_USER", "Deleted user ID: " + userId);
            }
            return success;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes user by Username (Lesson 03 Part 02)
     */
    @Override
    public boolean deleteUser(String username) throws AdminOperationException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logAudit("DELETE_USER", "Deleted username: " + username);
            }
            return success;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting username " + username + ": " + e.getMessage(), e);
        }
    }

    /**
     * Unlocks a locked user account (FR-AUTH-05).
     * Resets failed login attempts back to 0 and re-activates account.
     */
    @Override
    public boolean unlockAccount(int userId) throws AdminOperationException {
        String sql = "UPDATE users SET failed_logins = 0, is_active = 1 WHERE user_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                logAudit("UNLOCK_ACCOUNT", "Unlocked user ID: " + userId);
            }
            return success;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error unlocking account for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Backward-compatible alias for existing UI caller
     */
    public boolean unlockUserAccount(int userId) throws AdminOperationException {
        return unlockAccount(userId);
    }

    /**
     * Retrieves all users in the system with their role details.
     */
    @Override
    public List<Map<String, Object>> getAllUsers() throws AdminOperationException {
        return getAllUsers(null);
    }

    /**
     * Method Overloading: Retrieves users optionally filtered by role (Lesson 03 Part 02)
     */
    @Override
    public List<Map<String, Object>> getAllUsers(String roleFilter) throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.username, u.full_name, u.email, u.phone_no, u.role, u.is_active, u.failed_logins, " +
                "ug.student_id, ug.batch, " +
                "lec.employee_id AS lec_emp_id, " +
                "tow.employee_id AS to_emp_id, " +
                "COALESCE(du.department_name, dl.department_name, dt.department_name, 'Administration') AS department_name " +
                "FROM users u " +
                "LEFT JOIN undergraduate ug ON u.user_id = ug.user_id " +
                "LEFT JOIN department du ON ug.department_id = du.department_id " +
                "LEFT JOIN lecturer lec ON u.user_id = lec.user_id " +
                "LEFT JOIN department dl ON lec.department_id = dl.department_id " +
                "LEFT JOIN technical_officer tow ON u.user_id = tow.user_id " +
                "LEFT JOIN department dt ON tow.department_id = dt.department_id "
        );

        if (roleFilter != null && !roleFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleFilter.trim())) {
            sql.append("WHERE u.role = ? ");
        }
        sql.append("ORDER BY u.user_id DESC");

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (roleFilter != null && !roleFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleFilter.trim())) {
                ps.setString(1, roleFilter.trim().toUpperCase());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", rs.getInt("user_id"));
                    map.put("username", rs.getString("username"));
                    map.put("fullName", rs.getString("full_name"));
                    map.put("email", rs.getString("email"));
                    map.put("phoneNo", rs.getString("phone_no"));
                    map.put("role", rs.getString("role"));
                    map.put("isActive", rs.getInt("is_active") == 1);
                    map.put("failedLogins", rs.getInt("failed_logins"));
                    map.put("departmentName", rs.getString("department_name"));

                    String refId = "-";
                    if ("UNDERGRADUATE".equalsIgnoreCase(rs.getString("role"))) {
                        refId = rs.getString("student_id") != null ? rs.getString("student_id") : "-";
                    } else if ("LECTURER".equalsIgnoreCase(rs.getString("role"))) {
                        refId = rs.getString("lec_emp_id") != null ? rs.getString("lec_emp_id") : "-";
                    } else if ("TECH_OFFICER".equalsIgnoreCase(rs.getString("role"))) {
                        refId = rs.getString("to_emp_id") != null ? rs.getString("to_emp_id") : "-";
                    }
                    map.put("refId", refId);

                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error loading users: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Consolidated system counts for the Administrator Overview (FR-ADM-05).
     */
    public Map<String, Integer> getSystemStats() throws AdminOperationException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalUsers", 0);
        stats.put("students", 0);
        stats.put("lecturers", 0);
        stats.put("techOfficers", 0);
        stats.put("courses", 0);
        stats.put("notices", 0);

        String sql = "SELECT 'totalUsers' AS stat, COUNT(*) AS count FROM users " +
                     "UNION SELECT 'students', COUNT(*) FROM undergraduate " +
                     "UNION SELECT 'lecturers', COUNT(*) FROM lecturer " +
                     "UNION SELECT 'techOfficers', COUNT(*) FROM technical_officer " +
                     "UNION SELECT 'courses', COUNT(*) FROM course " +
                     "UNION SELECT 'notices', COUNT(*) FROM notice";

        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stats.put(rs.getString("stat"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error loading system statistics: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Returns list of all departments.
     */
    public List<Department> getAllDepartments() throws AdminOperationException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, department_name, department_code FROM department ORDER BY department_id";
        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("department_code")
                ));
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error loading departments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Returns list of all lecturers for assignment dropdowns.
     */
    public List<Map<String, Object>> getAllLecturers() throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT l.lecturer_id, l.employee_id, u.full_name, d.department_name " +
                     "FROM lecturer l " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "JOIN department d ON l.department_id = d.department_id " +
                     "ORDER BY u.full_name";

        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("lecturerId", rs.getInt("lecturer_id"));
                map.put("employeeId", rs.getString("employee_id"));
                map.put("fullName", rs.getString("full_name"));
                map.put("departmentName", rs.getString("department_name"));
                list.add(map);
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error loading lecturers: " + e.getMessage(), e);
        }
        return list;
    }
}
