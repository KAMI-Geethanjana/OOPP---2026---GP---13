package testing;

import config.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {
                System.out.println(
                        "Database connected successfully!"
                );
                System.out.println(
                        "Database: " +
                                con.getCatalog()
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database connection failed!"
            );
            e.printStackTrace();
        }
    }
}