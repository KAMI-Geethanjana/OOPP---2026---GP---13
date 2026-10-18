package testing;

import dao.TimetableDAO;
import java.sql.SQLException;
import java.util.List;

public class TestTimetableDAO {

    public static void main(String[] args) {
        TimetableDAO dao = new TimetableDAO();

        // Replace 1 with an existing department ID.
        int departmentId = 1;

        try {
            List<String[]> timetable =
                    dao.getTimetableByDepartment(departmentId);

            System.out.println(
                    "Timetable records found: " + timetable.size()
            );

            for (String[] row : timetable) {
                System.out.println(
                        String.join(" | ", row)
                );
            }

        } catch (SQLException e) {
            System.out.println("Timetable DAO test failed.");
            e.printStackTrace();
        }
    }
}