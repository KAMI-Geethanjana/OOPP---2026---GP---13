package tg2088.model;

import common.model.User;
import tg2088.gui.LecturerDashboard;

import javax.swing.JFrame;


public class Lecturer extends User {

    //private variables(attributes)-This is a Encapsulation method
    private int lecturerId;
    private String employeeId;
    private int departmentId;
    private String departmentName;


    // Instance Initializer Block
    //Instance Initializer Block runs before the constructor.

    {
        this.departmentName = "Faculty of Technology";
    }


    // Default Constructor
    // Constructor Chaining within the same class using 'this(...)'

    public Lecturer() {

        this(0, "lecturer_temp", "", "New Lecturer", "", "", 0, "EMP000", 1);
    }


    // Parameterized Constructor
    // super(...) must be the very first statement

    public Lecturer(int userId, String username, String passwordHash, String fullName,
                    String email, String phoneNo, int lecturerId, String employeeId, int departmentId) {
        // Explicitly calling superclass (User) constructor
        super(userId, username, passwordHash, fullName, email, phoneNo, "LECTURER");
        
        // 'this' refers to current class instance fields
        // assign  parameter instance variable
        this.lecturerId = lecturerId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }


    // Method Overriding Dynamic Polymorphism
    //Method Overriding means redefining a parent class method in a child class

    @Override
    public String getRoleDisplayName() {

        return "Lecturer";
    }


    @Override
    //It receives the parent window as a parameter.
    //open the lecture dashboard
    public void openDashboard(JFrame parent) {

        //Checking whether an object is an instance of the relevant class.
        if (parent != null && parent instanceof JFrame) {
            parent.dispose();
        }
        LecturerDashboard dashboard = new LecturerDashboard(this);
        dashboard.setVisible(true);
    }


    // Getters and Setters with Encapsulation & Validation
    //Get the lecture id
    //A getter method is used to read a private variable.

    public int getLecturerId() {

        return this.lecturerId;
    }

    //A setter method is used to update a private variable with validation.
    public void setLecturerId(int lecturerId) {
        if (lecturerId >= 0) {
            this.lecturerId = lecturerId;
        }
    }

    //It returns the employee ID.
    public String getEmployeeId() {

        return this.employeeId;
    }


    public void setEmployeeId(String employeeId) {
        //It checks whether the employee ID is not null or empty before updating.
        if (employeeId != null && !employeeId.trim().isEmpty()) {
            this.employeeId = employeeId.trim();
        }
    }

    //het the department id
    public int getDepartmentId() {

        return this.departmentId;
    }
    //It updates the department ID only if it is greater than zero.
    public void setDepartmentId(int departmentId) {
        if (departmentId > 0) {
            this.departmentId = departmentId;
        }
    }

    //Get the department name
    public String getDepartmentName() {

        return this.departmentName;
    }
    //It updates the department name after validation.
    public void setDepartmentName(String departmentName) {
        if (departmentName != null && !departmentName.trim().isEmpty()) {
            this.departmentName = departmentName.trim();
        }
    }


    // Overriding toString() method (Lesson 02 Part 02, Slide 10)

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
    //This method is used to upload student marks.
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
