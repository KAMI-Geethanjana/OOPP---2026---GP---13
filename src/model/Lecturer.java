package model;

public class Lecturer {
    private int lecturerId;
    private int userId;
    private int departmentId;

    public Lecturer() {}

    public Lecturer(int lecturerId, int userId, int departmentId) {
        this.lecturerId = lecturerId;
        this.userId = userId;
        this.departmentId = departmentId;
    }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
}