
package tg2123.testing;

import tg2123.dao.TimetableDAO;
import java.sql.SQLException;
import java.util.List;

public class TestTimetableDAO {

    public static void main(String[] args) {

        // Create an object of TimetableDAO.
        TimetableDAO dao = new TimetableDAO();

        // Use a department ID that exists in your database.
        int departmentId = 1;

        try {

            // Retrieve timetable records for this department.
            List<String[]> timetable =
                    dao.getTimetableByDepartment(departmentId);

            // Display the number of records found.
            System.out.println(
                    "Timetable records found: " + timetable.size()
            );

            // Check whether any records were returned.
            if (timetable.isEmpty()) {
                System.out.println(
                        "No timetable records found for department "
                                + departmentId
                );
            } else {

                // Display each timetable record.
                for (String[] row : timetable) {
                    System.out.println(String.join(" | ", row));
                }
            }

        } catch (SQLException e) {

            // Display an error if the database operation fails.
            System.out.println("Could not retrieve the timetable.");
            e.printStackTrace();
        }
    }
}
