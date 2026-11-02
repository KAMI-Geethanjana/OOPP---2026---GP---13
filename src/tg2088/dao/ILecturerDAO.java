package tg2088.dao;

import common.model.Course;
import common.model.Result;
import tg2088.exception.LecturerOperationException;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import java.util.List;
import java.util.Map;


public interface ILecturerDAO {


    double MIN_ATTENDANCE_REQUIREMENT = 80.0; // 80% mandatory attendance threshold
    double MIN_CA_PASS_MARK = 40.0;           // 40% mandatory CA threshold
    int STANDARD_SESSIONS_COUNT = 15;         // standard sessions per semester component


    Lecturer getLecturerByUserId(int userId);


    List<Course> getCoursesByLecturer(int lecturerId);


    List<Result> getResultsByCourse(String courseCode);


    List<Result> getResultsByCourse(String courseCode, String eligibilityFilter);


    boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                               double totalMark, String eligibilityStatus, String grade,
                               double gradePoint) throws LecturerOperationException;


    List<CourseMaterial> getCourseMaterials(String courseCode);


    boolean addCourseMaterial(CourseMaterial material) throws LecturerOperationException;


    boolean deleteCourseMaterial(int materialId);


    List<Map<String, Object>> getStudentAttendanceSummary(String courseCode);


    boolean updateProfile(int userId, String fullName, String email, String phoneNo);


    default void printModuleStatus() {
        System.out.println("[ILecturerDAO Contract] TG2088 Lecturer DAO service active and operational.");
    }


    static boolean isPassGrade(String grade) {
        if (grade == null) return false;
        String g = grade.trim().toUpperCase();
        return !g.equals("E") && !g.equals("F") && !g.equals("-");
    }
}
