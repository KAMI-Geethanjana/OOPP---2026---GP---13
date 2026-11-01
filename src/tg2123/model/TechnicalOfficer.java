package tg2123.model;

import common.model.User;
import tg2123.gui.TechnicalOfficerDashboard;

import javax.swing.JFrame;

/**
 * TechnicalOfficer - Represents technical officers in the FTMS system.
 * Demonstrates:
 * 1. Inheritance (extends abstract User)
 * 2. Polymorphism (overrides getRoleDisplayName and openDashboard)
 *
 * Authored under: Module TG2123 (B. G. N. M. Weerawardhana)
 */
public class TechnicalOfficer extends User {

    private int toId;
    private String employeeId;
    private int departmentId;
    private String departmentName;

    public TechnicalOfficer() {
        super();
        setRole("TECH_OFFICER");
    }

    public TechnicalOfficer(int userId, int toId, String employeeId, int departmentId,
                            String departmentName, String fullName, String email, String phoneNo) {
        super(userId, null, null, fullName, email, phoneNo, "TECH_OFFICER");
        this.toId = toId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public TechnicalOfficer(int userId, String username, String passwordHash, String fullName,
                            String email, String phoneNo, int toId, String employeeId, int departmentId) {
        super(userId, username, passwordHash, fullName, email, phoneNo, "TECH_OFFICER");
        this.toId = toId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }

    @Override
    public String getRoleDisplayName() {
        return "Technical Officer";
    }

    @Override
    public void openDashboard(JFrame parent) {
        if (parent != null) {
            parent.dispose();
        }
        TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard(this);
        dashboard.setVisible(true);
    }

    public int getToId() { return toId; }
    public void setToId(int toId) { this.toId = toId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
