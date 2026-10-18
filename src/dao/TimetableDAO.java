
package dao;

import config.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public List<String[]> getTimetableByDepartment(
            int departmentId) throws SQLException {

        List<String[]> timetable = new ArrayList<>();

        String sql = """
                SELECT *
                FROM timetable
                WHERE department_id = ?
                """;

        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();

                while (rs.next()) {
                    String[] row = new String[columnCount];

                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getString(i + 1);
                    }

                    timetable.add(row);
                }
            }
        }

        return timetable;
    }
}
