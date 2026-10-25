package exception;

/**
 * Custom Exception for validation failures (e.g. invalid email, empty fields, bad formats).
 * - Exception Handling (Extends Exception - Checked Exception)
 * - Encapsulation validation enforcement in setters / models.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
