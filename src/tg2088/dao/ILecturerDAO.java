//interfaces file
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

        //This method returns all courses assigned to a lecturer.
        List<Course> getCoursesByLecturer(int lecturerId);

        // I am using in Overloading Methods
        List<Result> getResultsByCourse(String courseCode);


        List<Result> getResultsByCourse(String courseCode, String eligibilityFilter);


        boolean saveOrUpdateResult(int enrollmentId, double caMark, double finalExamMark,
                                   double totalMark, String eligibilityStatus, String grade,
                                   double gradePoint) throws LecturerOperationException;

        //This method retrieves course materials for a given course.
        List<CourseMaterial> getCourseMaterials(String courseCode);

        //This method adds new course materials.
        boolean addCourseMaterial(CourseMaterial material) throws LecturerOperationException;

        //This method deletes a course material using its ID.
        boolean deleteCourseMaterial(int materialId);

        //This method returns student attendance details using a List of Maps
        List<Map<String, Object>> getStudentAttendanceSummary(String courseCode);

        //This method updates lecturer profile details.
        boolean updateProfile(int userId, String fullName, String email, String phoneNo);


        default void printModuleStatus() {
            System.out.println("[ILecturerDAO Contract] TG2088 Lecturer DAO service active and operational.");
        }

        //This static method checks whether a grade is a passing grade.
        static boolean isPassGrade(String grade) {
            if (grade == null) return false;
            String g = grade.trim().toUpperCase();
            return !g.equals("E") && !g.equals("F") && !g.equals("-");
        }
}
