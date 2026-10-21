
package testing;

import gui.MedicalPanel;

import javax.swing.*;

public class TestMedicalPanel {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Medical Panel");

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            try {
                MedicalPanel panel = new MedicalPanel();

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
