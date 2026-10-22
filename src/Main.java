import gui.LoginUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application launcher for the Faculty Technical Management System (FTMS).
 * Launches the Login Portal.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new LoginUI().setVisible(true);
        });
    }
}
