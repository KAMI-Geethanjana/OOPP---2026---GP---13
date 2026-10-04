CREATE DATABASE ftms_db;
USE ftms_db;

-- 1. Department Table
CREATE TABLE department (
                            department_id INT AUTO_INCREMENT PRIMARY KEY,
                            department_name VARCHAR(100) NOT NULL,
                            department_code VARCHAR(10) UNIQUE NOT NULL
);

-- 2. Users Table
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100),
                       phone_no VARCHAR(20),
                       profile_pic VARCHAR(255),
                       role ENUM('ADMIN', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') NOT NULL,
                       is_active TINYINT(1) DEFAULT 1,
                       failed_logins INT DEFAULT 0
);

-- 3. Lecturer Table
CREATE TABLE lecturer (
                          lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT UNIQUE NOT NULL,
                          employee_id VARCHAR(20) UNIQUE NOT NULL,
                          department_id INT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 4. Student Table
CREATE TABLE student (
                         student_id INT AUTO_INCREMENT PRIMARY KEY,
                         index_no VARCHAR(50) NOT NULL UNIQUE,
                         full_name VARCHAR(255) NOT NULL
);

-- 5. Undergraduate Table
CREATE TABLE undergraduate (
                               student_id VARCHAR(20) PRIMARY KEY,
                               user_id INT UNIQUE NOT NULL,
                               department_id INT NOT NULL,
                               batch VARCHAR(10) NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                               FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 6. Course Table
CREATE TABLE course (
                        course_code VARCHAR(10) PRIMARY KEY,
                        course_title VARCHAR(150) NOT NULL,
                        credit_theory TINYINT NOT NULL DEFAULT 0,
                        credit_practical TINYINT NOT NULL DEFAULT 0,
                        department_id INT NOT NULL,
                        lecturer_id INT,
                        FOREIGN KEY (department_id) REFERENCES department(department_id),
                        FOREIGN KEY (lecturer_id) REFERENCES lecturer(lecturer_id) ON DELETE SET NULL
);

-- 7. Enrollment Table
CREATE TABLE enrollment (
                            enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                            student_id VARCHAR(20) NOT NULL,
                            course_code VARCHAR(10) NOT NULL,
                            semester INT NOT NULL,
                            attempt_type VARCHAR(20) DEFAULT 'PROPER',
                            FOREIGN KEY (student_id) REFERENCES undergraduate(student_id) ON DELETE CASCADE,
                            FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE
);

-- 8. Assessment Table
CREATE TABLE assessment (
                            assessment_id INT AUTO_INCREMENT PRIMARY KEY,
                            course_code VARCHAR(10) NOT NULL,
                            component_name VARCHAR(50) NOT NULL,
                            component_type ENUM('CA', 'FINAL') NOT NULL,
                            weight DECIMAL(5,2) NOT NULL,
                            FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE
);

-- 9. Mark Table
CREATE TABLE mark (
                      mark_id INT AUTO_INCREMENT PRIMARY KEY,
                      enrollment_id INT NOT NULL,
                      assessment_id INT NOT NULL,
                      marks DECIMAL(5,2) NOT NULL,
                      ca_marks DOUBLE DEFAULT 0,
                      final_marks DOUBLE DEFAULT 0,
                      FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id) ON DELETE CASCADE,
                      FOREIGN KEY (assessment_id) REFERENCES assessment(assessment_id) ON DELETE CASCADE
);

-- 10. Result Table
CREATE TABLE result (
                        result_id INT AUTO_INCREMENT PRIMARY KEY,
                        enrollment_id INT UNIQUE NOT NULL,
                        ca_mark DECIMAL(5,2),
                        final_exam_mark DECIMAL(5,2),
                        total_mark DECIMAL(5,2),
                        eligibility_status ENUM('ELIGIBLE','NOT_ELIGIBLE') DEFAULT 'NOT_ELIGIBLE',
                        grade VARCHAR(3),
                        grade_point DECIMAL(3,2),
                        total_marks DOUBLE DEFAULT 0,
                        FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id) ON DELETE CASCADE
);

-- 11. Notice Table (Admin creates, updates, and deletes notices)
CREATE TABLE notice (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    target_audience ENUM('ALL', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') DEFAULT 'ALL',
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 12. Timetable Table (Admin creates and maintains lecture/practical/exam schedules)
CREATE TABLE timetable (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(10) NOT NULL,
    department_id INT NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL', 'EXAM') NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(50) NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

-- 13. Technical Officer table
CREATE TABLE technical_officer (
    technical_officer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department_id INT NOT NULL,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    FOREIGN KEY (department_id)
        REFERENCES department(department_id)
);

-- 14. Attendance Session
CREATE TABLE attendance_session (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(10) NOT NULL,
    session_date DATE NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
    session_number INT NOT NULL,

    FOREIGN KEY (course_code)
        REFERENCES course(course_code)
        ON DELETE CASCADE
);

-- 15. Attendance
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    status ENUM('PRESENT', 'ABSENT') NOT NULL,

    FOREIGN KEY (session_id)
        REFERENCES attendance_session(session_id)
        ON DELETE CASCADE,

    FOREIGN KEY (student_id)
        REFERENCES undergraduate(student_id)
        ON DELETE CASCADE,

    UNIQUE (session_id, student_id)
);

-- 16. Medical
CREATE TABLE medical (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    medical_date DATE NOT NULL,
    reason VARCHAR(255),
    document_path VARCHAR(255),
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED')
        DEFAULT 'PENDING',

    FOREIGN KEY (student_id)
        REFERENCES undergraduate(student_id)
        ON DELETE CASCADE,

    FOREIGN KEY (course_code)
        REFERENCES course(course_code)
        ON DELETE CASCADE
);




