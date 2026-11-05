package tg2088.dao;

import common.model.Course;
import common.model.Result;
import tg2088.exception.LecturerOperationException;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import java.util.List;
import java.util.Map;


// =========================================================================
// OOP CONCEPT: ABSTRACTION (Pure Interface Contract)
// 'ILecturerDAO' is an interface defining an abstract contract.
// It contains operation signatures without any implementation details.
// This completely decouples the GUI layer from the database implementation layer.
// =========================================================================
public interface ILecturerDAO {

        // Interface Constants (implicitly public static final)
        double MIN_ATTENDANCE_REQUIREMENT = 80.0; // 80% mandatory attendance threshold
        double MIN_CA_PASS_MARK = 40.0;           // 40% mandatory CA threshold
        int STANDARD_SESSIONS_COUNT = 15;         // standard sessions per semester component


        // Abstract Method Signatures (Pure Abstraction)
        Lecturer getLecturerByUserId(int userId);

        List<Course> getCoursesByLecturer(int lecturerId);


        // =====================================================================
        // OOP CONCEPT: POLYMORPHISM - COMPILE-TIME (Method Overloading Specification)
        // Two versions of getResultsByCourse with different parameter signatures:
        // 1. Single parameter: courseCode
        // 2. Dual parameters: courseCode, eligibilityFilter
        // =====================================================================
        List<Result> getResultsByCourse(String courseCode);

        List<Result> getResultsByCourse(String courseCode, String eligibilityFilter);


        boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                                   double totalMark, String eligibilityStatus, String grade,
                                   double gradePoint) throws LecturerOperationException;

        // Course Materials Management Operations
        List<CourseMaterial> getCourseMaterials(String courseCode);

        boolean addCourseMaterial(CourseMaterial material) throws LecturerOperationException;

        boolean deleteCourseMaterial(int materialId);

        List<Map<String, Object>> getStudentAttendanceSummary(String courseCode);

        boolean updateProfile(int userId, String fullName, String email, String phoneNo);


        // Interface Default Method (Java 8 feature)
        default void printModuleStatus() {
            System.out.println("[ILecturerDAO Contract] TG2088 Lecturer DAO service active and operational.");
        }

        // Interface Static Method (Utility helper)
        static boolean isPassGrade(String grade) {
            if (grade == null) return false;
            String g = grade.trim().toUpperCase();
            return !g.equals("E") && !g.equals("F") && !g.equals("-");
        }
}
