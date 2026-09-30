USE ftms_db;

-- 1. Test Course එකක් එකතු කිරීම (TST1001)
INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id)
VALUES ('TST1001', 'Test Module for Marks', 2, 0, 1, 1)
    ON DUPLICATE KEY UPDATE course_title = VALUES(course_title);

-- 2. Test Students 4 දෙනෙක් ඇතුළත් කිරීම
INSERT INTO users (user_id, username, password_hash, full_name, email, role) VALUES
                                                                                 (101, 'test_stu1', 'pass123', 'Test Student 1 (Pass)', 't1@student.ruh.ac.lk', 'UNDERGRADUATE'),
                                                                                 (102, 'test_stu2', 'pass123', 'Test Student 2 (Boundary)', 't2@student.ruh.ac.lk', 'UNDERGRADUATE'),
                                                                                 (103, 'test_stu3', 'pass123', 'Test Student 3 (Fail CA)', 't3@student.ruh.ac.lk', 'UNDERGRADUATE'),
                                                                                 (104, 'test_stu4', 'pass123', 'Test Student 4 (Pending Final)', 't4@student.ruh.ac.lk', 'UNDERGRADUATE')
    ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

INSERT INTO undergraduate (student_id, user_id, reg_no, batch_year, status) VALUES
                                                                                (101, 101, 'TG/2022/TEST01', 2022, 'ACTIVE'),
                                                                                (102, 102, 'TG/2022/TEST02', 2022, 'ACTIVE'),
                                                                                (103, 103, 'TG/2022/TEST03', 2022, 'ACTIVE'),
                                                                                (104, 104, 'TG/2022/TEST04', 2022, 'ACTIVE')
    ON DUPLICATE KEY UPDATE reg_no = VALUES(reg_no);

-- 3. Enrollments (ID 101, 102, 103, 104)
INSERT INTO enrollment (enrollment_id, student_id, course_code, academic_year, semester) VALUES
                                                                                             (101, 101, 'TST1001', 2026, 1),
                                                                                             (102, 102, 'TST1001', 2026, 1),
                                                                                             (103, 103, 'TST1001', 2026, 1),
                                                                                             (104, 104, 'TST1001', 2026, 1)
    ON DUPLICATE KEY UPDATE course_code = VALUES(course_code);

-- 4. Assessment Components (CA1: 20%, CA2: 20%, Final: 60%)
INSERT INTO assessment (assessment_id, course_code, component_name, component_type, weight) VALUES
                                                                                                (101, 'TST1001', 'CA Assignment 1', 'CA', 20.00),
                                                                                                (102, 'TST1001', 'CA Mid Exam', 'CA', 20.00),
                                                                                                (103, 'TST1001', 'Final Theory Exam', 'FINAL', 60.00)
    ON DUPLICATE KEY UPDATE weight = VALUES(weight);

-- 5. Marks ඇතුළත් කිරීම (Pass, Boundary, Fail, Pending Scenarios සඳහා)
INSERT INTO mark (enrollment_id, assessment_id, marks) VALUES
-- Student 1: Pass (CA % = 75%, Final = 75%)
(101, 101, 70.00), (101, 102, 80.00), (101, 103, 75.00),

-- Student 2: Boundary Pass (CA % = 40%, Final = 50%)
(102, 101, 40.00), (102, 102, 40.00), (102, 103, 50.00),

-- Student 3: Fail CA (CA % = 25% < 40%, Final = 80%)
(103, 101, 20.00), (103, 102, 30.00), (103, 103, 80.00),

-- Student 4: Pending Final (CA % = 55%, Final Mark නෑ)
(104, 101, 50.00), (104, 102, 60.00)
    ON DUPLICATE KEY UPDATE marks = VALUES(marks);