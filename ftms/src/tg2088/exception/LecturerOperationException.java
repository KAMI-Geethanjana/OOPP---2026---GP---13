package tg2088.exception;

/**
 * ============================================================================
 * Lesson 05: Introduction to Java Exceptions
 * ============================================================================
 * Reference: Lesson 05, Slide 48-50 ("Declaring Own exceptions")
 * - Extends java.lang.Exception to create a checked exception.
 * - Enforces the "Handle or Declare Rule" at compile time.
 * - Used to handle business logic errors specific to the Lecturer module
 *   (e.g., duplicate material uploads, invalid mark updates, missing records).
 * ============================================================================
 */
public class LecturerOperationException extends Exception {

    /**
     * Default constructor (Lesson 02, Slide 34)
     */
    public LecturerOperationException() {
        super("An error occurred during the Lecturer operation.");
    }

    /**
     * Parameterized constructor with custom error message (Lesson 05, Slide 50)
     *
     * @param message Detailed reason for the exception
     */
    public LecturerOperationException(String message) {
        // Calls the superclass (Exception) constructor using 'super' keyword (Lesson 03 Part 01, Slide 32)
        super(message);
    }

    /**
     * Parameterized constructor with message and root cause
     *
     * @param message Detailed error description
     * @param cause Underlying Throwable cause (Lesson 05, Slide 22)
     */
    public LecturerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
