
-- Faculty of Technology Management System (FTMS)
-- Group 13 - Database



DROP DATABASE IF EXISTS ftms_db;
CREATE DATABASE ftms_db;
USE ftms_db;

-- 1. Department Table
CREATE TABLE department (
                            department_id INT AUTO_INCREMENT PRIMARY KEY,
                            department_name VARCHAR(150) NOT NULL,
                            department_code VARCHAR(20) NOT NULL UNIQUE
);

-- 2. Users Table (Base entity for all user roles)
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       email VARCHAR(100),
                       phone_no VARCHAR(25),
                       profile_pic VARCHAR(255) DEFAULT 'default_avatar.png',
                       role ENUM('ADMIN', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') NOT NULL,
                       is_active BOOLEAN DEFAULT TRUE,
                       failed_logins INT DEFAULT 0
);

-- 3. Admin Table
CREATE TABLE admin (
                       admin_id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT UNIQUE NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Lecturer Table
CREATE TABLE lecturer (
                          lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT UNIQUE NOT NULL,
                          employee_id VARCHAR(50) UNIQUE NOT NULL,
                          department_id INT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 5. Technical Officer Table
CREATE TABLE technical_officer (
                                   to_id INT AUTO_INCREMENT PRIMARY KEY,
                                   user_id INT UNIQUE NOT NULL,
                                   employee_id VARCHAR(50) UNIQUE NOT NULL,
                                   department_id INT NOT NULL,
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                   FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 6. Undergraduate Table
CREATE TABLE undergraduate (
                               student_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT UNIQUE NOT NULL,
                               reg_no VARCHAR(50) NOT NULL UNIQUE,
                               batch_year INT NOT NULL,
                               status ENUM('ACTIVE', 'REPEAT', 'BATCH_MISSED') DEFAULT 'ACTIVE',
                               department_id INT NOT NULL,
                               contact_no VARCHAR(25),
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                               FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 7. Course Table
CREATE TABLE course (
                        course_code VARCHAR(20) PRIMARY KEY,
                        course_title VARCHAR(150) NOT NULL,
                        credit_theory INT NOT NULL DEFAULT 0,
                        credit_practical INT NOT NULL DEFAULT 0,
                        department_id INT NOT NULL,
                        lecturer_id INT,
                        FOREIGN KEY (department_id) REFERENCES department(department_id),
                        FOREIGN KEY (lecturer_id) REFERENCES lecturer(lecturer_id) ON DELETE SET NULL
);

-- 8. Course Material Table (Lecturer uploads)
CREATE TABLE course_material (
                                 material_id INT AUTO_INCREMENT PRIMARY KEY,
                                 course_code VARCHAR(20) NOT NULL,
                                 lecturer_id INT NOT NULL,
                                 title VARCHAR(150) NOT NULL,
                                 file_path VARCHAR(255) NOT NULL,
                                 uploaded_date DATE NOT NULL,
                                 FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE,
                                 FOREIGN KEY (lecturer_id) REFERENCES lecturer(lecturer_id) ON DELETE CASCADE
);

-- 9. Timetable Table
CREATE TABLE timetable (
                           timetable_id INT AUTO_INCREMENT PRIMARY KEY,
                           department_id INT NOT NULL,
                           course_code VARCHAR(20) NOT NULL,
                           day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday') NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           venue VARCHAR(100) NOT NULL,
                           session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                           FOREIGN KEY (department_id) REFERENCES department(department_id),
                           FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE
);

-- 10. Notice Table
CREATE TABLE notice (
                        notice_id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(200) NOT NULL,
                        content TEXT NOT NULL,
                        target_audience ENUM('ALL', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') DEFAULT 'ALL',
                        created_date DATE NOT NULL,
                        posted_by VARCHAR(100) DEFAULT 'System Admin'
);

-- 11. Enrollment Table
CREATE TABLE enrollment (
                            enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                            student_id INT NOT NULL,
                            course_code VARCHAR(20) NOT NULL,
                            academic_year INT NOT NULL,
                            semester INT NOT NULL,
                            attempt_type ENUM('PROPER', 'REPEAT', 'BATCH_MISSED') DEFAULT 'PROPER',
                            UNIQUE (student_id, course_code, academic_year, semester),
                            FOREIGN KEY (student_id) REFERENCES undergraduate(student_id) ON DELETE CASCADE,
                            FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE
);

-- 12. Attendance Table (Marked by Technical Officer)
CREATE TABLE attendance (
                            attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                            student_id INT NOT NULL,
                            course_code VARCHAR(20) NOT NULL,
                            session_date DATE NOT NULL,
                            session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                            status ENUM('PRESENT', 'ABSENT') NOT NULL,
                            to_id INT,
                            FOREIGN KEY (student_id) REFERENCES undergraduate(student_id) ON DELETE CASCADE,
                            FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE,
                            FOREIGN KEY (to_id) REFERENCES technical_officer(to_id) ON DELETE SET NULL
);

-- 13. Medical Records Table (Submitted by student, reviewed by Tech Officer)
CREATE TABLE medical (
                         medical_id INT AUTO_INCREMENT PRIMARY KEY,
                         student_id INT NOT NULL,
                         course_code VARCHAR(20) NOT NULL,
                         medical_date DATE NOT NULL,
                         reason TEXT NOT NULL,
                         document_path VARCHAR(255),
                         status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
                         reviewed_by_to_id INT,
                         FOREIGN KEY (student_id) REFERENCES undergraduate(student_id) ON DELETE CASCADE,
                         FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE,
                         FOREIGN KEY (reviewed_by_to_id) REFERENCES technical_officer(to_id) ON DELETE SET NULL
);

-- 14. Result / Marks Table (Maintained by Lecturer)
CREATE TABLE result (
                        result_id INT AUTO_INCREMENT PRIMARY KEY,
                        enrollment_id INT UNIQUE NOT NULL,
                        ca_mark DECIMAL(5,2) DEFAULT 0.00,
                        final_exam_mark DECIMAL(5,2) DEFAULT 0.00,
                        total_mark DECIMAL(5,2) DEFAULT 0.00,
                        eligibility_status ENUM('ELIGIBLE', 'NOT_ELIGIBLE') DEFAULT 'NOT_ELIGIBLE',
                        grade VARCHAR(5) DEFAULT '-',
                        grade_point DECIMAL(3,2) DEFAULT 0.00,
                        FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id) ON DELETE CASCADE
);