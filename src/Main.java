import gui.AdminDashboardUI;
import gui.LoginUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application launcher for the FTMS Admin Module.
 * Launches the Login Portal, which directs authenticated users to AdminDashboardUI.
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
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
