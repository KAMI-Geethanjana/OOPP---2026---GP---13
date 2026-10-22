package model;

/**
 * Admin - Concrete class representing System Administrator in FTMS.
 * Demonstrates:
 * 1. Inheritance (extends abstract User - Lesson 03 Part 01)
 * 2. super() constructor invocation (Lesson 03 Part 01 Slide 35)
 * 3. Constructor Chaining with this() (Lesson 02 Part 02 Slide 9)
 * 4. Polymorphism & Method Overriding (@Override getRoleDisplayName & getDashboardRoute - Lesson 03 Part 02)
 * 5. Encapsulation (Private fields with accessors/mutators - Lesson 03 Part 04)
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
     * Parameterized constructor calling superclass constructor (Lesson 03 Part 01 Slide 35)
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
     * Method Overriding (Dynamic Polymorphism - Lesson 03 Part 02)
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
