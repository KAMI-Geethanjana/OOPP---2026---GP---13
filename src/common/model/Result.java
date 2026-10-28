package common.model;

import common.exception.InvalidMarksException;
import common.util.GradeCalculator;

/**
 * Result entity representing grades, marks, and eligibility for an enrolled course.
 * Demonstrates:
 * 1. Encapsulation: Strict setter validation throwing custom InvalidMarksException.
 */
public class Result {
    private int resultId;
    private int enrollmentId;
    private int studentId;
    private String studentRegNo;
    private String studentName;
    private String courseCode;
    private String attemptType; // 'PROPER', 'REPEAT', 'BATCH_MISSED'
    private double caMark;
    private double finalExamMark;
    private double totalMark;
    private String eligibilityStatus; // 'ELIGIBLE', 'NOT_ELIGIBLE'
    private String grade;
    private double gradePoint;

    public Result() {
        this.attemptType = "PROPER";
        this.eligibilityStatus = "ELIGIBLE";
        this.grade = "-";
    }

    public Result(int resultId, int enrollmentId, int studentId, String studentRegNo, String studentName,
                  String courseCode, String attemptType, double caMark, double finalExamMark,
                  double totalMark, String eligibilityStatus, String grade, double gradePoint) {
        this.resultId = resultId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentRegNo = studentRegNo;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.attemptType = attemptType != null ? attemptType : "PROPER";
        this.caMark = caMark;
        this.finalExamMark = finalExamMark;
        this.totalMark = totalMark;
        this.eligibilityStatus = eligibilityStatus != null ? eligibilityStatus : "ELIGIBLE";
        this.grade = grade != null ? grade : "-";
        this.gradePoint = gradePoint;
    }

    // Encapsulation with validation
    public void setCaMark(double caMark) throws InvalidMarksException {
        GradeCalculator.validateMark(caMark, "CA");
        this.caMark = caMark;
    }

    public void setFinalExamMark(double finalExamMark) throws InvalidMarksException {
        GradeCalculator.validateMark(finalExamMark, "Final Exam");
        this.finalExamMark = finalExamMark;
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentRegNo() { return studentRegNo; }
    public void setStudentRegNo(String studentRegNo) { this.studentRegNo = studentRegNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getAttemptType() { return attemptType; }
    public void setAttemptType(String attemptType) { this.attemptType = attemptType; }

    public double getCaMark() { return caMark; }

    public double getFinalExamMark() { return finalExamMark; }

    public double getTotalMark() { return totalMark; }
    public void setTotalMark(double totalMark) { this.totalMark = totalMark; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }
}
