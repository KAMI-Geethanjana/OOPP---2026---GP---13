mysql> DROP DATABASE IF EXISTS ftms_db;
Query OK, 9 rows affected (0.23 sec)

mysql> -- 1. Create and Use Database
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE DATABASE ftms_db;
Query OK, 1 row affected (0.01 sec)

mysql> USE ftms_db;
Database changed
mysql>
mysql> -- 2. Users Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE users (
    ->     user_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     username VARCHAR(50) UNIQUE NOT NULL,
    ->     password_hash VARCHAR(255) NOT NULL,
    ->     full_name VARCHAR(100) NOT NULL,
    ->     email VARCHAR(100),
    ->     phone_no VARCHAR(20),
    ->     profile_pic VARCHAR(255),
    ->     role ENUM('ADMIN', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE') NOT NULL,
    ->     is_active TINYINT(1) DEFAULT 1,
    ->     failed_logins INT DEFAULT 0
    -> );
Query OK, 0 rows affected, 1 warning (0.04 sec)

mysql>
mysql> -- 3. Department Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE department (
    ->     department_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     department_name VARCHAR(100) NOT NULL,
    ->     department_code VARCHAR(10) UNIQUE NOT NULL
    -> );
Query OK, 0 rows affected (0.04 sec)

mysql>
mysql> -- 4. Lecturer Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE lecturer (
    ->     lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     user_id INT UNIQUE NOT NULL,
    ->     employee_id VARCHAR(20) UNIQUE NOT NULL,
    ->     department_id INT NOT NULL,
    ->     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    ->     FOREIGN KEY (department_id) REFERENCES department(department_id)
    -> );
Query OK, 0 rows affected (0.04 sec)

mysql>
mysql> -- 5. Undergraduate Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE undergraduate (
    ->     student_id VARCHAR(20) PRIMARY KEY, -- e.g., TG/2024/2001
    ->     user_id INT UNIQUE NOT NULL,
    ->     department_id INT NOT NULL,
    ->     batch VARCHAR(10) NOT NULL,
    ->     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    ->     FOREIGN KEY (department_id) REFERENCES department(department_id)
    -> );
Query OK, 0 rows affected (0.04 sec)

mysql>
mysql> -- 6. Course Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE course (
    ->     course_code VARCHAR(10) PRIMARY KEY,
    ->     course_title VARCHAR(150) NOT NULL,
    ->     credit_theory TINYINT NOT NULL DEFAULT 0,
    ->     credit_practical TINYINT NOT NULL DEFAULT 0,
    ->     department_id INT NOT NULL,
    ->     lecturer_id INT,
    ->     FOREIGN KEY (department_id) REFERENCES department(department_id),
    ->     FOREIGN KEY (lecturer_id) REFERENCES lecturer(lecturer_id) ON DELETE SET NULL
    -> );
Query OK, 0 rows affected (0.03 sec)

mysql>
mysql> -- 7. Enrollment Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE enrollment (
    ->     enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     student_id VARCHAR(20) NOT NULL,
    ->     course_code VARCHAR(10) NOT NULL,
    ->     semester INT NOT NULL,
    ->     FOREIGN KEY (student_id) REFERENCES undergraduate(student_id) ON DELETE CASCADE,
    ->     FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE,
    ->     UNIQUE KEY unique_enrollment (student_id, course_code)
    -> );
Query OK, 0 rows affected (0.03 sec)

mysql>
mysql> -- 8. Assessment Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE assessment (
    ->     assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     course_code VARCHAR(10) NOT NULL,
    ->     component_name VARCHAR(50) NOT NULL,
    ->     component_type ENUM('CA', 'FINAL') NOT NULL,
    ->     weight DECIMAL(5,2) NOT NULL,
    ->     FOREIGN KEY (course_code) REFERENCES course(course_code) ON DELETE CASCADE
    -> );
Query OK, 0 rows affected (0.03 sec)

mysql>
mysql> -- 9. Mark Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE mark (
    ->     mark_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     enrollment_id INT NOT NULL,
    ->     assessment_id INT NOT NULL,
    ->     marks DECIMAL(5,2) NOT NULL,
    ->     FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id) ON DELETE CASCADE,
    ->     FOREIGN KEY (assessment_id) REFERENCES assessment(assessment_id) ON DELETE CASCADE,
    ->     UNIQUE KEY unique_mark (enrollment_id, assessment_id)
    -> );
Query OK, 0 rows affected (0.03 sec)

mysql>
mysql> -- 10. Result Table
Query OK, 0 rows affected (0.00 sec)

mysql> CREATE TABLE result (
    ->     result_id INT AUTO_INCREMENT PRIMARY KEY,
    ->     enrollment_id INT UNIQUE NOT NULL,
    ->     ca_mark DECIMAL(5,2),
    ->     final_exam_mark DECIMAL(5,2),
    ->     total_mark DECIMAL(5,2),
    ->     eligibility_status ENUM('ELIGIBLE', 'NOT_ELIGIBLE') DEFAULT 'NOT_ELIGIBLE',
    ->     grade VARCHAR(3),
    ->     grade_point DECIMAL(3,2),
    ->     FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id) ON DELETE CASCADE
    -> );
Query OK, 0 rows affected (0.03 sec)

mysql>
mysql> -- =========================================================
Query OK, 0 rows affected (0.00 sec)

mysql> -- SEED DATA & TEST SCENARIOS INSERTION
Query OK, 0 rows affected (0.00 sec)

mysql> -- =========================================================
Query OK, 0 rows affected (0.00 sec)

mysql>
mysql> -- Insert Department
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO department (department_id, department_name, department_code) VALUES
    -> (1, 'Department of Information and Communication Technology', 'DICT');
Query OK, 1 row affected (0.00 sec)

