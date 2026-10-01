package model;

public class Result {
    private int enrollmentId;
    private String studentIndex;
    private String studentName;
    private String courseCode;
    private double caMarks;
    private double finalMarks;
    private double totalMarks;
    private String grade;
    private String attemptType; // "PROPER", "REPEAT", "MEDICAL"

    public Result() {}

    public Result(int enrollmentId, String studentIndex, String studentName, String courseCode, double caMarks, double finalMarks, double totalMarks, String grade, String attemptType) {
        this.enrollmentId = enrollmentId;
        this.studentIndex = studentIndex;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.caMarks = caMarks;
        this.finalMarks = finalMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;
        this.attemptType = attemptType;
    }

    // Previous constructor support (Default attemptType to "PROPER")
    public Result(int enrollmentId, String studentIndex, String studentName, String courseCode, double caMarks, double finalMarks, double totalMarks, String grade) {
        this(enrollmentId, studentIndex, studentName, courseCode, caMarks, finalMarks, totalMarks, grade, "PROPER");
    }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getStudentIndex() { return studentIndex; }
    public void setStudentIndex(String studentIndex) { this.studentIndex = studentIndex; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public double getCaMarks() { return caMarks; }
    public void setCaMarks(double caMarks) { this.caMarks = caMarks; }

    public double getFinalMarks() { return finalMarks; }
    public void setFinalMarks(double finalMarks) { this.finalMarks = finalMarks; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getAttemptType() { return attemptType; }
    public void setAttemptType(String attemptType) { this.attemptType = attemptType; }
}