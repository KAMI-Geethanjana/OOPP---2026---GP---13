import common.auth.LoginFrame;

import javax.swing.*;

/**
 * Main - Application Entry Point for the Faculty Technical Management System (FTMS).
 * Initializes desktop UI settings and launches the Central Login Authentication Portal.
 */
public class Main {
    public static void main(String[] args) {
        // Set native system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
