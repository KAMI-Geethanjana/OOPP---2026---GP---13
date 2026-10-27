package tg2088.model;

import java.io.Serializable;

/**
 * Quiz entity model representing an online assessment.
 */
public class Quiz implements Serializable {

    private static final long serialVersionUID = 1L;

    private int quizId;
    private String courseCode;
    private int lecturerId;
    private String title;
    private String description;
    private int durationMinutes;
    private double totalMarks;
    private boolean isActive;
    private String createdAt;
    private int questionCount;

    {
        this.durationMinutes = 30;
        this.totalMarks = 100.0;
        this.isActive = true;
        this.questionCount = 0;
    }

    public Quiz() {
        this(0, "", 0, "Untitled Quiz", "", 30, 100.0, true, "");
    }

    public Quiz(int quizId, String courseCode, int lecturerId, String title, 
                String description, int durationMinutes, double totalMarks, 
                boolean isActive, String createdAt) {
        this.quizId = quizId;
        this.courseCode = courseCode;
        this.lecturerId = lecturerId;
        this.title = title;
        this.description = description;
        if (durationMinutes > 0) {
            this.durationMinutes = durationMinutes;
        }
        if (totalMarks >= 0) {
            this.totalMarks = totalMarks;
        }
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
}
