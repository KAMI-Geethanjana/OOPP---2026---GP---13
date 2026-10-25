package model;

import exception.ValidationException;

/**
 * Base abstract class for all users in FTMS.
 * - Abstraction (abstract class with abstract methods)
 * - Encapsulation (private fields with validated getters and setters)
 * - Constructor Overloading and Constructor Chaining using this()
 * - Method Overloading (updateProfile variants)
 */
public abstract class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNo;
    private String profilePic;
    private String role;
    private boolean isActive;
    private int failedLogins;

    /**
     * Default constructor demonstrating constructor chaining with this(...)
     */
    public User() {
        this(0, "", "", "", "", "", "USER", true, 0);
    }

    /**
     * Parameterized Constructor
     */
    public User(int userId, String username, String passwordHash, String fullName,
                String email, String phoneNo, String role, boolean isActive, int failedLogins) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.role = role;
        this.isActive = isActive;
        this.failedLogins = failedLogins;
    }

    /**
     * Abstract method - must be implemented by concrete subclasses.
     */
    public abstract String getRoleDisplayName();

    /**
     * Abstract method for role-specific dashboard class name (Dynamic Binding).
     */
    public abstract String getDashboardRoute();

    public boolean login(String inputPassword) {
        if (!isActive) return false;
        return this.passwordHash != null && this.passwordHash.equals(inputPassword);
    }

    public void logout() {
        // Session cleanup hook
    }

    /**
     * Method Overloading:
     * Variant 1: Basic profile update
     */
    public void updateProfile(String fullName, String email, String phoneNo) throws ValidationException {
        setFullName(fullName);
        setEmail(email);
        setPhoneNo(phoneNo);
    }

    /**
     * Method Overloading:
     * Variant 2: Extended profile update including profile pic
     */
    public void updateProfile(String fullName, String email, String phoneNo, String profilePic) throws ValidationException {
        this.updateProfile(fullName, email, phoneNo);
        this.profilePic = profilePic;
    }

    // =========================================================
    // Encapsulation - Accessors (Getters) and Mutators (Setters)
    // with validation
    // =========================================================

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) throws ValidationException {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new ValidationException("Invalid email format: " + email);
            }
            this.email = email.trim();
        } else {
            this.email = "";
        }
    }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo != null ? phoneNo.trim() : "";
    }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role != null ? role.toUpperCase() : "USER"; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getFailedLogins() { return failedLogins; }
    public void setFailedLogins(int failedLogins) { this.failedLogins = failedLogins; }

    @Override
    public String toString() {
        return "User [ID=" + userId + ", Username=" + username + ", Name=" + fullName + ", Role=" + role + "]";
    }
}
