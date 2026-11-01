package tg2088.dao;

import common.model.Course;
import common.model.Result;
import tg2088.exception.LecturerOperationException;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * Lesson 04: Interfaces
 * ============================================================================
 * Reference: Lesson 04, Slide 5-7 ("Interface - Introduction")
 * - Acts as an agreed contract spelling out how software components interact.
 * - Variables are implicitly 'public static final' (Constants, Slide 23 & 25).
 * - Methods are implicitly 'public abstract' (Slide 21).
 * - Contains Java 8+ 'default' method (Slide 32) and 'static' method (Slide 33).
 * - Demonstrates loose coupling and full abstraction (Slide 8, 35).
 * ============================================================================
 */
public interface ILecturerDAO {

    // ========================================================================
    // Interface Constants (public static final by default - Lesson 04, Slide 23)
    // ========================================================================
    double MIN_ATTENDANCE_REQUIREMENT = 80.0; // 80% mandatory attendance threshold
    double MIN_CA_PASS_MARK = 40.0;           // 40% mandatory CA threshold
    int STANDARD_SESSIONS_COUNT = 15;         // standard sessions per semester component

    // ========================================================================
    // Abstract Method Declarations (public abstract by default - Slide 21)
    // ========================================================================

    /**
     * Retrieves lecturer profile information by their associated user ID.
     */
    Lecturer getLecturerByUserId(int userId);

    /**
     * Retrieves all courses assigned to a specific lecturer.
     */
    List<Course> getCoursesByLecturer(int lecturerId);

    /**
     * Retrieves all student results enrolled in a given course.
     */
    List<Result> getResultsByCourse(String courseCode);

    /**
     * Overloaded method to retrieve results filtered by eligibility status.
     * Demonstrates Static Polymorphism / Method Overloading (Lesson 03 Part 02, Slide 13-15).
     */
    List<Result> getResultsByCourse(String courseCode, String eligibilityFilter);

    /**
     * Saves or updates a student result record into the database.
     * Throws custom checked exception on validation/database error (Lesson 05, Slide 40).
     */
    boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                               double totalMark, String eligibilityStatus, String grade,
                               double gradePoint) throws LecturerOperationException;

    /**
     * Retrieves all course materials uploaded for a specific course code.
     */
    List<CourseMaterial> getCourseMaterials(String courseCode);

    /**
     * Uploads new course material to the repository.
     * Throws custom checked exception if input is invalid (Lesson 05, Slide 48).
     */
    boolean addCourseMaterial(CourseMaterial material) throws LecturerOperationException;

    /**
     * Deletes course material record by its unique material ID.
     */
    boolean deleteCourseMaterial(int materialId);

    /**
     * Retrieves aggregated student attendance and medical summary.
     */
    List<Map<String, Object>> getStudentAttendanceSummary(String courseCode);

    /**
     * Updates lecturer profile contact details.
     */
    boolean updateProfile(int userId, String fullName, String email, String phoneNo);

    // ========================================================================
    // Java 8 Default Method (Lesson 04, Slide 32)
    // Supplies reusable default logic without forcing implementing classes to override.
    // ========================================================================
    default void printModuleStatus() {
        System.out.println("[ILecturerDAO Contract] TG2088 Lecturer DAO service active and operational.");
    }

    // ========================================================================
    // Java 8 Static Method (Lesson 04, Slide 33)
    // Utility method accessible via ILecturerDAO.isPassGrade(grade)
    // ========================================================================
    static boolean isPassGrade(String grade) {
        if (grade == null) return false;
        String g = grade.trim().toUpperCase();
        return !g.equals("E") && !g.equals("F") && !g.equals("-");
    }
}
