
package tg2123.testing;

import tg2123.gui.ProfilePanel;
import tg2123.model.TechnicalOfficer;

import javax.swing.*;

public class TestProfilePanel {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Profile Panel");

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(600, 350);
            frame.setLocationRelativeTo(null);

            try {
                TechnicalOfficer officer = new TechnicalOfficer(
                        7, "testuser", "", "Test Officer", "test@example.com",
                        "0770000000", 1, "EMP/TO/2001", 1
                );
                officer.setDepartmentName("Department 1");

                ProfilePanel panel = new ProfilePanel(officer);

                frame.add(panel);
                frame.setVisible(true);

                System.out.println("ProfilePanel opened successfully.");

            } catch (Exception ex) {

                System.err.println("ProfilePanel test failed:");
                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        frame,
                        "Could not open Profile Panel:\n"
                                + ex.getMessage(),
                        "Test Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
