-- ==========================================================
-- FTMS Database Final Setup Script (Based on your Terminal state)
-- ==========================================================

DROP DATABASE IF EXISTS ftms_db;
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


-- ==========================================================
-- INITIAL SAMPLE DATA INSERTION
-- ==========================================================

-- 1. Department
INSERT INTO department (department_id, department_name, department_code) VALUES
    (1, 'Department of Information and Communication Technology', 'DICT');

-- 2. Users (Admin, Lecturer, and Undergraduates)
INSERT INTO users (user_id, username, password_hash, full_name, email, role) VALUES
                                                                                 (1, 'admin', '1234', 'System Admin', 'admin@fot.ruh.ac.lk', 'ADMIN'),
                                                                                 (2, 'lecturer1', '1234', 'Dr. Perera', 'perera@fot.ruh.ac.lk', 'LECTURER'),
INSERT INTO users
(username, password_hash, full_name, email, phone_no, profile_pic, role, is_active, failed_logins)
VALUES
    ('tg2001', 'tg@2001', 'Nethmi Perera', 'tg20242001@student.ruh.ac.lk', '0712345601', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2002', 'tg@2002', 'Kavindu Silva', 'tg20242002@student.ruh.ac.lk', '0723456702', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2003', 'tg@2003', 'Dinithi Fernando', 'tg20242003@student.ruh.ac.lk', '0754567803', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2004', 'tg@2004', 'Hasitha Jayawardena', 'tg20242004@student.ruh.ac.lk', '0765678904', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2005', 'tg@2005', 'Sachini Perera', 'tg20242005@student.ruh.ac.lk', '0776789005', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2006', 'tg@2006', 'Tharindu Bandara', 'tg20242006@student.ruh.ac.lk', '0717890106', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2007', 'tg@2007', 'Minsara Wijesinghe', 'tg20242007@student.ruh.ac.lk', '0728901207', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2008', 'tg@2008', 'Ishara Madushani', 'tg20242008@student.ruh.ac.lk', '0759012308', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2009', 'tg@2009', 'Dinuka Rathnayake', 'tg20242009@student.ruh.ac.lk', '0760123409', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2010', 'tg@2010', 'Senuri Gunawardena', 'tg20242010@student.ruh.ac.lk', '0771234510', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2011', 'tg@2011', 'Pasindu Lakshan', 'tg20242011@student.ruh.ac.lk', '0712345611', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2012', 'tg@2012', 'Amaya Sandaruwani', 'tg20242012@student.ruh.ac.lk', '0723456712', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2013', 'tg@2013', 'Chamod Perera', 'tg20242013@student.ruh.ac.lk', '0754567813', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2014', 'tg@2014', 'Hiruni Madushika', 'tg20242014@student.ruh.ac.lk', '0765678914', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2015', 'tg@2015', 'Ravindu Dissanayake', 'tg20242015@student.ruh.ac.lk', '0776789015', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2016', 'tg@2016', 'Kavisha Nethmini', 'tg20242016@student.ruh.ac.lk', '0717890116', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2017', 'tg@2017', 'Shehan Maduranga', 'tg20242017@student.ruh.ac.lk', '0728901217', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2018', 'tg@2018', 'Piumi Hansika', 'tg20242018@student.ruh.ac.lk', '0759012318', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg2019', 'tg@2019', 'Isuru Prabath', 'tg20242019@student.ruh.ac.lk', '0760123419', NULL, 'UNDERGRADUATE', 1, 0),
    ('tg1980', 'tg@2080', 'Malith Sandeepa', 'tg20231980@student.ruh.ac.lk', '0771234520', NULL, 'UNDERGRADUATE', 1, 0);
-- 3. Lecturer
INSERT INTO lecturer (lecturer_id, user_id, employee_id, department_id) VALUES
    (1, 2, 'EMP/1001', 1);

-- 4. Student (Table with Index No)
INSERT INTO student (student_id, index_no, full_name) VALUES
    (1, 'TG/2024/2088', 'Thenuwara Hannadige Dasula Wosada');

