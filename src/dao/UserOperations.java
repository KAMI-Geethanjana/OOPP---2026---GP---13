package dao;

import exception.AdminOperationException;
import java.util.List;
import java.util.Map;

/**
 * Interface contract for User Management Operations in FTMS.
 * Demonstrates:
 * 1. Interfaces as architectural contracts (Lesson 04 Slide 5-7)
 * 2. Interface Constants (public static final - Lesson 04 Slide 23)
 * 3. Default Methods in Interfaces (Java 8+ - Lesson 04 Slide 32)
 * 4. Static Methods in Interfaces (Java 8+ - Lesson 04 Slide 33)
 * 5. Method Overloading within interface contract (Lesson 03 Part 02)
 * 6. Exception declarations via throws (Lesson 05 Slide 40-41)
 */
public interface UserOperations {

    // Interface Constants (implicitly public static final - Lesson 04 Slide 23)
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
     * Method Overloading: Delete by user ID (Lesson 03 Part 02)
     */
    boolean deleteUser(int userId) throws AdminOperationException;

    /**
     * Method Overloading: Delete by username (Lesson 03 Part 02)
     */
    boolean deleteUser(String username) throws AdminOperationException;

    /**
     * Retrieves all users from database.
     */
    List<Map<String, Object>> getAllUsers() throws AdminOperationException;

    /**
     * Method Overloading: Retrieves users filtered by role (Lesson 03 Part 02)
     */
    List<Map<String, Object>> getAllUsers(String roleFilter) throws AdminOperationException;

    /**
     * Unlocks an account locked due to consecutive failed login attempts.
     */
    boolean unlockAccount(int userId) throws AdminOperationException;

    /**
     * Default method providing utility logging without forcing subclasses to implement.
     * Demonstrates Lesson 04 Slide 32 ("Using default methods").
     */
    default void logAudit(String action, String detail) {
        System.out.println("[AUDIT LOG] " + System.currentTimeMillis() + " - " + action + ": " + detail);
    }

    /**
     * Static utility method in interface.
     * Demonstrates Lesson 04 Slide 33 ("Using static methods").
     */
    static boolean isValidRole(String role) {
        if (role == null) return false;
        String r = role.trim().toUpperCase();
        return r.equals("ADMIN") || r.equals("LECTURER") || r.equals("TECH_OFFICER") || r.equals("UNDERGRADUATE");
    }
}
