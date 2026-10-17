package model;

import java.sql.Time;

/**
 * Timetable model representing scheduled academic sessions.
 *
 * Authored under: Module TG2086 (Admin Module)
 */
public class Timetable {
    private int timetableId;
    private String courseCode;
    private String courseTitle;
    private int departmentId;
    private String departmentName;
    private String sessionType;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private String venue;

    public Timetable() {}

    public Timetable(int timetableId, String courseCode, int departmentId, String sessionType,
                     String dayOfWeek, Time startTime, Time endTime, String venue) {
        this.timetableId = timetableId;
        this.courseCode = courseCode;
        this.departmentId = departmentId;
        this.sessionType = sessionType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
    }

    public Timetable(int timetableId, String courseCode, String courseTitle, int departmentId,
                     String departmentName, String sessionType, String dayOfWeek,
                     Time startTime, Time endTime, String venue) {
        this.timetableId = timetableId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.sessionType = sessionType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
    }

    // Getters and Setters
    public int getTimetableId() { return timetableId; }
    public void setTimetableId(int timetableId) { this.timetableId = timetableId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
}
