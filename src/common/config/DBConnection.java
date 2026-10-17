package common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Management
 * Handles JDBC connection pooling and ensures a single reusable connection.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2004"; // MySQL root password

    private static Connection connection = null;

    // Private constructor to prevent direct instantiation (Singleton pattern)
    private DBConnection() {}

    /**
     * Obtains the active database connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found in classpath!");
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Closes the active connection if open.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
