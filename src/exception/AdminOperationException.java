package exception;

/**
 * Custom Checked Exception representing failures in Admin operations.
 * - Java Exception Handling (Extends java.lang.Exception - Checked Exception)
 * - Constructor overloading and exception chaining (super(message, cause))
 */
public class AdminOperationException extends Exception {

    public AdminOperationException() {
        super("An error occurred during administrative operation.");
    }

    public AdminOperationException(String message) {
        super(message);
    }

    public AdminOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
