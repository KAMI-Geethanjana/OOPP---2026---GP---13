
package testing;

import gui.ProfilePanel;
import model.TechnicalOfficer;

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
                        1,
                        7,
                        "EMP/TO/2001",
                        1,
                        "Department 1",
                        "",
                        "",
                        ""
                );

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
