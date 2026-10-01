package model;

public class Mark {
    private int markId;
    private String studentId;
    private String courseCode;
    private double caMark;
    private double finalExamMark;
    private double totalMark;
    private boolean eligible;

    public Mark() {}

    public Mark(int markId, String studentId, String courseCode, double caMark, double finalExamMark, double totalMark, boolean eligible) {
        this.markId = markId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.caMark = caMark;
        this.finalExamMark = finalExamMark;
        this.totalMark = totalMark;
        this.eligible = eligible;
    }

    // Getters and Setters
    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public double getCaMark() { return caMark; }
    public void setCaMark(double caMark) { this.caMark = caMark; }

    public double getFinalExamMark() { return finalExamMark; }
    public void setFinalExamMark(double finalExamMark) { this.finalExamMark = finalExamMark; }

    public double getTotalMark() { return totalMark; }
    public void setTotalMark(double totalMark) { this.totalMark = totalMark; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
}