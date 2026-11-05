package common.model;

import javax.swing.JFrame;


// =========================================================================
// OOP CONCEPT: ABSTRACTION (Abstract Superclass)
// 'User' is an abstract class that cannot be instantiated directly via 'new User()'.
// It defines a generic template for all system actors (Lecturer, Undergraduate, Admin),
// enforcing a common contract while leaving specific implementations to subclasses.
// =========================================================================
public abstract class User {

    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Data Hiding)
    // Attributes are declared 'private'. Subclasses and outside callers
    // must access them through public getters and setters.
    // =========================================================================
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


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #1)
    // =========================================================================
    public User() {
        this.active = true;
        this.failedLogins = 0;
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #2)
    // =========================================================================
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


    // =========================================================================
    // OOP CONCEPT: ABSTRACTION & POLYMORPHISM (Abstract Methods)
    // These methods have NO body here (pure abstract specification).
    // Every concrete subclass (Lecturer, Undergraduate, Admin) MUST provide
    // its own @Override implementation, enabling Dynamic Method Dispatch at runtime.
    // =========================================================================

    public abstract String getRoleDisplayName();

    public abstract void openDashboard(JFrame parent);


    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Accessors & Mutators)
    // =========================================================================
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


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding)
    // =========================================================================
    @Override
    public String toString() {
        return fullName + " (" + username + " - " + role + ")";
    }
}
