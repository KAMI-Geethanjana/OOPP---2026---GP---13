package tg2088.model;

import java.sql.Date;
import java.sql.Timestamp;


public class CourseMaterial {


    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Data Hiding)
    // All attributes are declared as 'private' so external classes cannot
    // modify them directly. Access is controlled via public getters/setters.
    // =========================================================================

    private int materialId;
    private String courseCode;
    private int lecturerId;
    private String title;
    private String filePath;
    private Date uploadedDate;
    // Expiry date and time for student visibility window
    private Timestamp expiryDate;


    // Instance Initializer Block: Runs before constructor execution
    {
        this.uploadedDate = new Date(System.currentTimeMillis());
        this.expiryDate = null; // null means permanent / no expiry
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #1)
    // Default constructor chaining to the parameterized constructor via 'this(...)'
    // =========================================================================
    public CourseMaterial() {
        this(0, "", 0, "Untitled Material", "", new Date(System.currentTimeMillis()), null);
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #2)
    // Overloaded constructor with 6 parameters (backward compatibility)
    // Chains to the 7-parameter constructor using 'this(...)'
    // =========================================================================
    public CourseMaterial(int materialId, String courseCode, int lecturerId, String title, String filePath, Date uploadedDate) {
        this(materialId, courseCode, lecturerId, title, filePath, uploadedDate, null);
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Constructor Overloading #3)
    // Overloaded constructor with 7 parameters (including Timestamp expiryDate)
    // =========================================================================
    public CourseMaterial(int materialId, String courseCode, int lecturerId, String title, String filePath, Date uploadedDate, Timestamp expiryDate) {
        // 'this' refers to current object instance fields
        this.materialId = materialId;
        this.courseCode = courseCode;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        if (uploadedDate != null) {
            this.uploadedDate = uploadedDate;
        }
        this.expiryDate = expiryDate;
    }


    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Getters and Setters with Validation)
    // Mutators validate incoming data before updating object state
    // =========================================================================

    public int getMaterialId() {
        return this.materialId;
    }

    public void setMaterialId(int materialId) {
        // Validation logic inside mutator (Encapsulation)
        if (materialId >= 0) {
            this.materialId = materialId;
        }
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode != null) {
            this.courseCode = courseCode.trim().toUpperCase();
        }
    }

    public int getLecturerId() {
        return this.lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        // Validation to prevent empty/null titles
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        } else {
            this.title = "Untitled Material";
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = (filePath != null) ? filePath.trim() : "";
    }

    public Date getUploadedDate() {
        return this.uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        if (uploadedDate != null) {
            this.uploadedDate = uploadedDate;
        }
    }

    public Timestamp getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }


    // =========================================================================
    // OOP CONCEPT: ENCAPSULATION (Encapsulated Business Logic Methods)
    // The internal calculations for expiry and countdown are encapsulated here,
    // protecting caller classes from having to manipulate raw timestamps.
    // =========================================================================

    /**
     * Checks if this material's student access window has expired.
     * If expiryDate is null, the material never expires (permanent).
     */
    public boolean isExpired() {
        if (this.expiryDate == null) {
            return false;
        }
        return System.currentTimeMillis() > this.expiryDate.getTime();
    }

    /**
     * Calculates the remaining time in milliseconds until expiry.
     * Returns 0 if already expired, Long.MAX_VALUE if no expiry set.
     */
    public long getRemainingMillis() {
        if (this.expiryDate == null) {
            return Long.MAX_VALUE;
        }
        long diff = this.expiryDate.getTime() - System.currentTimeMillis();
        return Math.max(0, diff);
    }

    /**
     * Returns a human-readable countdown string (Days, Hours, Minutes, Seconds).
     * Example: "02d 14h 32m 10s" or "EXPIRED" or "Permanent"
     */
    public String getRemainingTimeFormatted() {
        if (this.expiryDate == null) {
            return "Permanent";
        }
        long remaining = getRemainingMillis();
        if (remaining <= 0) {
            return "EXPIRED";
        }
        long seconds = remaining / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, secs);
        } else {
            return String.format("%02dm %02ds", minutes, secs);
        }
    }

    /**
     * Returns access status for display in table ("Active", "Expired", or "Permanent")
     */
    public String getStatus() {
        if (this.expiryDate == null) {
            return "Permanent";
        }
        return isExpired() ? "Expired" : "Active";
    }


    // =========================================================================
    // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding)
    // Overriding toString() from java.lang.Object to provide a meaningful representation
    // =========================================================================
    @Override
    public String toString() {
        return "CourseMaterial [materialId=" + this.materialId +
                ", courseCode=" + this.courseCode +
                ", title=" + this.title +
                ", filePath=" + this.filePath +
                ", uploadedDate=" + this.uploadedDate +
                ", expiryDate=" + this.expiryDate + "]";
    }


    // Methods specified in UML Class Diagram

    public void upload() {
        System.out.println("Uploading course material: " + this.title);
    }

    public void update() {
        System.out.println("Updating course material: " + this.title);
    }

    public void delete() {
        System.out.println("Deleting course material ID: " + this.materialId);
    }
}
