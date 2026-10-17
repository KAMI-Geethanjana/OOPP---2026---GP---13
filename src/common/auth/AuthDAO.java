package common.auth;

import common.config.DBConnection;
import common.exception.AccountLockedException;
import common.model.User;
import tg2086.dao.AdminDAO;
import tg2088.dao.LecturerDAO;
import tg2123.dao.TechnicalOfficerDAO;
import tg2082.dao.UndergraduateDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AuthDAO - Handles user authentication, credential checking,
 * failed login attempt counting, and account locking (FR-AUTH-05).
 */
public class AuthDAO {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    public User authenticate(String username, String password) throws AccountLockedException, SQLException {
        String query = "SELECT user_id, username, password_hash, role, is_active, failed_logins FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String passHash = rs.getString("password_hash");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                int failedCount = rs.getInt("failed_logins");

                // Check account lock (FR-AUTH-05)
                if (!isActive || failedCount >= MAX_FAILED_ATTEMPTS) {
                    throw new AccountLockedException("Your account is locked due to 5 consecutive failed login attempts. Please contact System Administrator.");
                }

                // Password match check
                if (password.equals(passHash)) {
                    // Reset failed login count on successful login
                    resetFailedLogins(userId);

                    // Polymorphically return the specific role subclass
                    return loadUserSubclass(userId, role);
                } else {
                    // Increment failed login count
                    incrementFailedLogins(userId, failedCount + 1);
                    return null;
                }
            }
        }
        return null;
    }

    private void incrementFailedLogins(int userId, int newCount) {
        String sql = "UPDATE users SET failed_logins = ?, is_active = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setBoolean(2, newCount < MAX_FAILED_ATTEMPTS);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetFailedLogins(int userId) {
        String sql = "UPDATE users SET failed_logins = 0 WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User loadUserSubclass(int userId, String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return new AdminDAO().getAdminByUserId(userId);
            case "LECTURER":
                return new LecturerDAO().getLecturerByUserId(userId);
            case "TECH_OFFICER":
                return new TechnicalOfficerDAO().getTechnicalOfficerByUserId(userId);
            case "UNDERGRADUATE":
                return new UndergraduateDAO().getUndergraduateByUserId(userId);
            default:
                return null;
        }
    }
}
