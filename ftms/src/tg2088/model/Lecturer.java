package tg2088.model;

import common.model.User;
import tg2088.gui.LecturerDashboard;

import javax.swing.JFrame;


public class Lecturer extends User {

    private int lecturerId;
    private String employeeId;
    private int departmentId;
    private String departmentName;

    public Lecturer() {
        super();
        setRole("LECTURER");
    }

    public Lecturer(int userId, String username, String passwordHash, String fullName,
                    String email, String phoneNo, int lecturerId, String employeeId, int departmentId) {
        super(userId, username, passwordHash, fullName, email, phoneNo, "LECTURER");
        this.lecturerId = lecturerId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }

    @Override
    public String getRoleDisplayName() {
        return "Lecturer";
    }

    @Override
    public void openDashboard(JFrame parent) {
        if (parent != null) {
            parent.dispose();
        }
        LecturerDashboard dashboard = new LecturerDashboard(this);
        dashboard.setVisible(true);
    }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
