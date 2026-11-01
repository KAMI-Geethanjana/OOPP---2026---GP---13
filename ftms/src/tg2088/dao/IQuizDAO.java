package tg2088.dao;

import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.util.List;

/**
 * ============================================================================
 * Lesson 04: Interfaces
 * ============================================================================
 * Reference: Lesson 04, Slide 5-7, 21-25, 32-33
 * - Serves as the contractual specification for Quiz Data Access Object.
 * - Variables are implicitly public static final (Interface Constants, Slide 23).
 * - Methods are implicitly public abstract (Slide 21).
 * - Contains Java 8+ 'default' method (Slide 32) and 'static' method (Slide 33).
 * - Demonstrates loose coupling and full abstraction (Slide 8, 35).
 * ============================================================================
 */
public interface IQuizDAO {

    // ========================================================================
    // Interface Constants (public static final by default - Lesson 04, Slide 23)
    // ========================================================================
    int DEFAULT_QUIZ_DURATION = 30;          // 30 minutes default duration
    double PASSING_PERCENTAGE = 50.0;        // 50% pass mark threshold
    double DEFAULT_QUESTION_POINTS = 10.0;   // 10 points per question

    // ========================================================================
    // Abstract Method Declarations (Lesson 04, Slide 21)
    // ========================================================================

    /**
     * Creates a new quiz entry.
     * Returns the generated quiz_id.
     */
    int createQuiz(Quiz quiz) throws QuizOperationException;

    /**
     * Updates an existing quiz's header details.
     */
    boolean updateQuiz(Quiz quiz) throws QuizOperationException;

    /**
     * Deletes a quiz and all associated questions / attempts (CASCADE).
     */
    boolean deleteQuiz(int quizId) throws QuizOperationException;

    /**
     * Retrieves a single quiz by its primary key ID.
     */
    Quiz getQuizById(int quizId);

    /**
     * Retrieves all quizzes associated with a particular course.
     */
    List<Quiz> getQuizzesByCourse(String courseCode);

    /**
     * Overloaded method: Retrieves quizzes filtered by active status.
     * Demonstrates Static Polymorphism / Method Overloading (Lesson 03 Part 02, Slide 13-15).
     */
    List<Quiz> getQuizzesByCourse(String courseCode, boolean activeOnly);

    /**
     * Retrieves all quizzes created by a specific lecturer.
     */
    List<Quiz> getQuizzesByLecturer(int lecturerId);

    /**
     * Toggles the active/inactive state of a quiz.
     */
    boolean toggleQuizStatus(int quizId, boolean isActive) throws QuizOperationException;

    /**
     * Adds a new multiple-choice question to a quiz.
     */
    int addQuestion(QuizQuestion question) throws QuizOperationException;

    /**
     * Updates an existing question.
     */
    boolean updateQuestion(QuizQuestion question) throws QuizOperationException;

    /**
     * Deletes a specific question by ID.
     */
    boolean deleteQuestion(int questionId) throws QuizOperationException;

    /**
     * Retrieves all questions belonging to a quiz.
     */
    List<QuizQuestion> getQuestionsByQuizId(int quizId);

    /**
     * Records a student attempt / quiz score.
     */
    boolean recordAttempt(QuizAttempt attempt) throws QuizOperationException;

    /**
     * Retrieves all recorded attempts / submissions for a quiz.
     */
    List<QuizAttempt> getAttemptsByQuizId(int quizId);

    // ========================================================================
    // Java 8 Default Method (Lesson 04, Slide 32)
    // ========================================================================
    default void printQuizDAOStatus() {
        System.out.println("[IQuizDAO Contract] Quiz DAO operational and ready for transactions.");
    }

    // ========================================================================
    // Java 8 Static Method (Lesson 04, Slide 33)
    // ========================================================================
    static boolean isPassingScore(double score, double maxScore) {
        if (maxScore <= 0) return false;
        return ((score / maxScore) * 100.0) >= PASSING_PERCENTAGE;
    }
}
