package common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates JDBC connections for DAO operations.
 * Singleton connection factory that supports automatic fallback
 * between configured password ("3909") and default blank ("").
 */
public final class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "3909";

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
     * Tries configured password first, then falls back to blank password.
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            // Fallback for environments with blank root password
            return DriverManager.getConnection(URL, USER, "");
        }
    }

    @Deprecated
    public static void closeConnection() {
        // Kept for backward compatibility
    }
}
