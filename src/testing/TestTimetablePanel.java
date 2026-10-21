
package testing;

import gui.TimetablePanel;
import model.TechnicalOfficer;

import javax.swing.*;

public class TestTimetablePanel {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Timetable Panel");

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            try {
                TechnicalOfficer officer = new TechnicalOfficer(
                        1,
                        7,
                        "EMP/TO/2001",
                        1,
                        "Department 1",
                        "",
                        "",
                        ""
                );

                TimetablePanel panel = new TimetablePanel(officer);

                frame.add(panel);
                frame.setVisible(true);

                System.out.println("TimetablePanel opened successfully.");

            } catch (Exception ex) {

                System.err.println("TimetablePanel test failed:");
                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        frame,
                        "Could not open Timetable Panel:\n"
                                + ex.getMessage(),
                        "Test Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
