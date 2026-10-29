package tg2088.exception;

/**
 * ============================================================================
 * Lesson 05: Introduction to Java Exceptions
 * ============================================================================
 * Reference: Lesson 05, Slide 48-50 ("Declaring Own exceptions")
 * - Extends java.lang.Exception to create a checked exception.
 * - Enforces the "Handle or Declare Rule" at compile time.
 * - Used to handle business logic errors specific to the Quiz module
 *   (e.g., validation failures, duplicate titles, invalid questions, DB errors).
 * ============================================================================
 */
public class QuizOperationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor (Lesson 02, Slide 34)
     */
    public QuizOperationException() {
        super("An error occurred during the Quiz operation.");
    }

    /**
     * Parameterized constructor with custom message (Lesson 05, Slide 50)
     *
     * @param message Detailed reason for the exception
     */
    public QuizOperationException(String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom message and root cause
     *
     * @param message Detailed error description
     * @param cause Underlying Throwable cause (Lesson 05, Slide 22)
     */
    public QuizOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
