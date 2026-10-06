public class AttendanceSession {

    private int sessionId;
    private String courseCode;
    private String sessionDate;
    private String sessionType;
    private int sessionNumber;

    public AttendanceSession() {
    }

    public AttendanceSession(String courseCode, String sessionDate,
                             String sessionType, int sessionNumber) {
        this.courseCode = courseCode;
        this.sessionDate = sessionDate;
        this.sessionType = sessionType;
        this.sessionNumber = sessionNumber;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }
}