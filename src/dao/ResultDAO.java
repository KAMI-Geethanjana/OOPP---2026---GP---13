package dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class ResultDAO {

    // Main.java එකේ පාවිච්චි කළ DB connection logic එක
    private Connection getConnection() throws Exception {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new Exception("Unable to find db.properties");
            }
            props.load(input);
            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );
        }
    }

    // Grade ගණනය කිරීමේ සරල Helper Methods
    private String calculateGrade(double totalMark) {
        if (totalMark >= 85) return "A+";
        if (totalMark >= 80) return "A";
        if (totalMark >= 75) return "A-";
        if (totalMark >= 70) return "B+";
        if (totalMark >= 65) return "B";
        if (totalMark >= 60) return "B-";
        if (totalMark >= 55) return "C+";
        if (totalMark >= 50) return "C";
        if (totalMark >= 45) return "C-";
        if (totalMark >= 40) return "D+";
        if (totalMark >= 35) return "D";
        return "E";
    }

    private double calculateGPAPoints(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.00;
            case "A-": return 3.70;
            case "B+": return 3.30;
            case "B": return 3.00;
            case "B-": return 2.70;
            case "C+": return 2.30;
            case "C": return 2.00;
            case "C-": return 1.70;
            case "D+": return 1.30;
            case "D": return 1.00;
            default: return 0.00;
        }
    }

    public boolean calculateAndSaveResult(int enrollmentId) {
        String query = "SELECT " +
                "  SUM(CASE WHEN a.component_type = 'CA' THEN (IFNULL(m.marks, 0) * a.weight / 100) ELSE 0 END) * 100 / " +
                "  NULLIF(SUM(CASE WHEN a.component_type = 'CA' THEN a.weight ELSE 0 END), 0) AS ca_percentage, " +
                "  MAX(CASE WHEN a.component_type = 'FINAL' THEN m.marks ELSE NULL END) AS final_exam_mark " +
                "FROM enrollment e " +
                "JOIN assessment a ON a.course_code = e.course_code " +
                "LEFT JOIN mark m ON m.enrollment_id = e.enrollment_id AND m.assessment_id = a.assessment_id " +
                "WHERE e.enrollment_id = ?";

        Double caMark = null;
        Double finalMark = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                caMark = rs.getObject("ca_percentage") != null ? rs.getDouble("ca_percentage") : 0.0;
                Object finalObj = rs.getObject("final_exam_mark");
                if (finalObj != null) {
                    finalMark = rs.getDouble("final_exam_mark");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String eligibilityStatus = (caMark != null && caMark >= 40.0) ? "ELIGIBLE" : "NOT_ELIGIBLE";
        Double totalMark = null;
        String grade = null;
        Double gpaPoints = null;

        if ("ELIGIBLE".equals(eligibilityStatus) && finalMark != null) {
            totalMark = (caMark * 0.40) + (finalMark * 0.60);
            grade = calculateGrade(totalMark);
            gpaPoints = calculateGPAPoints(grade);
        }

        String upsertQuery = "INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "ca_mark = VALUES(ca_mark), " +
                "final_exam_mark = VALUES(final_exam_mark), " +
                "total_mark = VALUES(total_mark), " +
                "eligibility_status = VALUES(eligibility_status), " +
                "grade = VALUES(grade), " +
                "grade_point = VALUES(grade_point)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(upsertQuery)) {

            stmt.setInt(1, enrollmentId);
            stmt.setObject(2, caMark);
            stmt.setObject(3, finalMark);
            stmt.setObject(4, totalMark);
            stmt.setString(5, eligibilityStatus);
            stmt.setObject(6, grade);
            stmt.setObject(7, gpaPoints);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}