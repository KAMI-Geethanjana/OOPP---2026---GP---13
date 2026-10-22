package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager for FTMS Admin Module.
 * Demonstrates:
 * 1. Singleton Design Pattern (Private constructor preventing instantiation - Lesson 02 Part 02)
 * 2. Static Initializer block (Executed once when class is loaded - Lesson 02 Part 02 Slide 28)
 * 3. Static constants (final fields - Lesson 02 Slide 20-21)
 * 4. JDBC Connection Factory (Lesson 07)
 */
public final class DBConn {

    // Static constants
    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "3909";

    // Static Initializer block (Lesson 02 Part 02)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Private constructor to prevent direct instantiation.
     * Demonstrates Lesson 02 Part 02 (Slide 31: "Preventing instantiating a class").
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
