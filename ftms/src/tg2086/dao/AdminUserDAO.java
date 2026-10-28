package tg2086.dao;

import common.config.DBConnection;
import common.model.Department;
import tg2086.exception.AdminOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminUserDAO - Data Access Object for complete User Profile Lifecycle.
 * - Interface Implementation (implements UserOperations)
 * - Exception Handling (throws custom AdminOperationException)
 * - Method Overloading (deleteUser by ID vs username, getAllUsers with/without filter)
 * - Database Handling (JDBC transactions with rollback, PreparedStatements)
 * - Default and static interface methods invocation
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminUserDAO implements UserOperations {

    /**
     * Atomically creates a user and their role-specific record (Undergraduate, Lecturer, Technical Officer).
     */
    @Override
    public boolean createUser(String username, String password, String fullName, String email,
                              String phoneNo, String role, String extraId, int departmentId, String batch)
            throws AdminOperationException {

        // Validate role using static method from UserOperations interface
        if (!UserOperations.isValidRole(role)) {
            throw new AdminOperationException("Invalid user role specified: " + role);
        }

        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, phone_no, role, is_active, failed_logins) " +
                               "VALUES (?, ?, ?, ?, ?, ?, 1, 0)";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psRole = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin JDBC transaction

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

            int generatedUserId;
            try (ResultSet rs = psUser.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getInt(1);
                } else {
                    conn.rollback();
                    throw new AdminOperationException("Failed to retrieve generated user ID.");
                }
            }

            // Polymorphic Profile insertion based on role
            String roleUpper = role.toUpperCase().trim();
            if ("UNDERGRADUATE".equals(roleUpper)) {
                String ugSql = "INSERT INTO undergraduate (student_id, user_id, department_id, batch) VALUES (?, ?, ?, ?)";
                psRole = conn.prepareStatement(ugSql);
                psRole.setString(1, (extraId != null && !extraId.isEmpty()) ? extraId.trim() : "TG/" + username);
                psRole.setInt(2, generatedUserId);
                psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                psRole.setString(4, (batch != null && !batch.isEmpty()) ? batch.trim() : "2023/2024");
                psRole.executeUpdate();

            } else if ("LECTURER".equals(roleUpper)) {
                String lecSql = "INSERT INTO lecturer (user_id, employee_id, department_id) VALUES (?, ?, ?)";
                psRole = conn.prepareStatement(lecSql);
                psRole.setInt(1, generatedUserId);
                psRole.setString(2, (extraId != null && !extraId.isEmpty()) ? extraId.trim() : "EMP/" + username);
                psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                psRole.executeUpdate();

            } else if ("TECH_OFFICER".equals(roleUpper)) {
                String toSql = "INSERT INTO technical_officer (user_id, employee_id, department_id) VALUES (?, ?, ?)";
                psRole = conn.prepareStatement(toSql);
                psRole.setInt(1, generatedUserId);
                psRole.setString(2, (extraId != null && !extraId.isEmpty()) ? extraId.trim() : "EMP/" + username);
                psRole.setInt(3, departmentId > 0 ? departmentId : 1);
                psRole.executeUpdate();
            }

            conn.commit(); // Commit transaction successfully
            logAudit("CREATE_USER", "Successfully created user [" + username + "] with role " + role);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new AdminOperationException("Database error creating user: " + e.getMessage(), e);
        } finally {
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
     * Updates an existing user and cascading role attributes.
     */
    @Override
    public boolean updateUser(int userId, String fullName, String email, String phoneNo,
                              String role, boolean isActive, String extraId, int departmentId, String batch)
            throws AdminOperationException {

        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, phone_no = ?, is_active = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psRole = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psUser = conn.prepareStatement(updateUserSql);
            psUser.setString(1, fullName.trim());
            psUser.setString(2, email != null ? email.trim() : "");
            psUser.setString(3, phoneNo != null ? phoneNo.trim() : "");
            psUser.setBoolean(4, isActive);
            psUser.setInt(5, userId);

            int rows = psUser.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }

            String roleUpper = role != null ? role.toUpperCase().trim() : "";
            if ("UNDERGRADUATE".equals(roleUpper)) {
                String sql = "UPDATE undergraduate SET student_id = ?, department_id = ?, batch = ? WHERE user_id = ?";
                psRole = conn.prepareStatement(sql);
                psRole.setString(1, extraId != null ? extraId.trim() : "");
                psRole.setInt(2, departmentId > 0 ? departmentId : 1);
                psRole.setString(3, batch != null ? batch.trim() : "");
                psRole.setInt(4, userId);
                psRole.executeUpdate();

            } else if ("LECTURER".equals(roleUpper)) {
                String sql = "UPDATE lecturer SET employee_id = ?, department_id = ? WHERE user_id = ?";
                psRole = conn.prepareStatement(sql);
                psRole.setString(1, extraId != null ? extraId.trim() : "");
                psRole.setInt(2, departmentId > 0 ? departmentId : 1);
                psRole.setInt(3, userId);
                psRole.executeUpdate();

            } else if ("TECH_OFFICER".equals(roleUpper)) {
                String sql = "UPDATE technical_officer SET employee_id = ?, department_id = ? WHERE user_id = ?";
                psRole = conn.prepareStatement(sql);
                psRole.setString(1, extraId != null ? extraId.trim() : "");
                psRole.setInt(2, departmentId > 0 ? departmentId : 1);
                psRole.setInt(3, userId);
                psRole.executeUpdate();
            }

            conn.commit();
            logAudit("UPDATE_USER", "Successfully updated user ID [" + userId + "]");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new AdminOperationException("Database error updating user: " + e.getMessage(), e);
        } finally {
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
     * Method Overloading: Delete user by integer user ID.
     */
    @Override
    public boolean deleteUser(int userId) throws AdminOperationException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            logAudit("DELETE_USER", "Deleted user ID [" + userId + "], Rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Delete user by string username.
     */
    @Override
    public boolean deleteUser(String username) throws AdminOperationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AdminOperationException("Cannot delete user with empty username.");
        }
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            int rows = ps.executeUpdate();
            logAudit("DELETE_USER", "Deleted username [" + username + "], Rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting user [" + username + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Retrieves all users without filter.
     */
    @Override
    public List<Map<String, Object>> getAllUsers() throws AdminOperationException {
        return getAllUsers(null);
    }

    /**
     * Method Overloading: Retrieves users filtered by role.
     */
    @Override
    public List<Map<String, Object>> getAllUsers(String roleFilter) throws AdminOperationException {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.username, u.full_name, u.email, u.phone_no, u.role, u.is_active, u.failed_logins, " +
                "COALESCE(ug.student_id, l.employee_id, t.employee_id, '') AS extra_id, " +
                "COALESCE(ug.department_id, l.department_id, t.department_id, 0) AS department_id, " +
                "COALESCE(d.department_name, 'N/A') AS department_name, " +
                "COALESCE(ug.batch, '') AS batch " +
                "FROM users u " +
                "LEFT JOIN undergraduate ug ON u.user_id = ug.user_id " +
                "LEFT JOIN lecturer l ON u.user_id = l.user_id " +
                "LEFT JOIN technical_officer t ON u.user_id = t.user_id " +
                "LEFT JOIN department d ON d.department_id = COALESCE(ug.department_id, l.department_id, t.department_id) "
        );

        if (roleFilter != null && !roleFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleFilter.trim())) {
            sql.append("WHERE u.role = ? ");
        }
        sql.append("ORDER BY u.user_id ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (roleFilter != null && !roleFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleFilter.trim())) {
                ps.setString(1, roleFilter.toUpperCase().trim());
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
                    map.put("isActive", rs.getBoolean("is_active"));
                    map.put("failedLogins", rs.getInt("failed_logins"));
                    map.put("extraId", rs.getString("extra_id"));
                    map.put("departmentId", rs.getInt("department_id"));
                    map.put("departmentName", rs.getString("department_name"));
                    map.put("batch", rs.getString("batch"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving users: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Unlocks a locked user account (resets failed_logins to 0 and is_active to 1).
     */
    @Override
    public boolean unlockAccount(int userId) throws AdminOperationException {
        String sql = "UPDATE users SET failed_logins = 0, is_active = 1 WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            logAudit("UNLOCK_ACCOUNT", "Account unlocked for user ID [" + userId + "]");
            return rows > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error unlocking account: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all registered faculty departments.
     */
    public List<Department> getAllDepartments() throws AdminOperationException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, department_name, department_code FROM department ORDER BY department_id ASC";
        try (Connection conn = DBConnection.getConnection();
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
            throw new AdminOperationException("Database error fetching departments: " + e.getMessage(), e);
        }
        return list;
    }
}
