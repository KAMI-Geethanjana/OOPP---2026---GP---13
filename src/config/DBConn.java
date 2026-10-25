package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager for FTMS Admin Module.
 * - Singleton Design Pattern (Private constructor preventing direct instantiation)
 * - Static Initializer block (Executed once when class is loaded)
 * - Static constants (final connection configuration fields)
 * - JDBC Connection Factory
 */
public final class DBConn {

    // Static constants
    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "3909";

    // Static Initializer block for JDBC driver registration
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Private constructor to prevent direct instantiation (Singleton pattern).
     */
    private DBConn() {
        // Prevent reflection invocation
    }

    /**
     * Returns an active database connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
