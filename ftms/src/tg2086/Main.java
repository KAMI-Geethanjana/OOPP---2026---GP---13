package tg2086;

import tg2086.gui.AdminDashboardUI;
import tg2086.model.Admin;

import javax.swing.*;

/**
 * Main application launcher for Module TG2086 (Administrator & Notices Module).
 * Allows standalone execution and testing of the Administrator Dashboard.
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Admin defaultAdmin = new Admin(1, "admin", "1234", "K.A.M.I. Geethanjana", "admin@fot.ruh.ac.lk", "0771234567", 1, "SUPER_ADMIN");
            AdminDashboardUI dashboard = new AdminDashboardUI(defaultAdmin);
            dashboard.setVisible(true);
        });
    }
}
