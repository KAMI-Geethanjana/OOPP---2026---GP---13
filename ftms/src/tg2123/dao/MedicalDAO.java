
package tg2123.dao;

import tg2123.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public List<String[]> getMedicalRequestsForDepartment(int departmentId) throws SQLException {

        List<String[]> medicalRequests = new ArrayList<>();

        String sql =
                "SELECT m.medical_id, ug.reg_no, u.full_name, " +
                        "m.medical_date, m.reason, m.status " +
                        "FROM medical m " +
                        "JOIN undergraduate ug ON m.student_id = ug.student_id " +
                        "JOIN users u ON ug.user_id = u.user_id " +
                        "JOIN course c ON c.course_code = m.course_code " +
                        "WHERE c.department_id = ? ORDER BY m.medical_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {
            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String[] request = {
                            String.valueOf(resultSet.getInt("medical_id")),
                            resultSet.getString("reg_no"),
                            resultSet.getString("full_name"),
                            String.valueOf(resultSet.getDate("medical_date")),
                            resultSet.getString("reason"),
                            resultSet.getString("status")
                    };

                    medicalRequests.add(request);
                }
            }
        }

        return medicalRequests;
    }

    public boolean updateMedicalStatus(
            int medicalId,
            String status,
            int departmentId,
            int toId
    ) throws SQLException {

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new IllegalArgumentException(
                    "Invalid medical status: " + status
            );
        }

        String sql =
                "UPDATE medical m " +
                        "JOIN course c ON c.course_code = m.course_code " +
                        "SET m.status = ?, m.reviewed_by_to_id = ? " +
                        "WHERE m.medical_id = ? " +
                        "AND c.department_id = ? " +
                        "AND m.status = 'PENDING'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, toId);
            statement.setInt(3, medicalId);
            statement.setInt(4, departmentId);

            return statement.executeUpdate() > 0;
        }
    }

    public List<String[]> getStudentsForDepartment(int departmentId)
            throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql =
                "SELECT student_id, reg_no, full_name " +
                        "FROM undergraduate ug " +
                        "JOIN users u ON ug.user_id = u.user_id " +
                        "WHERE ug.department_id = ? " +
                        "ORDER BY reg_no";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, departmentId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String[] student = {
                            String.valueOf(rs.getInt("student_id")),
                            rs.getString("reg_no"),
                            rs.getString("full_name")
                    };

                    students.add(student);
                }
            }
        }

        return students;
    }

    public List<String> getCoursesForStudent(int studentId)
            throws SQLException {

        List<String> courses = new ArrayList<>();

        String sql =
                "SELECT DISTINCT course_code " +
                        "FROM enrollment " +
                        "WHERE student_id = ? " +
                        "ORDER BY course_code";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString("course_code"));
                }
            }
        }

        return courses;
    }

    public boolean addMedicalRequest(
            int studentId,
            String courseCode,
            java.sql.Date medicalDate,
            String reason
    ) throws SQLException {

        String sql =
                "INSERT INTO medical " +
                        "(student_id, course_code, medical_date, reason, status) " +
                        "VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setString(2, courseCode);
            statement.setDate(3, medicalDate);
            statement.setString(4, reason.trim());

            return statement.executeUpdate() > 0;
        }
    }
}