mysql>
mysql> -- Insert Users
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO users (user_id, username, password_hash, full_name, email, role) VALUES
    -> (1, 'admin', 'hashed_pass_1', 'System Admin', 'admin@fot.ruh.ac.lk', 'ADMIN'),
    -> (2, 'lecturer1', 'hashed_pass_2', 'Dr. Perera', 'perera@fot.ruh.ac.lk', 'LECTURER'),
    -> (3, 'tg2001', 'hashed_pass_3', 'Kamal Silva', 'kamal@gmail.com', 'UNDERGRADUATE'),
    -> (4, 'tg2002', 'hashed_pass_4', 'Nimali Fernando', 'nimali@gmail.com', 'UNDERGRADUATE'),
    -> (5, 'tg2003', 'hashed_pass_5', 'Sahan Jayasinghe', 'sahan@gmail.com', 'UNDERGRADUATE'),
    -> (6, 'tg2004', 'hashed_pass_6', 'Nuwan Pradeep', 'nuwan@gmail.com', 'UNDERGRADUATE');
Query OK, 6 rows affected (0.00 sec)
Records: 6  Duplicates: 0  Warnings: 0

mysql>
mysql> -- Insert Lecturer
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO lecturer (lecturer_id, user_id, employee_id, department_id) VALUES
    -> (1, 2, 'EMP/1001', 1);
Query OK, 1 row affected (0.00 sec)

mysql>
mysql> -- Insert Students (Matching User Accounts)
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO undergraduate (student_id, user_id, department_id, batch) VALUES
    -> ('TG/2024/2001', 3, 1, '2023/2024'),
    -> ('TG/2024/2002', 4, 1, '2023/2024'),
    -> ('TG/2024/2003', 5, 1, '2023/2024'),
    -> ('TG/2024/2004', 6, 1, '2023/2024');
Query OK, 4 rows affected (0.00 sec)
Records: 4  Duplicates: 0  Warnings: 0

mysql>
mysql> -- Insert Course
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) VALUES
    -> ('ICT2132', 'Object Oriented Programming Practicum', 0, 2, 1, 1);
Query OK, 1 row affected (0.00 sec)

mysql>
mysql> -- Insert Enrollments (Gets IDs 1, 2, 3, 4)
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO enrollment (enrollment_id, student_id, course_code, semester) VALUES
    -> (1, 'TG/2024/2001', 'ICT2132', 1),
    -> (2, 'TG/2024/2002', 'ICT2132', 1),
    -> (3, 'TG/2024/2003', 'ICT2132', 1),
    -> (4, 'TG/2024/2004', 'ICT2132', 1);
Query OK, 4 rows affected (0.00 sec)
Records: 4  Duplicates: 0  Warnings: 0

mysql>
mysql> -- Insert Assessment Structure
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO assessment (assessment_id, course_code, component_name, component_type, weight) VALUES
    -> (1, 'ICT2132', 'CA Aggregate', 'CA', 40.00),
    -> (2, 'ICT2132', 'Final Exam', 'FINAL', 60.00);
Query OK, 2 rows affected (0.00 sec)
Records: 2  Duplicates: 0  Warnings: 0

mysql>
mysql> -- Insert Test Marks for Scenarios
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO mark (enrollment_id, assessment_id, marks) VALUES
    -> (1, 1, 75.00), (1, 2, 80.00), -- Student 1
    -> (2, 1, 40.00), (2, 2, 50.00), -- Student 2
    -> (3, 1, 25.00),                 -- Student 3 (No final exam mark)
    -> (4, 1, 70.00);                 -- Student 4 (Pending final exam)
Query OK, 6 rows affected (0.00 sec)
Records: 6  Duplicates: 0  Warnings: 0

mysql>
mysql> -- Insert Computed Results for Scenarios 4
Query OK, 0 rows affected (0.00 sec)

mysql> INSERT INTO result (enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point) VALUES
    -> (1, 75.00, 80.00, 78.00, 'ELIGIBLE',     'A-', 3.70), -- Scenario 1: Pass
    -> (2, 40.00, 50.00, 46.00, 'ELIGIBLE',     'C-', 1.70), -- Scenario 2: Boundary Pass
    -> (3, 25.00, NULL,  NULL,  'NOT_ELIGIBLE', NULL, NULL), -- Scenario 3: Fail CA
    -> (4, 70.00, NULL,  NULL,  'ELIGIBLE',     NULL, NULL); -- Scenario 4: Pending Final Exam
Query OK, 4 rows affected (0.01 sec)
Records: 4  Duplicates: 0  Warnings: 0

mysql> SELECT e.student_id, r.ca_mark, r.final_exam_mark, r.total_mark, r.eligibility_status, r.grade, r.grade_point
                                                                                                                                                                       -> FROM result r
                                                                       -> JOIN enrollment e ON r.enrollment_id = e.enrollment_id;
+--------------+---------+-----------------+------------+--------------------+-------+-------------+
| student_id   | ca_mark | final_exam_mark | total_mark | eligibility_status | grade | grade_point |
+--------------+---------+-----------------+------------+--------------------+-------+-------------+
| TG/2024/2001 |   75.00 |           80.00 |      78.00 | ELIGIBLE           | A-    |        3.70 |
| TG/2024/2002 |   40.00 |           50.00 |      46.00 | ELIGIBLE           | C-    |        1.70 |
| TG/2024/2003 |   25.00 |            NULL |       NULL | NOT_ELIGIBLE       | NULL  |        NULL |
| TG/2024/2004 |   70.00 |            NULL |       NULL | ELIGIBLE           | NULL  |        NULL |
+--------------+---------+-----------------+------------+--------------------+-------+-------------+
4 rows in set (0.00 sec)

mysql>