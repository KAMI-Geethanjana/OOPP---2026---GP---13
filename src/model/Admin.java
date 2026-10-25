package model;

/**
 * Admin - Concrete class representing System Administrator in FTMS.
 * - Inheritance (extends abstract User)
 * - super() constructor invocation
 * - Constructor Chaining with this()
 * - Polymorphism & Method Overriding (@Override getRoleDisplayName & getDashboardRoute)
 * - Encapsulation (Private fields with accessors/mutators)
 */
public class Admin extends User {
    private int adminId;
    private String adminLevel; // e.g. "SUPER_ADMIN", "FACULTY_ADMIN"

    /**
     * Default constructor uses constructor chaining with this(...)
     */
    public Admin() {
        this(0, "admin", "", "System Administrator", "admin@fot.ruh.ac.lk", "", 0, "SUPER_ADMIN");
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
        super(userId, username, passwordHash, fullName, email, phoneNo, "ADMIN", true, 0);
        this.adminId = adminId;
        this.adminLevel = adminLevel;
    }

    /**
     * Method Overriding (Dynamic Polymorphism)
     */
    @Override
    public String getRoleDisplayName() {
        return "System Administrator (" + adminLevel + ")";
    }

    /**
     * Dynamic routing route for Admin dashboard
     */
    @Override
    public String getDashboardRoute() {
        return "gui.AdminDashboardUI";
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