-- 5. Undergraduate Students
INSERT INTO undergraduate (student_id, user_id, department_id, batch) VALUES
INSERT INTO undergraduate
(student_id, user_id, department_id, batch)
VALUES
    ('TG/2024/2001', 8, 1, '2023/2024'),
    ('TG/2024/2002', 9, 1, '2023/2024'),
    ('TG/2024/2003', 10, 1, '2023/2024'),
    ('TG/2024/2004', 11, 1, '2023/2024'),
    ('TG/2024/2005', 12, 1, '2023/2024'),
    ('TG/2024/2006', 13, 1, '2023/2024'),
    ('TG/2024/2007', 14, 1, '2023/2024'),
    ('TG/2024/2008', 15, 1, '2023/2024'),
    ('TG/2024/2009', 16, 1, '2023/2024'),
    ('TG/2024/2010', 17, 1, '2023/2024'),
    ('TG/2024/2011', 18, 1, '2023/2024'),
    ('TG/2024/2012', 19, 1, '2023/2024'),
    ('TG/2024/2013', 20, 1, '2023/2024'),
    ('TG/2024/2014', 21, 1, '2023/2024'),
    ('TG/2024/2015', 22, 1, '2023/2024'),
    ('TG/2024/2016', 23, 1, '2023/2024'),
    ('TG/2024/2017', 24, 1, '2023/2024'),
    ('TG/2024/2018', 25, 1, '2023/2024'),
    ('TG/2024/2019', 26, 1, '2023/2024'),
    ('TG/2023/1980', 27, 1, '2022/2023');


-- 6. Courses
INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) VALUES
                                                                                                                ('ICT1113', 'Introduction to ICT', 2, 1, 1, 1),
                                                                                                                ('ICT1122', 'Object Oriented Programming', 1, 2, 1, 1),
                                                                                                                ('ICT1133', 'Data Structures and Algorithms', 2, 1, 1, 1),
                                                                                                                ('ICT2132', 'Advanced Programming', 2, 0, 1, 1);

INSERT INTO course
(course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id)
VALUES
    ('ENG2112', 'English III', 2, NULL, NULL, NULL),
    ('ICT2113', 'Data Structures and Algorithms', 2, NULL, 1, NULL),
    ('ICT2122', 'Object Oriented Programming', 2, NULL, 1, NULL),
    ('ICT2132', 'Object Oriented Programming Practicum', NULL, 2, 1, NULL),
    ('ICT2142', 'E-Business Systems', 2, NULL, 1, NULL),
    ('ICT2152', 'Object Oriented Analysis and Design', 2, NULL, 1, NULL),
    ('ICT2162', 'Management Information Systems', 2, NULL, 1, NULL),
    ('TCS2112', 'Business Economics', 2, NULL, NULL, NULL),
    ('TCS2121', 'Soft Skills', 2, NULL, NULL, NULL);

-- 7. Enrollments
INSERT INTO enrollment (enrollment_id, student_id, course_code, semester, attempt_type) VALUES
                                                                                            (1, 'TG/2024/2001', 'ICT2132', 1, 'PROPER'),
                                                                                            (2, 'TG/2024/2002', 'ICT2132', 1, 'PROPER'),
                                                                                            (3, 'TG/2024/2003', 'ICT2132', 1, 'PROPER'),
                                                                                            (4, 'TG/2024/2004', 'ICT2132', 1, 'PROPER'),
                                                                                            (5, 'TG/2024/2001', 'ICT1122', 1, 'PROPER'),
                                                                                            (6, 'TG/2024/2002', 'ICT1122', 1, 'PROPER'),
                                                                                            (8, 'TG/2024/2088', 'ICT1122', 1, 'PROPER');

INSERT INTO enrollment
(student_id, course_code, semester, attempt_type)
VALUES

