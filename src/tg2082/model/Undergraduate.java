package tg2082.model;

import common.model.User;
import tg2082.gui.UndergraduateDashboard;

import javax.swing.JFrame;


public class Undergraduate extends User {

    private int studentId;
    private String regNo;
    private int batchYear;
    private String status; // 'ACTIVE', 'REPEAT', 'BATCH_MISSED'
    private int departmentId;
    private String departmentName;
    private String contactNo;

    public Undergraduate() {
        super();
        setRole("UNDERGRADUATE");
    }

    public Undergraduate(int userId, String username, String passwordHash, String fullName,
                         String email, String phoneNo, int studentId, String regNo,
                         int batchYear, String status, int departmentId) {
        super(userId, username, passwordHash, fullName, email, phoneNo, "UNDERGRADUATE");
        this.studentId = studentId;
        this.regNo = regNo;
        this.batchYear = batchYear;
        this.status = status;
        this.departmentId = departmentId;
    }

    @Override
    public String getRoleDisplayName() {
        return "Undergraduate Student";
    }

    @Override
    public void openDashboard(JFrame parent) {
        if (parent != null) {
            parent.dispose();
        }
        UndergraduateDashboard dashboard = new UndergraduateDashboard(this);
        dashboard.setVisible(true);
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
}
