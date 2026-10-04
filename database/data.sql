-- 1. Departments Insert 
INSERT INTO department (department_id, department_name) VALUES
            (1, 'Department of Information and Communication Technology'),
            (2, 'Department of Multidisciplinary Studies');

-- 2. Users Insert
INSERT INTO users (user_id, username, password_hash, full_name, email, role) VALUES
             (1, 'admin', 'admin123', 'System Administrator', 'admin@ruh.ac.lk', 'ADMIN'),
             (2, 'lecturer1', 'pass123', 'Dr. Kamal Perera', 'kamal@ict.ruh.ac.lk', 'LECTURER'),
             (3, 'student1', 'pass123', 'Dasola Hennedige', 'dasola@student.ruh.ac.lk', 'UNDERGRADUATE');

-- 3. Lecturer Role Details
INSERT INTO lecturer (lecturer_id, user_id, department_id) VALUES
    (1, 2, 1);

-- 4. Undergraduate Student Role Details
INSERT INTO undergraduate (student_id, user_id, reg_no, batch_year, status) VALUES
    (1, 3, 'TG/2022/1001', 2022, 'ACTIVE');

-- 5. Courses Insert
INSERT INTO course (course_code, course_title, credit_theory, credit_practical, department_id, lecturer_id) VALUES
            ('ENG2112', 'English III', 2, 0, 2, NULL),
            ('ICT2113', 'Data Structures and Algorithms', 3, 0, 1, 1),
            ('ICT2122', 'Object Oriented Programming', 2, 0, 1, 1),
            ('ICT2132', 'Object Oriented Programming Practicum', 0, 2, 1, 1),
            ('ICT2142', 'E-Business Systems', 2, 0, 1, NULL),
            ('ICT2152', 'Object Oriented Analysis and Design', 2, 0, 1, NULL),
            ('ICT2162', 'Management Information Systems', 2, 0, 1, NULL),
            ('TCS2112', 'Business Economics', 2, 0, 2, NULL),
            ('TCS2121', 'Soft Skills', 1, 0, 2, NULL);

-- 6. Enrollment 
INSERT INTO enrollment (enrollment_id, student_id, course_code, academic_year, semester) VALUES
            (1, 1, 'ICT2122', 2026, 1),
            (2, 1, 'ICT2132', 2026, 1);

-- 7. Assessment Criteria
INSERT INTO assessment (assessment_id, course_code, component_name, component_type, weight) VALUES
            (1, 'ICT2122', 'Mid Exam', 'CA', 20.00),
            (2, 'ICT2122', 'Assignment 1', 'CA', 10.00),
            (3, 'ICT2122', 'Final Theory', 'FINAL', 70.00);

-- 8. Sample Marks 
INSERT INTO mark (enrollment_id, assessment_id, marks) VALUES
            (1, 1, 85.50),
            (1, 2, 90.00);


-- 5. Notices (Admin publishes notices)
INSERT INTO notice (notice_id, title, content, target_audience, posted_by) VALUES
    (1, 'Mid-Semester Timetable Released', 'The timetable for Mid-Semester examinations has been uploaded.', 'ALL', 1),
    (2, 'Lab 03 Maintenance', 'Software Lab 02 will be closed for maintenance on Friday.', 'UNDERGRADUATE', 1);

-- 6. Timetable (Admin manages session schedules)
INSERT INTO timetable (timetable_id, course_code, department_id, session_type, day_of_week, start_time, end_time, venue) VALUES
    (1, 'ICT1122', 1, 'THEORY', 'MONDAY', '08:00:00', '10:00:00', 'Lecture Hall 01'),
    (2, 'ICT1122', 1, 'PRACTICAL', 'TUESDAY', '13:00:00', '17:00:00', 'Software Lab 01'),
    (3, 'ICT2132', 1, 'THEORY', 'WEDNESDAY', '10:00:00', '12:00:00', 'Lecture Hall 02');


INSERT INTO users
(user_id, username, password_hash, full_name, email, role)
VALUES
(8, 'to001', '1234', 'Technical Officer One',
 'to001@fot.ruh.ac.lk', 'TECH_OFFICER');


INSERT INTO technical_officer
(technical_officer_id, user_id, employee_id, department_id)
VALUES
(1, 8, 'TO/1001', 1);


