package common.exception;

/**
 * Custom Exception thrown when an account is locked due to 5 consecutive failed logins
 * (Requirement: FR-AUTH-05).
 */
public class AccountLockedException extends Exception {
    public AccountLockedException(String message) {
        super(message);
    }
}
