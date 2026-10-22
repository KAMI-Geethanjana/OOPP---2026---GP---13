package exception;

/**
 * Custom Exception for validation failures (e.g. invalid email, empty fields, bad formats).
 * Demonstrates:
 * 1. Exception Handling (Extends Exception - Checked Exception)
 * 2. Encapsulation validation enforcement in setters / models.
 * (Reference: ICT2122 Lesson 03 Part 04 & Lesson 05)
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
