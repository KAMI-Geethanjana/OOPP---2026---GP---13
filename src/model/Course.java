package model;

/**
 * Course entity model representing academic modules.
 * Matches UML Class Diagram and ER schema.
 *
 * Authored under: Module TG2086 (Admin Module)
 */
public class Course {
    private String courseCode;
    private String courseTitle;
    private int creditTheory;
    private int creditPractical;
    private int departmentId;
    private String departmentName;
    private Integer lecturerId;
    private String lecturerName;

    public Course() {}

    public Course(String courseCode, String courseTitle, int creditTheory, int creditPractical,
                  int departmentId, String departmentName, Integer lecturerId, String lecturerName) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditTheory = creditTheory;
        this.creditPractical = creditPractical;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.lecturerId = lecturerId;
        this.lecturerName = lecturerName;
    }

    public int getTotalCredits() {
        return creditTheory + creditPractical;
    }

    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getCreditTheory() { return creditTheory; }
    public void setCreditTheory(int creditTheory) { this.creditTheory = creditTheory; }

    public int getCreditPractical() { return creditPractical; }
    public void setCreditPractical(int creditPractical) { this.creditPractical = creditPractical; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getLecturerId() { return lecturerId; }
    public void setLecturerId(Integer lecturerId) { this.lecturerId = lecturerId; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    @Override
    public String toString() {
        return courseCode + " - " + courseTitle;
    }
}
