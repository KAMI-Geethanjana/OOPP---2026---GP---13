package tg2123.testing;

import tg2123.gui.AttendancePanel;
import tg2123.model.TechnicalOfficer;

import javax.swing.*;

public class TestAttendancePanel {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            TechnicalOfficer officer = new TechnicalOfficer();

            // Assign an existing Technical Officer ID from the database
            officer.setToId(1);
            officer.setDepartmentId(1);
            officer.setEmployeeId("EMP/TO/2001");

            JFrame frame = new JFrame("Test Attendance Panel");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 500);
            frame.setLocationRelativeTo(null);

            frame.setContentPane(new AttendancePanel(officer));

            frame.setVisible(true);
        });
    }
}