package tg2088.model;

import java.sql.Date;

/**
 * ============================================================================
 * Lessons 01, 02, 03 (Part 04): Classes, Objects, this & Encapsulation
 * ============================================================================
 * Reference:
 * - Lesson 02, Slide 12-14: Class anatomy (Fields, Initializers, Constructors, Methods)
 * - Lesson 02 Part 02, Slide 5-7: Usage of 'this' to distinguish instance variables
 * - Lesson 02 Part 02, Slide 11: Instance Initializer block
 * - Lesson 03 Part 04, Slide 9-13: Encapsulation (Private fields + Public getters/setters)
 * - Lesson 03 Part 04, Slide 20: Control over data via mutator validation
 * ============================================================================
 */
public class CourseMaterial {

    // ========================================================================
    // Private Fields (Data / Information Hiding - Lesson 03 Part 04, Slide 11)
    // ========================================================================
    private int materialId;
    private String courseCode;
    private int lecturerId;
    private String title;
    private String filePath;
    private Date uploadedDate;

    // ========================================================================
    // Instance Initializer Block (Lesson 02 Part 02, Slide 11-12)
    // Runs before constructor execution every time an instance is created
    // ========================================================================
    {
        this.uploadedDate = new Date(System.currentTimeMillis());
    }

    // ========================================================================
    // Default Constructor (no-arg) (Lesson 02, Slide 34)
    // Demonstrates Constructor Chaining using 'this' keyword (Lesson 02 Part 02, Slide 8-9)
    // ========================================================================
    public CourseMaterial() {
        this(0, "", 0, "Untitled Material", "", new Date(System.currentTimeMillis()));
    }

    // ========================================================================
    // Parameterized Constructor (Lesson 02, Slide 38)
    // ========================================================================
    public CourseMaterial(int materialId, String courseCode, int lecturerId, String title, String filePath, Date uploadedDate) {
        // 'this' refers to the current object instance (Lesson 02 Part 02, Slide 5-7)
        this.materialId = materialId;
        this.courseCode = courseCode;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        if (uploadedDate != null) {
            this.uploadedDate = uploadedDate;
        }
    }

    // ========================================================================
    // Accessors (Getters) and Mutators (Setters) with Encapsulation (Lesson 03 Part 04)
    // ========================================================================
    public int getMaterialId() {
        return this.materialId;
    }

    public void setMaterialId(int materialId) {
        // Validation logic inside mutator (Lesson 03 Part 04, Slide 20)
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
        // Validating input data to maintain object integrity
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

    // ========================================================================
    // Overridden toString method (Lesson 02 Part 02, Slide 10)
    // ========================================================================
    @Override
    public String toString() {
        return "CourseMaterial [materialId=" + this.materialId +
                ", courseCode=" + this.courseCode +
                ", title=" + this.title +
                ", filePath=" + this.filePath +
                ", uploadedDate=" + this.uploadedDate + "]";
    }

    // ========================================================================
    // Methods specified in UML Class Diagram (ICT2132 Task 02)
    // ========================================================================
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
