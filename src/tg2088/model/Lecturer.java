package tg2088.model;

import common.model.User;
import tg2088.gui.LecturerDashboard;

import javax.swing.JFrame;


// =========================================================================
// OOP CONCEPT: INHERITANCE (Subclassing / Code Reusability)
// 'Lecturer' is a subclass that extends the abstract superclass 'User'.
// It inherits common user fields (userId, username, passwordHash, email, etc.)
// and adds lecturer-specific attributes (lecturerId, employeeId, departmentId).
// =========================================================================
public class Lecturer extends User {

    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Data Hiding)
    // Attributes are declared 'private' to prevent direct unauthorized access.
    // =========================================================================
    private int lecturerId;
    private String employeeId;
    private int departmentId;
    private String departmentName;


    // Instance Initializer Block: Runs before constructor
    {
        this.departmentName = "Faculty of Technology";
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #1)
    // Default constructor chaining to parameterized constructor via 'this(...)'
    // =========================================================================
    public Lecturer() {
        this(0, "lecturer_temp", "", "New Lecturer", "", "", 0, "EMP000", 1);
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #2)
    // Parameterized constructor invoking superclass constructor via 'super(...)'
    // =========================================================================
    public Lecturer(int userId, String username, String passwordHash, String fullName,
                    String email, String phoneNo, int lecturerId, String employeeId, int departmentId) {
        // Explicitly calling superclass (User) constructor (Inheritance)
        super(userId, username, passwordHash, fullName, email, phoneNo, "LECTURER");
        
        // 'this' refers to current class instance fields
        this.lecturerId = lecturerId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding / Dynamic Binding #1)
    // Overriding the abstract method defined in superclass 'User'.
    // Provides role-specific display name at runtime.
    // =========================================================================
    @Override
    public String getRoleDisplayName() {
        return "Lecturer";
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding / Dynamic Binding #2)
    // Overriding abstract method 'openDashboard' from superclass 'User'.
    // At runtime, user.openDashboard(parent) will execute this specific
    // LecturerDashboard creation when the logged-in user is a Lecturer.
    // =========================================================================
    @Override
    public void openDashboard(JFrame parent) {
        if (parent != null && parent instanceof JFrame) {
            parent.dispose();
        }
        LecturerDashboard dashboard = new LecturerDashboard(this);
        dashboard.setVisible(true);
    }


    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Getters and Setters with Validation)
    // Mutators validate input values before updating the private attributes.
    // =========================================================================

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


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding #3)
    // Overriding java.lang.Object.toString()
    // =========================================================================
    @Override
    public String toString() {
        return "Lecturer [lecturerId=" + this.lecturerId +
                ", employeeId=" + this.employeeId +
                ", name=" + getFullName() +
                ", dept=" + this.departmentName + "]";
    }


    // Methods specified in UML Class Diagram (ICT2132 Task 02)

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
