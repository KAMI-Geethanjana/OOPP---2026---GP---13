USE ftms_db;

-- Re-run කරන්න පුළුවන් වෙන්න (reverse order එකෙන් drop)
DROP TABLE IF EXISTS result;
DROP TABLE IF EXISTS mark;
DROP TABLE IF EXISTS assessment;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS undergraduate;
DROP TABLE IF EXISTS lecturer;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS department;

CREATE TABLE department (
                            department_id   INT AUTO_INCREMENT PRIMARY KEY,
                            department_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users (
                       user_id       INT AUTO_INCREMENT PRIMARY KEY,
                       username      VARCHAR(50)  NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name     VARCHAR(100) NOT NULL,
                       email         VARCHAR(100),
                       phone_no      VARCHAR(20),
                       profile_pic   VARCHAR(255),
                       role          ENUM('ADMIN','LECTURER','TECH_OFFICER','UNDERGRADUATE') NOT NULL,
                       is_active     BOOLEAN DEFAULT TRUE,
                       failed_logins INT DEFAULT 0
);

CREATE TABLE lecturer (
                          lecturer_id   INT AUTO_INCREMENT PRIMARY KEY,
                          user_id       INT NOT NULL UNIQUE,
                          department_id INT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(user_id),
                          FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE undergraduate (
                               student_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id    INT NOT NULL UNIQUE,
                               reg_no     VARCHAR(20) NOT NULL UNIQUE,
                               batch_year INT NOT NULL,
                               status     ENUM('ACTIVE','REPEAT','BATCH_MISSED') DEFAULT 'ACTIVE',
                               contact_no VARCHAR(20),
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE course (
                        course_code      VARCHAR(10) PRIMARY KEY,
                        course_title     VARCHAR(150) NOT NULL,
                        credit_theory    TINYINT NOT NULL DEFAULT 0,
                        credit_practical TINYINT NOT NULL DEFAULT 0,
                        department_id    INT NOT NULL,
                        lecturer_id      INT,
                        FOREIGN KEY (department_id) REFERENCES department(department_id),
                        FOREIGN KEY (lecturer_id) REFERENCES lecturer(lecturer_id)
);

CREATE TABLE enrollment (
                            enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                            student_id    INT NOT NULL,
                            course_code   VARCHAR(10) NOT NULL,
                            academic_year INT NOT NULL,
                            semester      TINYINT NOT NULL,
                            UNIQUE (student_id, course_code, academic_year),
                            FOREIGN KEY (student_id) REFERENCES undergraduate(student_id),
                            FOREIGN KEY (course_code) REFERENCES course(course_code)
);

-- ===== ඔයාගේ කොටස (Marks/Grading) =====

CREATE TABLE assessment (
                            assessment_id  INT AUTO_INCREMENT PRIMARY KEY,
                            course_code    VARCHAR(10) NOT NULL,
                            component_name VARCHAR(50) NOT NULL,
                            component_type ENUM('CA','FINAL') NOT NULL,
                            weight         DECIMAL(5,2) NOT NULL,
                            FOREIGN KEY (course_code) REFERENCES course(course_code)
);

CREATE TABLE mark (
                      mark_id       INT AUTO_INCREMENT PRIMARY KEY,
                      enrollment_id INT NOT NULL,
                      assessment_id INT NOT NULL,
                      marks         DECIMAL(5,2) NOT NULL CHECK (marks BETWEEN 0 AND 100),
                      UNIQUE (enrollment_id, assessment_id),
                      FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id),
                      FOREIGN KEY (assessment_id) REFERENCES assessment(assessment_id)
);

CREATE TABLE result (
                        result_id          INT AUTO_INCREMENT PRIMARY KEY,
                        enrollment_id      INT NOT NULL UNIQUE,
                        ca_mark            DECIMAL(5,2),
                        final_exam_mark    DECIMAL(5,2),
                        total_mark         DECIMAL(5,2),
                        eligibility_status ENUM('ELIGIBLE','NOT_ELIGIBLE'),
                        grade              VARCHAR(3),
                        grade_point        DECIMAL(2,1),
                        FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id)
);