package tg2123.dao;

import common.model.Course;
import common.model.Medical;
import common.model.Timetable;
import tg2123.model.TechnicalOfficer;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Contract for operations available to the Technical Officer module.
 * The dashboard can depend on this interface rather than SQL implementation details.
 */
public interface TechnicalOfficerRepository {
    TechnicalOfficer getTechnicalOfficerByUserId(int userId);
    List<Course> getCoursesByDepartment(int departmentId);
    List<Map<String, Object>> getStudentsForAttendance(String courseCode, Date sessionDate,
                                                       String sessionType, int departmentId);
    boolean saveAttendance(int studentId, String courseCode, Date sessionDate,
                           String sessionType, String status, int toId);
    List<Medical> getMedicalRequestsByDepartment(int departmentId);
    boolean updateMedicalStatus(int medicalId, String newStatus, int toId, int departmentId);
    List<Timetable> getTimetableByDepartment(int departmentId);
    boolean updateProfile(int userId, String fullName, String email, String phoneNo);
    boolean updateProfilePhoto(int userId, String photoPath);
}
