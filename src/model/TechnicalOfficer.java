
package model;

public class TechnicalOfficer {

    private int toId;
    private int userId;
    private String employeeId;
    private int departmentId;
    private String departmentName;
    private String fullName;
    private String email;
    private String phoneNo;

    public TechnicalOfficer() {
    }

    public TechnicalOfficer(
            int toId, int userId, String employeeId,
            int departmentId, String departmentName,
            String fullName, String email, String phoneNo) {

        this.toId = toId;
        this.userId = userId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
