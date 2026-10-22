package exception;

/**
 * Custom Checked Exception representing failures in Admin operations.
 * Demonstrates:
 * 1. Java Exception Handling (Extends java.lang.Exception - Checked Exception)
 * 2. Constructor overloading and exception chaining (super(message, cause))
 * (Reference: ICT2122 Lesson 05 - Introduction to Java Exceptions)
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
