package common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates JDBC connections for DAO operations.
 * Each call returns a new connection so try-with-resources can close it safely.
 */
public final class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ftms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    // Set this to your local MySQL password before running the project.
    private static final String PASSWORD = "";

    private DBConnection() {
        // Prevent objects of this utility class from being created.
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver was not found. Add Connector/J to the project libraries.", ex);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Kept for compatibility; individual DAO methods close their own connections. */
    @Deprecated
    public static void closeConnection() {
        // No shared connection is stored. Use try-with-resources in each DAO.
    }
}
