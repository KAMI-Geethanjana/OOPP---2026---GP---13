package common.model;

import java.sql.Date;

/**
 * Attendance entity representing a student attendance record for a session.
 */
public class Attendance {
    private int attendanceId;
    private int studentId;
    private String studentRegNo;
    private String studentName;
    private String courseCode;
    private Date sessionDate;
    private String sessionType; // 'THEORY', 'PRACTICAL'
    private String status;      // 'PRESENT', 'ABSENT'
    private Integer toId;

    public Attendance() {}

    public Attendance(int attendanceId, int studentId, String courseCode, Date sessionDate, String sessionType, String status, Integer toId) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.sessionDate = sessionDate;
        this.sessionType = sessionType;
        this.status = status;
        this.toId = toId;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentRegNo() { return studentRegNo; }
    public void setStudentRegNo(String studentRegNo) { this.studentRegNo = studentRegNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getToId() { return toId; }
    public void setToId(Integer toId) { this.toId = toId; }
}
