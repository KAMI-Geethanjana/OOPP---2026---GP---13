package common.model;

/**
 * Course entity representing academic courses.
 */
public class Course {
    private String courseCode;
    private String courseTitle;
    private int creditTheory;
    private int creditPractical;
    private int departmentId;
    private Integer lecturerId;
    private String lecturerName;

    public Course() {}

    public Course(String courseCode, String courseTitle, int creditTheory, int creditPractical, int departmentId, Integer lecturerId) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditTheory = creditTheory;
        this.creditPractical = creditPractical;
        this.departmentId = departmentId;
        this.lecturerId = lecturerId;
    }

    public int getTotalCredits() {
        return creditTheory + creditPractical;
    }

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

    public Integer getLecturerId() { return lecturerId; }
    public void setLecturerId(Integer lecturerId) { this.lecturerId = lecturerId; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    @Override
    public String toString() {
        return courseCode + " - " + courseTitle;
    }
}
