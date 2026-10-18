
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

        List<String[]> requests = new ArrayList<>();

        String sql = """
                SELECT m.medical_id, u.full_name,
                       m.medical_date, m.reason, m.status
                FROM medical m
                JOIN undergraduate ug
                    ON m.undergraduate_id = ug.undergraduate_id
                JOIN users u ON ug.user_id = u.user_id
                ORDER BY m.medical_date DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                requests.add(new String[]{
                        rs.getString("medical_id"),
                        rs.getString("full_name"),
                        rs.getString("medical_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                });
            }
        }

        return requests;
    }

    public boolean updateMedicalStatus(
            int medicalId, String status) throws SQLException {

        String sql = """
                UPDATE medical
                SET status = ?
                WHERE medical_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, medicalId);

            return ps.executeUpdate() > 0;
        }
    }
}
