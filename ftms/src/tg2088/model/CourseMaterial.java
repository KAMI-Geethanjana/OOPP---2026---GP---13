package tg2088.model;

import java.sql.Date;


public class CourseMaterial {
    private int materialId;
    private String courseCode;
    private int lecturerId;
    private String title;
    private String filePath;
    private Date uploadedDate;

    public CourseMaterial() {}

    public CourseMaterial(int materialId, String courseCode, int lecturerId, String title, String filePath, Date uploadedDate) {
        this.materialId = materialId;
        this.courseCode = courseCode;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        this.uploadedDate = uploadedDate;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Date getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(Date uploadedDate) { this.uploadedDate = uploadedDate; }

    // ========================================================
    // Methods specified in UML Class Diagram (ICT2132 Task 02)
    // ========================================================
    public void upload() {
        // Upload logic handled by LecturerDAO.addCourseMaterial(this)
    }

    public void update() {
        // Update logic handled by LecturerDAO
    }

    public void delete() {
        // Delete logic handled by LecturerDAO.deleteCourseMaterial(this.materialId)
    }
}
