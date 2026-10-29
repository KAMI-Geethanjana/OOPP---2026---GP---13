package common.model;

import java.sql.Date;

/**
 * Medical entity representing student medical certificate submissions.
 */
public class Medical {
    private int medicalId;
    private int studentId;
    private String studentRegNo;
    private String studentName;
    private String courseCode;
    private Date medicalDate;
    private String reason;
    private String documentPath;
    private String status; // 'PENDING', 'APPROVED', 'REJECTED'
    private Integer reviewedByToId;

    public Medical() {}

    public Medical(int medicalId, int studentId, String courseCode, Date medicalDate, String reason, String documentPath, String status, Integer reviewedByToId) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.medicalDate = medicalDate;
        this.reason = reason;
        this.documentPath = documentPath;
        this.status = status;
        this.reviewedByToId = reviewedByToId;
    }

    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentRegNo() { return studentRegNo; }
    public void setStudentRegNo(String studentRegNo) { this.studentRegNo = studentRegNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Date getMedicalDate() { return medicalDate; }
    public void setMedicalDate(Date medicalDate) { this.medicalDate = medicalDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getReviewedByToId() { return reviewedByToId; }
    public void setReviewedByToId(Integer reviewedByToId) { this.reviewedByToId = reviewedByToId; }
}
