
package tg2123.testing;

import tg2123.model.TechnicalOfficer;
import tg2123.gui.MedicalPanel;

import javax.swing.*;

public class TestMedicalPanel {

    private static TechnicalOfficer createTestOfficer() {
        TechnicalOfficer officer = new TechnicalOfficer(1, "testuser", "", "Test Officer",
                "test@example.com", "0770000000", 1, "EMP/TO/2001", 1);
        officer.setDepartmentName("Department 1");
        return officer;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Medical Panel");

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            try {
                MedicalPanel panel = new MedicalPanel(createTestOfficer());

                frame.add(panel);
                frame.setVisible(true);

                System.out.println("MedicalPanel opened successfully.");

            } catch (Exception ex) {

                System.err.println("MedicalPanel test failed:");
                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        frame,
                        "Could not open Medical Panel:\n"
                                + ex.getMessage(),
                        "Test Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
