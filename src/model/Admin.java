package model;

/**
 * Admin - Concrete class representing System Administrator in FTMS.
 * Demonstrates:
 * 1. Inheritance (extends abstract User)
 * 2. Polymorphism (overrides getRoleDisplayName)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class Admin extends User {
    private int adminId;

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int userId, String username, String passwordHash, String fullName,
                 String email, String phoneNo, int adminId) {
        super(userId, username, passwordHash, fullName, email, phoneNo, "ADMIN", true, 0);
        this.adminId = adminId;
    }

    @Override
    public String getRoleDisplayName() {
        return "System Administrator";
    }

    // Methods as per UML Class Diagram specification
    public void addUser() {
        // Handled via AdminUserDAO and UI Form
    }

    public void updateUser() {
        // Handled via AdminUserDAO and UI Form
    }

    public void deleteUser() {
        // Handled via AdminUserDAO and UI Form
    }

    public void addCourse() {
        // Handled via AdminCourseDAO and UI Form
    }

    public void updateCourse() {
        // Handled via AdminCourseDAO and UI Form
    }

    public void deleteCourse() {
        // Handled via AdminCourseDAO and UI Form
    }

    public void createNotice() {
        // Handled via AdminNoticeDAO and UI Form
    }

    public void createTimetable() {
        // Handled via AdminTimetableDAO and UI Form
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
}