-- TG/2024/2001
('TG/2024/2001', 'ENG2112', 1, 'PROPER'),
('TG/2024/2001', 'ICT2113', 1, 'PROPER'),
('TG/2024/2001', 'ICT2122', 1, 'PROPER'),
('TG/2024/2001', 'ICT2132', 1, 'PROPER'),
('TG/2024/2001', 'ICT2142', 1, 'PROPER'),
('TG/2024/2001', 'ICT2152', 1, 'PROPER'),
('TG/2024/2001', 'ICT2162', 1, 'PROPER'),
('TG/2024/2001', 'TCS2112', 1, 'PROPER'),
('TG/2024/2001', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2002
('TG/2024/2002', 'ENG2112', 1, 'PROPER'),
('TG/2024/2002', 'ICT2113', 1, 'PROPER'),
('TG/2024/2002', 'ICT2122', 1, 'PROPER'),
('TG/2024/2002', 'ICT2132', 1, 'PROPER'),
('TG/2024/2002', 'ICT2142', 1, 'PROPER'),
('TG/2024/2002', 'ICT2152', 1, 'PROPER'),
('TG/2024/2002', 'ICT2162', 1, 'PROPER'),
('TG/2024/2002', 'TCS2112', 1, 'PROPER'),
('TG/2024/2002', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2003
('TG/2024/2003', 'ENG2112', 1, 'PROPER'),
('TG/2024/2003', 'ICT2113', 1, 'PROPER'),
('TG/2024/2003', 'ICT2122', 1, 'PROPER'),
('TG/2024/2003', 'ICT2132', 1, 'PROPER'),
('TG/2024/2003', 'ICT2142', 1, 'PROPER'),
('TG/2024/2003', 'ICT2152', 1, 'PROPER'),
('TG/2024/2003', 'ICT2162', 1, 'PROPER'),
('TG/2024/2003', 'TCS2112', 1, 'PROPER'),
('TG/2024/2003', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2004
('TG/2024/2004', 'ENG2112', 1, 'PROPER'),
('TG/2024/2004', 'ICT2113', 1, 'PROPER'),
('TG/2024/2004', 'ICT2122', 1, 'PROPER'),
('TG/2024/2004', 'ICT2132', 1, 'PROPER'),
('TG/2024/2004', 'ICT2142', 1, 'PROPER'),
('TG/2024/2004', 'ICT2152', 1, 'PROPER'),
('TG/2024/2004', 'ICT2162', 1, 'PROPER'),
('TG/2024/2004', 'TCS2112', 1, 'PROPER'),
('TG/2024/2004', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2005
('TG/2024/2005', 'ENG2112', 1, 'PROPER'),
('TG/2024/2005', 'ICT2113', 1, 'PROPER'),
('TG/2024/2005', 'ICT2122', 1, 'PROPER'),
('TG/2024/2005', 'ICT2132', 1, 'PROPER'),
('TG/2024/2005', 'ICT2142', 1, 'PROPER'),
('TG/2024/2005', 'ICT2152', 1, 'PROPER'),
('TG/2024/2005', 'ICT2162', 1, 'PROPER'),
('TG/2024/2005', 'TCS2112', 1, 'PROPER'),
('TG/2024/2005', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2006
('TG/2024/2006', 'ENG2112', 1, 'PROPER'),
('TG/2024/2006', 'ICT2113', 1, 'PROPER'),
('TG/2024/2006', 'ICT2122', 1, 'PROPER'),
('TG/2024/2006', 'ICT2132', 1, 'PROPER'),
('TG/2024/2006', 'ICT2142', 1, 'PROPER'),
('TG/2024/2006', 'ICT2152', 1, 'PROPER'),
('TG/2024/2006', 'ICT2162', 1, 'PROPER'),
('TG/2024/2006', 'TCS2112', 1, 'PROPER'),
('TG/2024/2006', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2007
('TG/2024/2007', 'ENG2112', 1, 'PROPER'),
('TG/2024/2007', 'ICT2113', 1, 'PROPER'),
('TG/2024/2007', 'ICT2122', 1, 'PROPER'),
('TG/2024/2007', 'ICT2132', 1, 'PROPER'),
('TG/2024/2007', 'ICT2142', 1, 'PROPER'),
('TG/2024/2007', 'ICT2152', 1, 'PROPER'),
('TG/2024/2007', 'ICT2162', 1, 'PROPER'),
('TG/2024/2007', 'TCS2112', 1, 'PROPER'),
('TG/2024/2007', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2008
('TG/2024/2008', 'ENG2112', 1, 'PROPER'),
('TG/2024/2008', 'ICT2113', 1, 'PROPER'),
('TG/2024/2008', 'ICT2122', 1, 'PROPER'),
('TG/2024/2008', 'ICT2132', 1, 'PROPER'),
('TG/2024/2008', 'ICT2142', 1, 'PROPER'),
('TG/2024/2008', 'ICT2152', 1, 'PROPER'),
('TG/2024/2008', 'ICT2162', 1, 'PROPER'),
('TG/2024/2008', 'TCS2112', 1, 'PROPER'),
('TG/2024/2008', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2009
('TG/2024/2009', 'ENG2112', 1, 'PROPER'),
('TG/2024/2009', 'ICT2113', 1, 'PROPER'),
('TG/2024/2009', 'ICT2122', 1, 'PROPER'),
('TG/2024/2009', 'ICT2132', 1, 'PROPER'),
('TG/2024/2009', 'ICT2142', 1, 'PROPER'),
('TG/2024/2009', 'ICT2152', 1, 'PROPER'),
('TG/2024/2009', 'ICT2162', 1, 'PROPER'),
('TG/2024/2009', 'TCS2112', 1, 'PROPER'),
('TG/2024/2009', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2010
('TG/2024/2010', 'ENG2112', 1, 'PROPER'),
('TG/2024/2010', 'ICT2113', 1, 'PROPER'),
('TG/2024/2010', 'ICT2122', 1, 'PROPER'),
('TG/2024/2010', 'ICT2132', 1, 'PROPER'),
('TG/2024/2010', 'ICT2142', 1, 'PROPER'),
('TG/2024/2010', 'ICT2152', 1, 'PROPER'),
('TG/2024/2010', 'ICT2162', 1, 'PROPER'),
('TG/2024/2010', 'TCS2112', 1, 'PROPER'),
('TG/2024/2010', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2011
('TG/2024/2011', 'ENG2112', 1, 'PROPER'),
('TG/2024/2011', 'ICT2113', 1, 'PROPER'),
('TG/2024/2011', 'ICT2122', 1, 'PROPER'),
('TG/2024/2011', 'ICT2132', 1, 'PROPER'),
('TG/2024/2011', 'ICT2142', 1, 'PROPER'),
('TG/2024/2011', 'ICT2152', 1, 'PROPER'),
('TG/2024/2011', 'ICT2162', 1, 'PROPER'),
('TG/2024/2011', 'TCS2112', 1, 'PROPER'),
('TG/2024/2011', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2012
('TG/2024/2012', 'ENG2112', 1, 'PROPER'),
('TG/2024/2012', 'ICT2113', 1, 'PROPER'),
('TG/2024/2012', 'ICT2122', 1, 'PROPER'),
('TG/2024/2012', 'ICT2132', 1, 'PROPER'),
('TG/2024/2012', 'ICT2142', 1, 'PROPER'),
('TG/2024/2012', 'ICT2152', 1, 'PROPER'),
('TG/2024/2012', 'ICT2162', 1, 'PROPER'),
('TG/2024/2012', 'TCS2112', 1, 'PROPER'),
('TG/2024/2012', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2013
('TG/2024/2013', 'ENG2112', 1, 'PROPER'),
('TG/2024/2013', 'ICT2113', 1, 'PROPER'),
('TG/2024/2013', 'ICT2122', 1, 'PROPER'),
('TG/2024/2013', 'ICT2132', 1, 'PROPER'),
('TG/2024/2013', 'ICT2142', 1, 'PROPER'),
('TG/2024/2013', 'ICT2152', 1, 'PROPER'),
('TG/2024/2013', 'ICT2162', 1, 'PROPER'),
('TG/2024/2013', 'TCS2112', 1, 'PROPER'),
('TG/2024/2013', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2014
('TG/2024/2014', 'ENG2112', 1, 'PROPER'),
('TG/2024/2014', 'ICT2113', 1, 'PROPER'),
('TG/2024/2014', 'ICT2122', 1, 'PROPER'),
('TG/2024/2014', 'ICT2132', 1, 'PROPER'),
('TG/2024/2014', 'ICT2142', 1, 'PROPER'),
('TG/2024/2014', 'ICT2152', 1, 'PROPER'),
('TG/2024/2014', 'ICT2162', 1, 'PROPER'),
('TG/2024/2014', 'TCS2112', 1, 'PROPER'),
('TG/2024/2014', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2015
('TG/2024/2015', 'ENG2112', 1, 'PROPER'),
('TG/2024/2015', 'ICT2113', 1, 'PROPER'),
('TG/2024/2015', 'ICT2122', 1, 'PROPER'),
('TG/2024/2015', 'ICT2132', 1, 'PROPER'),
('TG/2024/2015', 'ICT2142', 1, 'PROPER'),
('TG/2024/2015', 'ICT2152', 1, 'PROPER'),
('TG/2024/2015', 'ICT2162', 1, 'PROPER'),
('TG/2024/2015', 'TCS2112', 1, 'PROPER'),
('TG/2024/2015', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2016
('TG/2024/2016', 'ENG2112', 1, 'PROPER'),
('TG/2024/2016', 'ICT2113', 1, 'PROPER'),
('TG/2024/2016', 'ICT2122', 1, 'PROPER'),
('TG/2024/2016', 'ICT2132', 1, 'PROPER'),
('TG/2024/2016', 'ICT2142', 1, 'PROPER'),
('TG/2024/2016', 'ICT2152', 1, 'PROPER'),
('TG/2024/2016', 'ICT2162', 1, 'PROPER'),
('TG/2024/2016', 'TCS2112', 1, 'PROPER'),
('TG/2024/2016', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2017
('TG/2024/2017', 'ENG2112', 1, 'PROPER'),
('TG/2024/2017', 'ICT2113', 1, 'PROPER'),
('TG/2024/2017', 'ICT2122', 1, 'PROPER'),
('TG/2024/2017', 'ICT2132', 1, 'PROPER'),
('TG/2024/2017', 'ICT2142', 1, 'PROPER'),
('TG/2024/2017', 'ICT2152', 1, 'PROPER'),
('TG/2024/2017', 'ICT2162', 1, 'PROPER'),
('TG/2024/2017', 'TCS2112', 1, 'PROPER'),
('TG/2024/2017', 'TCS2121', 1, 'PROPER'),

-- TG/2024/2018
('TG/2024/2018', 'ENG2112', 1, 'PROPER'),
('TG/2024/2018', 'ICT2113', 1, 'PROPER'),
('TG/2024/2018', 'ICT2122', 1, 'PROPER'),
('TG/2024/2018', 'ICT2132', 1, 'PROPER'),
('TG/2024/2018', 'ICT2142', 1, 'PROPER'),
('TG/2024/2018', 'ICT2152', 1, 'PROPER'),
('TG/2024/2018', 'ICT2162', 1, 'PROPER'),
('TG/2024/2018', 'TCS2112', 1, 'PROPER'),
('TG/2024/2018', 'TCS2121', 1, 'REPEAT'),

-- TG/2024/2019
('TG/2024/2019', 'ENG2112', 1, 'PROPER'),
('TG/2024/2019', 'ICT2113', 1, 'PROPER'),
('TG/2024/2019', 'ICT2122', 1, 'PROPER'),
('TG/2024/2019', 'ICT2132', 1, 'PROPER'),
('TG/2024/2019', 'ICT2142', 1, 'PROPER'),
('TG/2024/2019', 'ICT2152', 1, 'REPEAT'),
('TG/2024/2019', 'ICT2162', 1, 'PROPER'),
('TG/2024/2019', 'TCS2112', 1, 'PROPER'),
('TG/2024/2019', 'TCS2121', 1, 'PROPER'),

-- TG/2023/1980 (Batch-missed student)
('TG/2023/1980', 'ENG2112', 1, 'PROPER'),
('TG/2023/1980', 'ICT2113', 1, 'REPEAT'),
('TG/2023/1980', 'ICT2122', 1, 'REPEAT'),
('TG/2023/1980', 'ICT2132', 1, 'PROPER'),
('TG/2023/1980', 'ICT2142', 1, 'PROPER'),
('TG/2023/1980', 'ICT2152', 1, 'PROPER'),
('TG/2023/1980', 'ICT2162', 1, 'PROPER'),
('TG/2023/1980', 'TCS2112', 1, 'PROPER'),
('TG/2023/1980', 'TCS2121', 1, 'PROPER');


-- 8. Results / Marks
INSERT INTO result (result_id, enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point, total_marks) VALUES
                                                                                                                                             (1, 5, 65.00, 70.00, 67.50, 'ELIGIBLE', 'B', 3.00, 67.50),
                                                                                                                                             (2, 6, 50.00, 55.00, 52.50, 'ELIGIBLE', 'C', 2.00, 52.50),
                                                                                                                                             (3, 8, 35.00, 50.00, 85.00, 'ELIGIBLE', 'A', 4.00, 85.00);