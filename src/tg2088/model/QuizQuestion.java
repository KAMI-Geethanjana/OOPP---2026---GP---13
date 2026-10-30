package tg2088.model;

import java.io.Serializable;

/**
 * ============================================================================
 * Lessons 01, 02, 03 (Part 04): Classes, Objects, this & Encapsulation
 * ============================================================================
 * Reference:
 * - Lesson 02, Slide 12-14: Class anatomy
 * - Lesson 02 Part 02, Slide 5-7: Usage of 'this' keyword
 * - Lesson 02 Part 02, Slide 11: Instance Initializer block
 * - Lesson 03 Part 04, Slide 9-13: Encapsulation (Private fields + Public getters/setters)
 * - Lesson 03 Part 04, Slide 20: Mutator input validation
 * ============================================================================
 */
public class QuizQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========================================================================
    // Private Fields (Encapsulation - Lesson 03 Part 04)
    // ========================================================================
    private int questionId;
    private int quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char correctOption; // 'A', 'B', 'C', or 'D'
    private double points;

    // ========================================================================
    // Instance Initializer Block (Lesson 02 Part 02, Slide 11-12)
    // ========================================================================
    {
        this.correctOption = 'A';
        this.points = 10.0;
    }

    // ========================================================================
    // Default Constructor (Lesson 02, Slide 34)
    // Demonstrates Constructor Chaining using 'this' (Lesson 02 Part 02, Slide 8-9)
    // ========================================================================
    public QuizQuestion() {
        this(0, 0, "", "", "", "", "", 'A', 10.0);
    }

    // ========================================================================
    // Parameterized Constructor (Lesson 02, Slide 38)
    // ========================================================================
    public QuizQuestion(int questionId, int quizId, String questionText,
                        String optionA, String optionB, String optionC, String optionD,
                        char correctOption, double points) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        setCorrectOption(correctOption);
        if (points >= 0) {
            this.points = points;
        }
    }

    // ========================================================================
    // Getters and Setters with Validation
    // ========================================================================
    public int getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(int questionId) {
        if (questionId >= 0) {
            this.questionId = questionId;
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

    public String getQuestionText() {
        return this.questionText;
    }

    public void setQuestionText(String questionText) {
        if (questionText != null) {
            this.questionText = questionText.trim();
        }
    }

    public String getOptionA() {
        return this.optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = (optionA != null) ? optionA.trim() : "";
    }

    public String getOptionB() {
        return this.optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = (optionB != null) ? optionB.trim() : "";
    }

    public String getOptionC() {
        return this.optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = (optionC != null) ? optionC.trim() : "";
    }

    public String getOptionD() {
        return this.optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = (optionD != null) ? optionD.trim() : "";
    }

    public char getCorrectOption() {
        return this.correctOption;
    }

    public void setCorrectOption(char correctOption) {
        char upper = Character.toUpperCase(correctOption);
        if (upper == 'A' || upper == 'B' || upper == 'C' || upper == 'D') {
            this.correctOption = upper;
        } else {
            this.correctOption = 'A'; // fallback default
        }
    }

    public double getPoints() {
        return this.points;
    }

    public void setPoints(double points) {
        if (points >= 0) {
            this.points = points;
        }
    }

    /**
     * Helper to return the text of the correct option.
     */
    public String getCorrectAnswerText() {
        switch (correctOption) {
            case 'A': return optionA;
            case 'B': return optionB;
            case 'C': return optionC;
            case 'D': return optionD;
            default: return "";
        }
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "questionId=" + questionId +
                ", quizId=" + quizId +
                ", questionText='" + questionText + '\'' +
                ", correctOption=" + correctOption +
                ", points=" + points +
                '}';
    }
}
