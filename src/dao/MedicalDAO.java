
package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public List<String[]> getAllMedicalRequests() throws SQLException {

        List<String[]> medicalRequests = new ArrayList<>();

        String sql =
                "SELECT m.medical_id, ug.reg_no, u.full_name, " +
                        "m.medical_date, m.reason, m.status " +
                        "FROM medical m " +
                        "JOIN undergraduate ug ON m.student_id = ug.student_id " +
                        "JOIN users u ON ug.user_id = u.user_id " +
                        "ORDER BY m.medical_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

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

        return medicalRequests;
    }

    public boolean updateMedicalStatus(int medicalId, String status)
            throws SQLException {

        if (status == null ||
                !(status.equals("PENDING")
                        || status.equals("APPROVED")
                        || status.equals("REJECTED"))) {
            throw new IllegalArgumentException(
                    "Invalid medical status: " + status);
        }

        String sql =
                "UPDATE medical SET status = ? WHERE medical_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, medicalId);

            return statement.executeUpdate() > 0;
        }
    }
}
