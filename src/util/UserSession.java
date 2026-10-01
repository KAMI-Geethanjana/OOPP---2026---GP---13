package util;

import model.User;

public class UserSession {
    private static User currentUser;

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

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public static void cleanSession() {
        currentUser = null;
    }
}