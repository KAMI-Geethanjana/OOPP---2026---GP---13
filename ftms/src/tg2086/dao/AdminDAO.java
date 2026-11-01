package tg2086.dao;

import common.config.DBConnection;
import tg2086.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminDAO - Data Access Object for Admin Profile and Authentication Integration.
 * Called dynamically by common.auth.AuthDAO via reflection:
 *   loadRoleUser("tg2086.dao.AdminDAO", "getAdminByUserId", userId)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminDAO {

    /**
     * Retrieves an Admin instance by user_id.
     * Required contract for common.auth.AuthDAO.
     */
    public Admin getAdminByUserId(int userId) {
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no, " +
                     "u.profile_pic, u.role, u.is_active, u.failed_logins " +
                     "FROM users u " +
                     "WHERE u.user_id = ? AND u.role = 'ADMIN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone_no"),
                            1,
                            "SUPER_ADMIN"
                    );
                    admin.setProfilePic(rs.getString("profile_pic"));
                    admin.setActive(rs.getBoolean("is_active"));
                    admin.setFailedLogins(rs.getInt("failed_logins"));
                    return admin;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching admin profile: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches high-level system metrics for the dashboard KPI cards.
     */
    public Map<String, Integer> getDashboardMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("totalUsers", 0);
        metrics.put("students", 0);
        metrics.put("lecturers", 0);
        metrics.put("techOfficers", 0);
        metrics.put("courses", 0);
        metrics.put("notices", 0);

        try (Connection conn = DBConnection.getConnection()) {
            // Count users by role
            String userSql = "SELECT role, COUNT(*) AS cnt FROM users GROUP BY role";
            try (PreparedStatement ps = conn.prepareStatement(userSql);
                 ResultSet rs = ps.executeQuery()) {
                int total = 0;
                while (rs.next()) {
                    String role = rs.getString("role");
                    int count = rs.getInt("cnt");
                    total += count;
                    if ("UNDERGRADUATE".equalsIgnoreCase(role)) metrics.put("students", count);
                    else if ("LECTURER".equalsIgnoreCase(role)) metrics.put("lecturers", count);
                    else if ("TECH_OFFICER".equalsIgnoreCase(role)) metrics.put("techOfficers", count);
                }
                metrics.put("totalUsers", total);
            }

            // Count courses
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM course");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) metrics.put("courses", rs.getInt(1));
            }

            // Count notices
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM notice");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) metrics.put("notices", rs.getInt(1));
            }

        } catch (SQLException ex) {
            System.err.println("Error calculating dashboard metrics: " + ex.getMessage());
        }
        return metrics;
    }
}
