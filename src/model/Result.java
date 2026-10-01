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

    public Result(int enrollmentId, String studentIndex, String studentName, String courseCode, double caMarks, double finalMarks, double totalMarks, String grade) {
        this.enrollmentId = enrollmentId;
        this.studentIndex = studentIndex;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.caMarks = caMarks;
        this.finalMarks = finalMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;
    }

    public int getEnrollmentId() { return enrollmentId; }
    public String getStudentIndex() { return studentIndex; }
    public String getStudentName() { return studentName; }
    public String getCourseCode() { return courseCode; }
    public double getCaMarks() { return caMarks; }
    public double getFinalMarks() { return finalMarks; }
    public double getTotalMarks() { return totalMarks; }
    public String getGrade() { return grade; }
}