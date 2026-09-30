import dao.ResultDAO;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                return;
            }
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            try (Connection con = DriverManager.getConnection(url, user, password)) {
                System.out.println("✅ Connected using db.properties file!");

                // --- MARKS & GRADING CALCULATION TEST ---
                ResultDAO resultDAO = new ResultDAO();
                int[] testEnrollments = {101, 102, 103, 104};

                System.out.println("\n========== FTMS MARKS & GRADING TEST ==========");
                for (int id : testEnrollments) {
                    boolean success = resultDAO.calculateAndSaveResult(id);
                    System.out.println("Enrollment ID " + id + " Result Calculation: " + (success ? "SUCCESS" : "FAILED"));
                }
                System.out.println("===============================================");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}