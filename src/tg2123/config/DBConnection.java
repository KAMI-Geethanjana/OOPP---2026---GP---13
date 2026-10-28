package tg2123.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * TG2123 access point for database connections.
 * Delegates to the shared connection utility so every FTMS module uses the same configuration.
 */
public final class DBConnection {
    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        return common.config.DBConnection.getConnection();
    }

    public static void closeConnection() {
        common.config.DBConnection.closeConnection();
    }
}
