package tg2088.model;

import java.io.Serializable;

/**
 * ============================================================================
 * Lessons 01, 02, 03 (Part 04): Classes, Objects, this & Encapsulation
 * ============================================================================
 * Reference:
 * - Lesson 02, Slide 12-14: Class anatomy (Fields, Initializers, Constructors, Methods)
 * - Lesson 02 Part 02, Slide 5-7: Usage of 'this' to distinguish instance variables
 * - Lesson 02 Part 02, Slide 11: Instance Initializer block
 * - Lesson 03 Part 04, Slide 9-13: Encapsulation (Private fields + Public getters/setters)
 * - Lesson 03 Part 04, Slide 20: Control over data via mutator validation
 * ============================================================================
 */
public class Quiz implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========================================================================
    // Private Fields (Encapsulation / Information Hiding - Lesson 03 Part 04)
    // ========================================================================
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

    // ========================================================================
    // Instance Initializer Block (Lesson 02 Part 02, Slide 11-12)
    // ========================================================================
    {
        this.durationMinutes = 30;
        this.totalMarks = 100.0;
        this.isActive = true;
        this.questionCount = 0;
    }

    // ========================================================================
    // Default Constructor (no-arg) (Lesson 02, Slide 34)
    // Demonstrates Constructor Chaining using 'this' (Lesson 02 Part 02, Slide 8-9)
    // ========================================================================
    public Quiz() {
        this(0, "", 0, "Untitled Quiz", "", 30, 100.0, true, "");
    }

    // ========================================================================
    // Parameterized Constructor (Lesson 02, Slide 38)
    // ========================================================================
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

    // ========================================================================
    // Accessors and Mutators (Getters & Setters) with Validation
    // ========================================================================
    public int getQuizId() {
        return this.quizId;
    }

    public void setQuizId(int quizId) {
        if (quizId >= 0) {
            this.quizId = quizId;
        }
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode != null) {
            this.courseCode = courseCode.trim().toUpperCase();
        }
    }

    public int getLecturerId() {
        return this.lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        if (lecturerId >= 0) {
            this.lecturerId = lecturerId;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public int getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes > 0) {
            this.durationMinutes = durationMinutes;
        }
    }

    public double getTotalMarks() {
        return this.totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        if (totalMarks >= 0) {
            this.totalMarks = totalMarks;
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getQuestionCount() {
        return this.questionCount;
    }

    public void setQuestionCount(int questionCount) {
        if (questionCount >= 0) {
            this.questionCount = questionCount;
        }
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "quizId=" + quizId +
                ", courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + durationMinutes + " mins" +
                ", totalMarks=" + totalMarks +
                ", isActive=" + isActive +
                '}';
    }
}
