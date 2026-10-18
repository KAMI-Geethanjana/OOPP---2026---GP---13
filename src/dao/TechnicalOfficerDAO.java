
package dao;

import config.DBConnection;
import model.TechnicalOfficer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TechnicalOfficerDAO {

    public TechnicalOfficer getTechnicalOfficerByUserId(int userId)
            throws SQLException {

        String sql = """
                SELECT t.to_id, t.user_id, t.employee_id,
                       t.department_id, d.department_name,
                       u.full_name, u.email, u.phone_no
                FROM technical_officer t
                JOIN users u ON t.user_id = u.user_id
                LEFT JOIN department d
                    ON t.department_id = d.department_id
                WHERE t.user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TechnicalOfficer(
                            rs.getInt("to_id"),
                            rs.getInt("user_id"),
                            rs.getString("employee_id"),
                            rs.getInt("department_id"),
                            rs.getString("department_name"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone_no")
                    );
                }
            }
        }

        return null;
    }

    public boolean updateProfile(
            int userId, String fullName,
            String email, String phoneNo) throws SQLException {

        String sql = """
                UPDATE users
                SET full_name = ?, email = ?, phone_no = ?
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phoneNo);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
