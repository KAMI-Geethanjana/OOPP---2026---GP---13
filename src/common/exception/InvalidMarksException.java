package common.exception;

/**
 * Custom Exception thrown when mark values are outside the valid 0-100 range.
 * Demonstrates OOP Exception Handling requirement.
 */
public class InvalidMarksException extends Exception {
    public InvalidMarksException(String message) {
        super(message);
    }
}
