package testing;

import dao.AttendanceDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TestAttendanceDAO {

    public static void main(String[] args) {

        AttendanceDAO dao = new AttendanceDAO();

        String courseCode = "ICT1122";
        Date sessionDate = Date.valueOf("2026-08-10");
        String sessionType = "THEORY";

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
                        "Student ID: " + student[0]
                                + " | Name: " + student[1]
                                + " | Attendance: " + student[2]
                );
            }

        } catch (SQLException e) {
            System.out.println("Attendance test failed!");
            e.printStackTrace();
        }
    }
}