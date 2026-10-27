package tg2088.model;

import java.io.Serializable;

/**
 * QuizQuestion model for multiple choice questions.
 */
public class QuizQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private int questionId;
    private int quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char correctOption;
    private double points;

    {
        this.correctOption = 'A';
        this.points = 10.0;
    }
}
