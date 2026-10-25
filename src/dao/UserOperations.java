package dao;

import exception.AdminOperationException;
import java.util.List;
import java.util.Map;

/**
 * Interface contract for User Management Operations in FTMS.
 * - Interfaces as architectural contracts
 * - Interface Constants (public static final)
 * - Default Methods in Interfaces (Java 8+)
 * - Static Methods in Interfaces (Java 8+)
 * - Method Overloading within interface contract
 * - Exception declarations via throws
 */
public interface UserOperations {

    // Interface Constants (implicitly public static final)
    int MAX_FAILED_ATTEMPTS = 3;
    String DEFAULT_PASSWORD = "Password@123";

    /**
     * Creates a new user record along with role-specific profile.
     */
    boolean createUser(String username, String password, String fullName, String email,
                       String phoneNo, String role, String extraId, int departmentId, String batch)
            throws AdminOperationException;

    /**
     * Updates an existing user record.
     */
    boolean updateUser(int userId, String fullName, String email, String phoneNo,
                       String role, boolean isActive, String extraId, int departmentId, String batch)
            throws AdminOperationException;

    /**
     * Method Overloading: Delete by user ID
     */
    boolean deleteUser(int userId) throws AdminOperationException;

    /**
     * Method Overloading: Delete by username
     */
    boolean deleteUser(String username) throws AdminOperationException;

    /**
     * Retrieves all users from database.
     */
    List<Map<String, Object>> getAllUsers() throws AdminOperationException;

    /**
     * Method Overloading: Retrieves users filtered by role
     */
    List<Map<String, Object>> getAllUsers(String roleFilter) throws AdminOperationException;

    /**
     * Unlocks an account locked due to consecutive failed login attempts.
     */
    boolean unlockAccount(int userId) throws AdminOperationException;

    /**
     * Default method providing utility logging without forcing subclasses to implement.
     */
    default void logAudit(String action, String detail) {
        System.out.println("[AUDIT LOG] " + System.currentTimeMillis() + " - " + action + ": " + detail);
    }

    /**
     * Static utility method in interface.
     */
    static boolean isValidRole(String role) {
        if (role == null) return false;
        String r = role.trim().toUpperCase();
        return r.equals("ADMIN") || r.equals("LECTURER") || r.equals("TECH_OFFICER") || r.equals("UNDERGRADUATE");
    }
}
