-- ==========================================================
-- FTMS Database - Module: Admin (TG/2024/2086)
-- Covers: User Management, Departments, Courses, Notices, Timetable
-- ==========================================================

DROP DATABASE IF EXISTS ftms_db;
CREATE DATABASE ftms_db;
USE ftms_db;

-- 1. Department Table (Admin manages faculty departments)
CREATE TABLE department (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    department_code VARCHAR(10) UNIQUE NOT NULL
);

-- 2. Base Users Table (Admin creates & manages all user accounts)
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

-- 3. Lecturer Role Table (Admin assigns department & employee ID)
CREATE TABLE lecturer (
    lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 4. Technical Officer Role Table (Admin assigns department)
CREATE TABLE technical_officer (
    to_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 5. Undergraduate Role Table (Admin/System manages student records)
CREATE TABLE undergraduate (
    student_id VARCHAR(20) PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    department_id INT NOT NULL,
    batch VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- 6. Course Table (Admin creates, updates, deletes courses & assigns lecturers)
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

-- 7. Notice Table (Admin creates, updates, and deletes notices)
CREATE TABLE notice (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    target_audience ENUM('ALL', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') DEFAULT 'ALL',
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 8. Timetable Table (Admin creates and maintains lecture/practical/exam schedules)
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

-- ==========================================================
-- SAMPLE DATA (Admin Module Initial Data)
-- ==========================================================

-- 1. Departments
INSERT INTO department (department_id, department_name, department_code) VALUES
    (1, 'Department of Information and Communication Technology', 'DICT'),
    (2, 'Department of Engineering Technology', 'DET');

-- 2. Users (Admin, Lecturers, TO, Students)
INSERT INTO users (user_id, username, password_hash, full_name, email, role, is_active) VALUES
    (1, 'admin', '1234', 'System Admin (Geethanjana)', 'admin@fot.ruh.ac.lk', 'ADMIN', 1),
    (2, 'lecturer1', '1234', 'Dr. Perera', 'perera@fot.ruh.ac.lk', 'LECTURER', 1),
    (3, 'to_officer1', '1234', 'Sunil Wickrama', 'sunil@fot.ruh.ac.lk', 'TECH_OFFICER', 1),
    (4, 'tg2086', '1234', 'K.A.M.I. Geethanjana', 'geethanjana@gmail.com', 'UNDERGRADUATE', 1),
    (5, 'tg2088', '1234', 'T.H.D. Wosada', 'wosad@gmail.com', 'UNDERGRADUATE', 1);

-- 3. Specific Role Records
INSERT INTO lecturer (lecturer_id, user_id, employee_id, department_id) VALUES
    (1, 2, 'EMP/1001', 1);

INSERT INTO technical_officer (to_id, user_id, employee_id, department_id) VALUES
    (1, 3, 'EMP/2001', 1);

INSERT INTO undergraduate (student_id, user_id, department_id, batch) VALUES
    ('TG/2024/2086', 4, 1, '2023/2024'),
    ('TG/2024/2088', 5, 1, '2023/2024');

-- 4. Courses (Admin manages courses)
INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) VALUES
    ('ICT1113', 'Introduction to ICT', 2, 1, 1, 1),
    ('ICT1122', 'Object Oriented Programming', 1, 2, 1, 1),
    ('ICT2132', 'Advanced Programming', 2, 0, 1, 1);

-- 5. Notices (Admin publishes notices)
INSERT INTO notice (notice_id, title, content, target_audience, posted_by) VALUES
    (1, 'Mid-Semester Timetable Released', 'The timetable for Mid-Semester examinations has been uploaded.', 'ALL', 1),
    (2, 'Lab 03 Maintenance', 'Software Lab 02 will be closed for maintenance on Friday.', 'UNDERGRADUATE', 1);

-- 6. Timetable (Admin manages session schedules)
INSERT INTO timetable (timetable_id, course_code, department_id, session_type, day_of_week, start_time, end_time, venue) VALUES
    (1, 'ICT1122', 1, 'THEORY', 'MONDAY', '08:00:00', '10:00:00', 'Lecture Hall 01'),
    (2, 'ICT1122', 1, 'PRACTICAL', 'TUESDAY', '13:00:00', '17:00:00', 'Software Lab 01'),
    (3, 'ICT2132', 1, 'THEORY', 'WEDNESDAY', '10:00:00', '12:00:00', 'Lecture Hall 02');
