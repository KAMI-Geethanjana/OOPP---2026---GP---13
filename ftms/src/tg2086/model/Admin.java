package tg2086.model;

import common.model.User;
import tg2086.gui.AdminDashboardUI;

import javax.swing.JFrame;

/**
 * Admin - Concrete model representing System Administrator in FTMS.
 * Demonstrates:
 * 1. Inheritance (extends abstract common.model.User)
 * 2. super() constructor invocation
 * 3. Constructor Chaining with this()
 * 4. Polymorphism & Method Overriding (@Override getRoleDisplayName & openDashboard)
 * 5. Encapsulation (Private fields with accessors/mutators)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class Admin extends User {

    private int adminId;
    private String adminLevel; // e.g. "SUPER_ADMIN", "FACULTY_ADMIN"

    /**
     * Default constructor uses constructor chaining with this(...)
     */
    public Admin() {
        this(1, "admin", "1234", "System Administrator", "admin@fot.ruh.ac.lk", "0771234567", 1, "SUPER_ADMIN");
    }

    /**
     * Parameterized constructor calling superclass constructor
     */
    public Admin(int userId, String username, String passwordHash, String fullName,
                 String email, String phoneNo, int adminId) {
        this(userId, username, passwordHash, fullName, email, phoneNo, adminId, "FACULTY_ADMIN");
    }

    /**
     * Overloaded constructor with admin level
     */
    public Admin(int userId, String username, String passwordHash, String fullName,
                 String email, String phoneNo, int adminId, String adminLevel) {
        super(userId, username, passwordHash, fullName, email, phoneNo, "ADMIN");
        this.adminId = adminId;
        this.adminLevel = (adminLevel != null) ? adminLevel : "SUPER_ADMIN";
    }

    /**
     * Method Overriding (Dynamic Polymorphism)
     */
    @Override
    public String getRoleDisplayName() {
        return "System Administrator";
    }

    /**
     * Polymorphic method to display role-specific dashboard.
     * Invoked automatically by common.auth.LoginFrame upon successful login.
     */
    @Override
    public void openDashboard(JFrame parent) {
        if (parent != null) {
            parent.dispose();
        }
        AdminDashboardUI dashboard = new AdminDashboardUI(this);
        dashboard.setVisible(true);
    }

    // ==========================================
    // Encapsulation (Accessors & Mutators)
    // ==========================================

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        if (adminId < 0) {
            throw new IllegalArgumentException("Admin ID cannot be negative.");
        }
        this.adminId = adminId;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel != null ? adminLevel.trim() : "FACULTY_ADMIN";
    }

    @Override
    public String toString() {
        return "Admin Profile [AdminId=" + adminId + ", Level=" + adminLevel + ", FullName=" + getFullName() + "]";
    }
}
