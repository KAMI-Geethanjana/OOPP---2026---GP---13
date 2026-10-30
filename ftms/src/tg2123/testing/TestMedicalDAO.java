package tg2123.testing;

import tg2123.dao.MedicalDAO;
import java.sql.SQLException;
import java.util.List;

public class TestMedicalDAO {

    public static void main(String[] args) {
        MedicalDAO dao = new MedicalDAO();

        try {
            List<String[]> requests = dao.getMedicalRequestsForDepartment(1);

            System.out.println(
                    "Medical requests found: " + requests.size()
            );

            for (String[] request : requests) {
                System.out.println(
                        "ID: " + request[0]
                                + " | Student: " + request[1]
                                + " | Date: " + request[2]
                                + " | Reason: " + request[3]
                                + " | Status: " + request[4]
                );
            }

        } catch (SQLException e) {
            System.out.println("Medical DAO test failed.");
            e.printStackTrace();
        }
    }
}