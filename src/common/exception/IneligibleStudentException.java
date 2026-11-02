package common.exception;

/**
 * Custom Exception thrown when an action cannot be performed because
 * a student does not meet the 80% attendance or CA >= 40% criteria.
 */
public class IneligibleStudentException extends Exception {
    public IneligibleStudentException(String message) {
        super(message);
    }
}
