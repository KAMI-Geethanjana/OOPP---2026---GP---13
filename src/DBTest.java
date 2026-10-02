import config.DBConn;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try {
            Connection conn = DBConn.getConn();

            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connected successfully!");
                System.out.println("Database: " + conn.getCatalog());
            }

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}