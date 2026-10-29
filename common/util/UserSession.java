package common.util;

import common.model.User;

/**
 * UserSession - Stores session state of the logged-in user.
 */
public class UserSession {

    private static User currentUser = null;

    public static void createSession(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getUserId() {
        return currentUser != null ? currentUser.getUserId() : 0;
    }

    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }

    public static String getFullName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
