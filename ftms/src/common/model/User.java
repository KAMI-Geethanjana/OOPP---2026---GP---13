package common.model;

import javax.swing.JFrame;

/**
 * User - Abstract Base Class for all system actors.
 * Demonstrates:
 * 1. Abstraction (cannot be instantiated directly, defines core contracts)
 * 2. Encapsulation (private fields, controlled access via getters/setters)
 * 3. Polymorphic dashboard opening via openDashboard()
 */
public abstract class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNo;
    private String profilePic;
    private String role; // 'ADMIN', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE'
    private boolean active;
    private int failedLogins;

    public User() {
        this.active = true;
        this.failedLogins = 0;
    }

    public User(int userId, String username, String passwordHash, String fullName, String email, String phoneNo, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.role = role;
        this.active = true;
        this.failedLogins = 0;
    }

    // Abstract method to demonstrate Polymorphism
    public abstract String getRoleDisplayName();

    // Polymorphic method to display role-specific dashboard
    public abstract void openDashboard(JFrame parent);

    // Getters and Setters (Encapsulation)
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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getFailedLogins() { return failedLogins; }
    public void setFailedLogins(int failedLogins) { this.failedLogins = failedLogins; }

    @Override
    public String toString() {
        return fullName + " (" + username + " - " + role + ")";
    }
}
