package tg2088.model;

import java.io.Serializable;

/**
 * ============================================================================
 * Lessons 01, 02, 03 (Part 04): Classes, Objects, this & Encapsulation
 * ============================================================================
 * Model representing a student's submission / attempt on a specific Quiz.
 * ============================================================================
 */
public class QuizAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========================================================================
    // Private Fields (Lesson 03 Part 04)
    // ========================================================================
    private int attemptId;
    private int quizId;
    private String studentRegNo;
    private String studentName;
    private double score;
    private double maxScore;
    private double percentage;
    private String status; // "PASS", "FAIL"
    private String attemptedAt;

    // ========================================================================
    // Instance Initializer Block (Lesson 02 Part 02)
    // ========================================================================
    {
        this.score = 0.0;
        this.maxScore = 100.0;
        this.percentage = 0.0;
        this.status = "FAIL";
    }

    // ========================================================================
    // Default Constructor (Lesson 02, Slide 34)
    // ========================================================================
    public QuizAttempt() {
        this(0, 0, "", "", 0.0, 100.0, 0.0, "FAIL", "");
    }

    // ========================================================================
    // Parameterized Constructor (Lesson 02, Slide 38)
    // ========================================================================
    public QuizAttempt(int attemptId, int quizId, String studentRegNo, String studentName,
                       double score, double maxScore, double percentage, String status, String attemptedAt) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.studentRegNo = studentRegNo;
        this.studentName = studentName;
        this.score = score;
        this.maxScore = maxScore > 0 ? maxScore : 100.0;
        this.percentage = percentage;
        this.status = status;
        this.attemptedAt = attemptedAt;
    }

    // ========================================================================
    // Getters and Setters
    // ========================================================================
    public int getAttemptId() {
        return this.attemptId;
    }

    public void setAttemptId(int attemptId) {
        if (attemptId >= 0) {
            this.attemptId = attemptId;
        }
    }

    public int getQuizId() {
        return this.quizId;
    }

    public void setQuizId(int quizId) {
        if (quizId >= 0) {
            this.quizId = quizId;
        }
    }

    public String getStudentRegNo() {
        return this.studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = (studentRegNo != null) ? studentRegNo.trim() : "";
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = (studentName != null) ? studentName.trim() : "";
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        if (score >= 0) {
            this.score = score;
            recalculate();
        }
    }

    public double getMaxScore() {
        return this.maxScore;
    }

    public void setMaxScore(double maxScore) {
        if (maxScore > 0) {
            this.maxScore = maxScore;
            recalculate();
        }
    }

    public double getPercentage() {
        return this.percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttemptedAt() {
        return this.attemptedAt;
    }

    public void setAttemptedAt(String attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    private void recalculate() {
        if (this.maxScore > 0) {
            this.percentage = (this.score / this.maxScore) * 100.0;
            this.status = (this.percentage >= 50.0) ? "PASS" : "FAIL";
        }
    }

    @Override
    public String toString() {
        return "QuizAttempt{" +
                "attemptId=" + attemptId +
                ", studentRegNo='" + studentRegNo + '\'' +
                ", score=" + score + "/" + maxScore +
                ", percentage=" + percentage + "%" +
                ", status='" + status + '\'' +
                '}';
    }
}
