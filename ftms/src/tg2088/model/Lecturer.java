package tg2088.model;

import common.model.User;
import tg2088.gui.LecturerDashboard;

import javax.swing.JFrame;

/**
 * ============================================================================
 * Lessons 01, 02, 03 (Part 01, 02, 04): Inheritance, Polymorphism & Encapsulation
 * ============================================================================
 * Reference:
 * - Lesson 03 Part 01, Slide 6-9: Single Inheritance ('extends' User)
 * - Lesson 03 Part 01, Slide 32-41: Subclass constructors calling 'super(...)'
 * - Lesson 03 Part 01, Slide 26-28: Method Overriding with @Override annotation
 * - Lesson 03 Part 02, Slide 23-26: Dynamic Polymorphism (Dynamic Binding of openDashboard)
 * - Lesson 03 Part 04, Slide 11-20: Data Hiding & Setters validation
 * ============================================================================
 */
public class Lecturer extends User {

    // ========================================================================
    // Private Subclass Fields (Encapsulation - Lesson 03 Part 04, Slide 11)
    // ========================================================================
    private int lecturerId;
    private String employeeId;
    private int departmentId;
    private String departmentName;

    // ========================================================================
    // Instance Initializer Block (Lesson 02 Part 02, Slide 11)
    // Runs before constructor to ensure consistent initialization
    // ========================================================================
    {
        this.departmentName = "Faculty of Technology";
    }

    // ========================================================================
    // Default Constructor (no-arg) (Lesson 02, Slide 34)
    // Constructor Chaining within the same class using 'this(...)' (Lesson 02 Part 02, Slide 8-9)
    // ========================================================================
    public Lecturer() {
        this(0, "lecturer_temp", "", "New Lecturer", "", "", 0, "EMP000", 1);
    }

    // ========================================================================
    // Parameterized Constructor (Lesson 02, Slide 38)
    // super(...) must be the very first statement (Lesson 03 Part 01, Slide 35 & 41)
    // ========================================================================
    public Lecturer(int userId, String username, String passwordHash, String fullName,
                    String email, String phoneNo, int lecturerId, String employeeId, int departmentId) {
        // Explicitly calling superclass (User) constructor
        super(userId, username, passwordHash, fullName, email, phoneNo, "LECTURER");
        
        // 'this' refers to current class instance fields (Lesson 02 Part 02, Slide 5-7)
        this.lecturerId = lecturerId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }

    // ========================================================================
    // Method Overriding (Dynamic Polymorphism - Lesson 03 Part 01 Slide 26, Part 02 Slide 23-26)
    // ========================================================================
    @Override
    public String getRoleDisplayName() {
        return "Lecturer";
    }

    /**
     * Pure Dynamic Binding / Runtime Polymorphism:
     * When LoginFrame calls user.openDashboard(this), the JVM resolves and invokes
     * this specific Lecturer method at runtime! (Lesson 03 Part 02, Slide 23-26)
     */
    @Override
    public void openDashboard(JFrame parent) {
        // Lesson 03 Part 01, Slide 49: 'instanceof' operator check
        if (parent != null && parent instanceof JFrame) {
            parent.dispose();
        }
        LecturerDashboard dashboard = new LecturerDashboard(this);
        dashboard.setVisible(true);
    }

    // ========================================================================
    // Getters and Setters with Encapsulation & Validation (Lesson 03 Part 04, Slide 20)
    // ========================================================================
    public int getLecturerId() {
        return this.lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        if (lecturerId >= 0) {
            this.lecturerId = lecturerId;
        }
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId != null && !employeeId.trim().isEmpty()) {
            this.employeeId = employeeId.trim();
        }
    }

    public int getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(int departmentId) {
        if (departmentId > 0) {
            this.departmentId = departmentId;
        }
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        if (departmentName != null && !departmentName.trim().isEmpty()) {
            this.departmentName = departmentName.trim();
        }
    }

    // ========================================================================
    // Overriding toString() method (Lesson 02 Part 02, Slide 10)
    // ========================================================================
    @Override
    public String toString() {
        return "Lecturer [lecturerId=" + this.lecturerId +
                ", employeeId=" + this.employeeId +
                ", name=" + getFullName() +
                ", dept=" + this.departmentName + "]";
    }

    // ========================================================================
    // Methods specified in UML Class Diagram (ICT2132 Task 02)
    // ========================================================================
    public void addCourseMeterial() {
        System.out.println("Course material addition initiated by lecturer: " + getFullName());
    }

    public void updateCourseMaterial() {
        System.out.println("Course material update initiated by lecturer: " + getFullName());
    }

    public void uploadMarks() {
        System.out.println("Marks upload initiated by lecturer: " + getFullName());
    }

    public void viewStudentDetails() {
        System.out.println("Viewing student details for lecturer: " + getFullName());
    }

    public double viewEligibility() {
        // Minimum attendance percentage threshold (80.0%)
        return 80.0;
    }

    public void viewMarks() {
        System.out.println("Viewing marks table for lecturer: " + getFullName());
    }

    public void viewGrades() {
        System.out.println("Viewing calculated UGC grades for lecturer: " + getFullName());
    }

    public double viewGPA() {
        return 4.0;
    }

    public void viewAttendance() {
        System.out.println("Viewing student attendance for lecturer: " + getFullName());
    }

    public void viewMedicalRecords() {
        System.out.println("Viewing medical records for lecturer: " + getFullName());
    }

    public void viewNotices() {
        System.out.println("Viewing notices for lecturer: " + getFullName());
    }
}
