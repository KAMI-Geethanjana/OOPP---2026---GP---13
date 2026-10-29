package common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates JDBC connections for DAO operations.
 * Multi-environment connection factory supporting configured, team-member,
 * and default database passwords automatically.
 */
public final class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";

    // Known candidate passwords across team members and common local dev environments
    private static final String[] CANDIDATE_PASSWORDS = {
            "2004", // TG2088 (Wosada)
            "3909", // TG2086 (Geethanjana)
            "",     // Default blank (XAMPP / root without password)
            "root",
            "1234"
    };

    private static volatile String cachedWorkingPassword = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("MySQL JDBC Driver not found: " + ex.getMessage());
        }
    }

    private DBConnection() {
        // Prevent instantiation
    }

    /**
     * Obtains an active database connection.
     * Checks cached working password first, then candidate passwords in sequence.
     */
    public static Connection getConnection() throws SQLException {
        // 1. Try environment variable or system property if specified
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass == null) {
            envPass = System.getProperty("db.password");
        }
        if (envPass != null) {
            try {
                return DriverManager.getConnection(URL, USER, envPass);
            } catch (SQLException ignored) {}
        }

        // 2. Try previously cached working password
        if (cachedWorkingPassword != null) {
            try {
                return DriverManager.getConnection(URL, USER, cachedWorkingPassword);
            } catch (SQLException ignored) {
                cachedWorkingPassword = null; // Invalidate cache if connection failed
            }
        }

        // 3. Try each known password in order
        SQLException lastException = null;
        for (String pass : CANDIDATE_PASSWORDS) {
            try {
                Connection conn = DriverManager.getConnection(URL, USER, pass);
                if (conn != null && !conn.isClosed()) {
                    cachedWorkingPassword = pass;
                    return conn;
                }
            } catch (SQLException ex) {
                lastException = ex;
            }
        }

        throw lastException != null ? lastException : new SQLException("Failed to connect to MySQL database at " + URL);
    }

    @Deprecated
    public static void closeConnection() {
        // Kept for backward compatibility
    }
}
