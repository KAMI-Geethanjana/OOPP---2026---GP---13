package model;

/**
 * Base abstract class for all users in FTMS.
 * Demonstrates:
 * 1. Abstraction (abstract method getRoleDisplayName)
 * 2. Encapsulation (private fields with controlled getters and setters)
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

    public User() {
        this.isActive = true;
        this.failedLogins = 0;
    }

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

    public abstract String getRoleDisplayName();

    public boolean login(String inputPassword) {
        if (!isActive) return false;
        return this.passwordHash != null && this.passwordHash.equals(inputPassword);
    }

    public void logout() {
        // Clean session on logout
    }

    public void updateProfile(String fullName, String email, String phoneNo) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getFailedLogins() { return failedLogins; }
    public void setFailedLogins(int failedLogins) { this.failedLogins = failedLogins; }
}
