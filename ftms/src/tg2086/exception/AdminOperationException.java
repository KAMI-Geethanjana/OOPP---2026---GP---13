package tg2086.exception;

/**
 * Custom checked exception for failures during administrative operations.
 * - Custom Checked Exception Hierarchy (extends Exception)
 * - Constructor Overloading for flexible error propagation
 */
public class AdminOperationException extends Exception {

    public AdminOperationException(String message) {
        super(message);
    }

    public AdminOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
