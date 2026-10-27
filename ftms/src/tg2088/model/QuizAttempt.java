package tg2088.model;

import java.io.Serializable;

/**
 * Model representing a student attempt on a quiz.
 */
public class QuizAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    private int attemptId;
    private int quizId;
    private String studentRegNo;
    private String studentName;
    private double score;
    private double maxScore;
    private double percentage;
    private String status;
    private String attemptedAt;

    {
        this.score = 0.0;
        this.maxScore = 100.0;
        this.percentage = 0.0;
        this.status = "FAIL";
    }
}
