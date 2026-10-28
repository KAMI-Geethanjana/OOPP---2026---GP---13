import common.auth.LoginFrame;

import javax.swing.*;

/**
 * Main - Application Entry Point for FTMS.
 * Initializes desktop UI settings and displays LoginFrame.
 */
public class Main {
    public static void main(String[] args) {
        // Set system UI look and feel and antialiasing
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}