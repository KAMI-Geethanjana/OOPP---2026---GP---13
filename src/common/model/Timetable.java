package common.model;

import java.sql.Time;

/**
 * Timetable entity for lecture/practical schedules.
 */
public class Timetable {
    private int timetableId;
    private int departmentId;
    private String courseCode;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private String venue;
    private String sessionType; // 'THEORY', 'PRACTICAL'

    public Timetable() {}

    public Timetable(int timetableId, int departmentId, String courseCode, String dayOfWeek, Time startTime, Time endTime, String venue, String sessionType) {
        this.timetableId = timetableId;
        this.departmentId = departmentId;
        this.courseCode = courseCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.sessionType = sessionType;
    }

    public int getTimetableId() { return timetableId; }
    public void setTimetableId(int timetableId) { this.timetableId = timetableId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
}
