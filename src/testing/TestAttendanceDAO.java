package testing;

import dao.AttendanceDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TestAttendanceDAO {

    public static void main(String[] args) {

        AttendanceDAO dao = new AttendanceDAO();

        String courseCode = "ICT2132";
        Date sessionDate = Date.valueOf("2026-10-18");
        String sessionType = "LECTURE";

        try {

            List<String[]> students =
                    dao.getStudentsForAttendance(
                            courseCode,
                            sessionDate,
                            sessionType
                    );

            System.out.println("Students found: " + students.size());

            for (String[] student : students) {
                System.out.println(
                        "Student ID: " + student[0] +
                                " | Name: " + student[1] +
                                " | Status: " + student[2]
                );
            }

        } catch (SQLException e) {
            System.out.println("Attendance test failed!");
            e.printStackTrace();
        }
    }
}