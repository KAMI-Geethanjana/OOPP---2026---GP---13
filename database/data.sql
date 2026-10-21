USE ftms_db;

-- 1. Departments
INSERT INTO department (department_id, department_name, department_code) VALUES
                                                                             (1, 'Department of Information and Communication Technology', 'DICT'),
                                                                             (2, 'Department of Multidisciplinary Studies', 'DMS'),
                                                                             (3, 'Department of Engineering Technology', 'DET'),
                                                                             (4, 'Department of Biosystems Technology', 'BST');

-- 2. Users

INSERT INTO users (user_id, username, password_hash, full_name, email, phone_no, role) VALUES
-- Admin
(1, 'admin', '1234', 'K.A.M.I. Geethanjana (Admin)', 'admin@fot.ruh.ac.lk', '0711112233', 'ADMIN'),

-- 5 Lecturers
(2, 'wosada', '1234', 'Mr. T.H.D. Wosada', 'wosada@fot.ruh.ac.lk', '0772223344', 'LECTURER'),
(3, 'kamal', '1234', 'Dr. Kamal Perera', 'kamal@fot.ruh.ac.lk', '0773334455', 'LECTURER'),
(4, 'nimal', '1234', 'Prof. Nimal Silva', 'nimal@fot.ruh.ac.lk', '0774445566', 'LECTURER'),
(5, 'anura', '1234', 'Dr. Anura Fernando', 'anura@fot.ruh.ac.lk', '0775556677', 'LECTURER'),
(6, 'chamari', '1234', 'Mrs. Chamari Jayasinghe', 'chamari@fot.ruh.ac.lk', '0776667788', 'LECTURER'),

-- 4 Technical Officers
(7, 'to_weera', '1234', 'Mr. B.G.N.M. Weerawardhana', 'weera@fot.ruh.ac.lk', '0714445566', 'TECH_OFFICER'),
(8, 'to_saman', '1234', 'Mr. Saman Kumara', 'saman@fot.ruh.ac.lk', '0715556677', 'TECH_OFFICER'),
(9, 'to_sunil', '1234', 'Mr. Sunil Shantha', 'sunil@fot.ruh.ac.lk', '0716667788', 'TECH_OFFICER'),
(10, 'to_ruwan', '1234', 'Mr. Ruwan Gamage', 'ruwan@fot.ruh.ac.lk', '0717778899', 'TECH_OFFICER'),

-- 20 Undergraduates
(11, 'tg2082', '1234', 'W.D.P. Kaveesha', 'kaveesha@student.ruh.ac.lk', '0781112201', 'UNDERGRADUATE'),
(12, 'tg2088', '1234', 'T.H.D. Wosada', 'wosada@student.ruh.ac.lk', '0781112202', 'UNDERGRADUATE'),
(13, 'tg2086', '1234', 'K.A.M.I. Geethanjana', 'geethanjana@student.ruh.ac.lk', '0781112203', 'UNDERGRADUATE'),
(14, 'tg2123', '1234', 'B.G.N.M. Weerawardhana', 'weerawardhana@student.ruh.ac.lk', '0781112204', 'UNDERGRADUATE'),
(15, 'tg2001', '1234', 'Kasun Bandara', 'kasun@student.ruh.ac.lk', '0781112205', 'UNDERGRADUATE'),
(16, 'tg2002', '1234', 'Nimali Fernando', 'nimali@student.ruh.ac.lk', '0781112206', 'UNDERGRADUATE'),
(17, 'tg2003', '1234', 'Chathura Perera', 'chathura@student.ruh.ac.lk', '0781112207', 'UNDERGRADUATE'),
(18, 'tg2004', '1234', 'Dinithi Jayawardena', 'dinithi@student.ruh.ac.lk', '0781112208', 'UNDERGRADUATE'),
(19, 'tg2005', '1234', 'Sahan Senanayake', 'sahan@student.ruh.ac.lk', '0781112209', 'UNDERGRADUATE'),
(20, 'tg2006', '1234', 'Dilki Wijesinghe', 'dilki@student.ruh.ac.lk', '0781112210', 'UNDERGRADUATE'),
(21, 'tg2007', '1234', 'Tharindu Madushanka', 'tharindu@student.ruh.ac.lk', '0781112211', 'UNDERGRADUATE'),
(22, 'tg2008', '1234', 'Sachini Wickramasinghe', 'sachini@student.ruh.ac.lk', '0781112212', 'UNDERGRADUATE'),
(23, 'tg2009', '1234', 'Hasitha Rathnayake', 'hasitha@student.ruh.ac.lk', '0781112213', 'UNDERGRADUATE'),
(24, 'tg2010', '1234', 'Anuki De Silva', 'anuki@student.ruh.ac.lk', '0781112214', 'UNDERGRADUATE'),
(25, 'tg2011', '1234', 'Kavindu Gimhana', 'kavindu@student.ruh.ac.lk', '0781112215', 'UNDERGRADUATE'),
(26, 'tg2012', '1234', 'Sanduni Piyumika', 'sanduni@student.ruh.ac.lk', '0781112216', 'UNDERGRADUATE'),
(27, 'tg2013', '1234', 'Lahiru Dananjaya', 'lahiru@student.ruh.ac.lk', '0781112217', 'UNDERGRADUATE'),
(28, 'tg2014', '1234', 'Pooja Rasanjali', 'pooja@student.ruh.ac.lk', '0781112218', 'UNDERGRADUATE'),
(29, 'tg1950', '1234', 'Malith Prabath (Repeat)', 'malith@student.ruh.ac.lk', '0781112219', 'UNDERGRADUATE'),
(30, 'tg1955', '1234', 'Keshara Dilshan (Batch-Missed)', 'keshara@student.ruh.ac.lk', '0781112220', 'UNDERGRADUATE');

-- 3. Admin Details
INSERT INTO admin (admin_id, user_id) VALUES (1, 1);

-- 4. Lecturer Details
INSERT INTO lecturer (lecturer_id, user_id, employee_id, department_id) VALUES
                                                                            (1, 2, 'EMP/LEC/1001', 1), -- T.H.D. Wosada
                                                                            (2, 3, 'EMP/LEC/1002', 1), -- Dr. Kamal Perera
                                                                            (3, 4, 'EMP/LEC/1003', 1), -- Prof. Nimal Silva
                                                                            (4, 5, 'EMP/LEC/1004', 2), -- Dr. Anura Fernando
                                                                            (5, 6, 'EMP/LEC/1005', 1); -- Mrs. Chamari Jayasinghe

-- 5. Technical Officer Details
INSERT INTO technical_officer (to_id, user_id, employee_id, department_id) VALUES
                                                                               (1, 7, 'EMP/TO/2001', 1), -- B.G.N.M. Weerawardhana
                                                                               (2, 8, 'EMP/TO/2002', 1), -- Mr. Saman Kumara
                                                                               (3, 9, 'EMP/TO/2003', 1), -- Mr. Sunil Shantha
                                                                               (4, 10, 'EMP/TO/2004', 2); -- Mr. Ruwan Gamage

-- 6. Undergraduate Details
INSERT INTO undergraduate (student_id, user_id, reg_no, batch_year, status, department_id, contact_no) VALUES
                                                                                                           (1, 11, 'TG/2024/2082', 2024, 'ACTIVE', 1, '0781112201'),
                                                                                                           (2, 12, 'TG/2024/2088', 2024, 'ACTIVE', 1, '0781112202'),
                                                                                                           (3, 13, 'TG/2024/2086', 2024, 'ACTIVE', 1, '0781112203'),
                                                                                                           (4, 14, 'TG/2024/2123', 2024, 'ACTIVE', 1, '0781112204'),
                                                                                                           (5, 15, 'TG/2024/2001', 2024, 'ACTIVE', 1, '0781112205'),
                                                                                                           (6, 16, 'TG/2024/2002', 2024, 'ACTIVE', 1, '0781112206'),
                                                                                                           (7, 17, 'TG/2024/2003', 2024, 'ACTIVE', 1, '0781112207'),
                                                                                                           (8, 18, 'TG/2024/2004', 2024, 'ACTIVE', 1, '0781112208'),
                                                                                                           (9, 19, 'TG/2024/2005', 2024, 'ACTIVE', 1, '0781112209'),
                                                                                                           (10, 20, 'TG/2024/2006', 2024, 'ACTIVE', 1, '0781112210'),
                                                                                                           (11, 21, 'TG/2024/2007', 2024, 'ACTIVE', 1, '0781112211'),
                                                                                                           (12, 22, 'TG/2024/2008', 2024, 'ACTIVE', 1, '0781112212'),
                                                                                                           (13, 23, 'TG/2024/2009', 2024, 'ACTIVE', 1, '0781112213'),
                                                                                                           (14, 24, 'TG/2024/2010', 2024, 'ACTIVE', 1, '0781112214'),
                                                                                                           (15, 25, 'TG/2024/2011', 2024, 'ACTIVE', 1, '0781112215'),
                                                                                                           (16, 26, 'TG/2024/2012', 2024, 'ACTIVE', 1, '0781112216'),
                                                                                                           (17, 27, 'TG/2024/2013', 2024, 'ACTIVE', 1, '0781112217'),
                                                                                                           (18, 28, 'TG/2024/2014', 2024, 'ACTIVE', 1, '0781112218'),
                                                                                                           (19, 29, 'TG/2023/1950', 2023, 'REPEAT', 1, '0781112219'),
                                                                                                           (20, 30, 'TG/2023/1955', 2023, 'BATCH_MISSED', 1, '0781112220');

-- 7. Courses
INSERT INTO course
    (course_code, course_title, credit_theory, credit_practical,
     department_id, lecturer_id)
VALUES
    ('ICT2113', 'Data Structures and Algorithms', 3, 0, 1, 2),
    ('ICT2122', 'Object Oriented Programming', 2, 0, 1, 1),
    ('ICT2132', 'Object Oriented Programming Practicum', 0, 2, 1, 1),
    ('ICT2142', 'E-Business Systems', 2, 0, 1, 5),
    ('ICT2152', 'Object Oriented Analysis and Design', 2, 0, 1, 3),
    ('ENG2112', 'English III', 2, 0, 2, 4),
    ('TCS2112', 'Business Economics', 2, 0, 2, 4),
    ('TCS2121', 'Soft Skills', 2, 0, 2, 4),
    ('ICT2162', 'Management Information Systems', 2, 0, 2, 4);
    
-- 8. Course Materials
INSERT INTO course_material
    (material_id, course_code, lecturer_id, title, file_path, uploaded_date)
VALUES
    (1, 'ICT2122', 1, 'Lecture 01 - Intro to OOP & Classes',
     'materials/ICT2122_Lec01.pdf', '2026-02-10'),

    (2, 'ICT2122', 1, 'Lecture 02 - Inheritance & Polymorphism',
     'materials/ICT2122_Lec02.pdf', '2026-02-17'),

    (3, 'ICT2122', 1, 'Lecture 03 - Abstraction & Interfaces',
     'materials/ICT2122_Lec03.pdf', '2026-02-24'),

    (4, 'ICT2132', 1, 'Lab 01 - Java Swing GUI Development',
     'materials/ICT2132_Lab01.pdf', '2026-03-01'),

    (5, 'ICT2132', 1, 'Lab 02 - JDBC & Database Connectivity',
     'materials/ICT2132_Lab02.pdf', '2026-03-08'),

    (6, 'ICT2113', 2, 'DSA Chapter 1 - Algorithmic Complexity',
     'materials/ICT2113_Ch01.pdf', '2026-02-12');


-- 9. Timetable
INSERT INTO timetable
    (timetable_id, department_id, course_code, day_of_week,
     start_time, end_time, venue, session_type)
VALUES
    -- Monday
    (1, 1, 'ICT2122', 'Monday',
     '08:30:00', '10:30:00', 'Hall C', 'THEORY'),

    (2, 1, 'ICT2132', 'Monday',
     '10:30:00', '12:30:00', 'Computer Lab 01', 'PRACTICAL'),

    -- Tuesday
    (3, 1, 'ICT2113', 'Tuesday',
     '08:30:00', '11:30:00', 'Hall B', 'THEORY'),

    -- Wednesday
    (4, 1, 'ICT2142', 'Wednesday',
     '08:30:00', '10:30:00', 'Hall A', 'THEORY'),

    (5, 2, 'TCS2121', 'Wednesday',
     '10:00:00', '12:00:00', 'ICT Hardware Lab', 'PRACTICAL'),

    -- Thursday
    (6, 1, 'ICT2152', 'Thursday',
     '10:30:00', '12:30:00', 'Hall C', 'THEORY'),

    -- Friday
    (7, 2, 'ENG2112', 'Friday',
     '08:30:00', '10:30:00', 'Language Lab', 'THEORY'),

    (8, 2, 'ICT2162', 'Friday',
     '10:00:00', '12:00:00', 'ICT New Lab', 'THEORY'),

    (9, 2, 'TCS2112', 'Friday',
     '13:30:00', '15:30:00', 'Seminar Room 1', 'THEORY');

-- 10. Notices
INSERT INTO notice (notice_id, title, content, target_audience, created_date, posted_by) VALUES
                                                                                             (1, 'Mid-Semester Examination Schedule', 'The mid-semester examinations for Level II Semester I will commence on 20th October 2026. Please check your timetables.', 'ALL', '2026-10-01', 'Admin Office'),
                                                                                             (2, 'Submission of Medical Certificates', 'All undergraduates who missed academic sessions due to medical reasons must submit their certificates to Technical Officers within 7 days.', 'UNDERGRADUATE', '2026-10-03', 'Technical Officer Unit'),
                                                                                             (3, 'CA Marks Upload Deadline', 'All lecturers are kindly requested to complete the entry of Continuous Assessment (CA) marks on or before 25th October 2026.', 'LECTURER', '2026-10-05', 'Dean Office'),
                                                                                             (4, 'Lab Maintenance Notice', 'Computer Lab 01 and 02 will be closed this Saturday for scheduled network maintenance.', 'TECH_OFFICER', '2026-10-07', 'IT Unit');

-- 11. Enrollments
INSERT INTO enrollment (enrollment_id, student_id, course_code, academic_year, semester, attempt_type) VALUES
-- Student 1 (Kaveesha TG2082)
(1, 1, 'ICT2122', 2026, 1, 'PROPER'),
(2, 1, 'ICT2132', 2026, 1, 'PROPER'),
-- Student 2 (Wosada TG2088)
(3, 2, 'ICT2122', 2026, 1, 'PROPER'),
(4, 2, 'ICT2132', 2026, 1, 'PROPER'),
-- Student 3 (Geethanjana TG2086)
(5, 3, 'ICT2122', 2026, 1, 'PROPER'),
(6, 3, 'ICT2132', 2026, 1, 'PROPER'),
-- Student 4 (Weerawardhana TG2123)
(7, 4, 'ICT2122', 2026, 1, 'PROPER'),
(8, 4, 'ICT2132', 2026, 1, 'PROPER'),
-- Student 5 (Kasun TG2001 - Scenario B: Exactly 80%)
(9, 5, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 6 (Nimali TG2002 - Scenario C: < 80% without medicals)
(10, 6, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 7 (Chathura TG2003 - Scenario D: > 80% with medicals)
(11, 7, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 8 (Dinithi TG2004 - Scenario E: < 80% with medicals)
(12, 8, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 9 (Sahan TG2005)
(13, 9, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 10 (Dilki TG2006)
(14, 10, 'ICT2122', 2026, 1, 'PROPER'),
-- Student 19 (Malith TG1950 - Repeat Student!)
(15, 19, 'ICT2122', 2026, 1, 'REPEAT'),
-- Student 20 (Keshara TG1955 - Batch Missed!)
(16, 20, 'ICT2122', 2026, 1, 'BATCH_MISSED');

-- 12. Attendance Records
INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (2, 'ICT2122', '2026-02-02', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-02-09', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-02-16', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-02-23', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-03-02', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-03-09', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-03-16', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-03-23', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-03-30', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-04-06', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-04-13', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-04-20', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-04-27', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-05-04', 'THEORY', 'PRESENT', 1),
                                                                                                (2, 'ICT2122', '2026-05-11', 'THEORY', 'ABSENT',  1);


INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (5, 'ICT2122', '2026-02-02', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-02-09', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-02-16', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-02-23', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-03-02', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-03-09', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-03-16', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-03-23', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-03-30', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-04-06', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-04-13', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-04-20', 'THEORY', 'PRESENT', 1),
                                                                                                (5, 'ICT2122', '2026-04-27', 'THEORY', 'ABSENT',  1),
                                                                                                (5, 'ICT2122', '2026-05-04', 'THEORY', 'ABSENT',  1),
                                                                                                (5, 'ICT2122', '2026-05-11', 'THEORY', 'ABSENT',  1);


INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (6, 'ICT2122', '2026-02-02', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-02-09', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-02-16', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-02-23', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-03-02', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-03-09', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-03-16', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-03-23', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-03-30', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-04-06', 'THEORY', 'PRESENT', 1),
                                                                                                (6, 'ICT2122', '2026-04-13', 'THEORY', 'ABSENT',  1),
                                                                                                (6, 'ICT2122', '2026-04-20', 'THEORY', 'ABSENT',  1),
                                                                                                (6, 'ICT2122', '2026-04-27', 'THEORY', 'ABSENT',  1),
                                                                                                (6, 'ICT2122', '2026-05-04', 'THEORY', 'ABSENT',  1),
                                                                                                (6, 'ICT2122', '2026-05-11', 'THEORY', 'ABSENT',  1);


INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (7, 'ICT2122', '2026-02-02', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-02-09', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-02-16', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-02-23', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-03-02', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-03-09', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-03-16', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-03-23', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-03-30', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-04-06', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-04-13', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-04-20', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-04-27', 'THEORY', 'PRESENT', 1),
                                                                                                (7, 'ICT2122', '2026-05-04', 'THEORY', 'ABSENT',  1),
                                                                                                (7, 'ICT2122', '2026-05-11', 'THEORY', 'ABSENT',  1);


INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (8, 'ICT2122', '2026-02-02', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-02-09', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-02-16', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-02-23', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-03-02', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-03-09', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-03-16', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-03-23', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-03-30', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-04-06', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-04-13', 'THEORY', 'PRESENT', 1),
                                                                                                (8, 'ICT2122', '2026-04-20', 'THEORY', 'ABSENT',  1),
                                                                                                (8, 'ICT2122', '2026-04-27', 'THEORY', 'ABSENT',  1),
                                                                                                (8, 'ICT2122', '2026-05-04', 'THEORY', 'ABSENT',  1),
                                                                                                (8, 'ICT2122', '2026-05-11', 'THEORY', 'ABSENT',  1);


INSERT INTO attendance (student_id, course_code, session_date, session_type, status, to_id) VALUES
                                                                                                (1, 'ICT2132', '2026-02-02', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-02-09', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-02-16', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-02-23', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-03-02', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-03-09', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-03-16', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-03-23', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-03-30', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-04-06', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-04-13', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-04-20', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-04-27', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-05-04', 'PRACTICAL', 'PRESENT', 1),
                                                                                                (1, 'ICT2132', '2026-05-11', 'PRACTICAL', 'PRESENT', 1);

-- 13. Medical Records
INSERT INTO medical (medical_id, student_id, course_code, medical_date, reason, document_path, status, reviewed_by_to_id) VALUES
(1, 7, 'ICT2122', '2026-05-04', 'Severe fever and hospital admission', 'docs/med_001.pdf', 'APPROVED', 1),
(2, 8, 'ICT2122', '2026-04-20', 'Viral flu with government doctor certificate', 'docs/med_002.pdf', 'APPROVED', 1),
(3, 8, 'ICT2122', '2026-04-27', 'Follow-up medical rest recommended', 'docs/med_003.pdf', 'APPROVED', 1),
(4, 6, 'ICT2122', '2026-05-04', 'Dental appointment (submitted late)', 'docs/med_004.pdf', 'REJECTED', 1),
(5, 2, 'ICT2122', '2026-05-11', 'Sports injury during Inter-Faculty meet', 'docs/med_005.pdf', 'PENDING', NULL);

-- 14. Marks & Results
INSERT INTO result (result_id, enrollment_id, ca_mark, final_exam_mark, total_mark, eligibility_status, grade, grade_point) VALUES
(1, 1, 85.00, 90.00, 88.00, 'ELIGIBLE', 'A+', 4.00),
(2, 3, 82.00, 86.00, 84.40, 'ELIGIBLE', 'A', 4.00),
(3, 5, 75.00, 78.00, 76.80, 'ELIGIBLE', 'A-', 3.70),
(4, 7, 70.00, 72.00, 71.20, 'ELIGIBLE', 'B+', 3.30),
(5, 9, 64.00, 66.00, 65.20, 'ELIGIBLE', 'B', 3.00),
(6, 10, 32.00, 50.00, 42.80, 'NOT_ELIGIBLE', 'E', 0.00),
(7, 11, 60.00, 62.00, 61.20, 'ELIGIBLE', 'B-', 2.70),
(8, 12, 55.00, 56.00, 55.60, 'ELIGIBLE', 'C+', 2.30),
(9, 13, 50.00, 52.00, 51.20, 'ELIGIBLE', 'C', 2.00),
(10, 14, 46.00, 48.00, 47.20, 'ELIGIBLE', 'C-', 1.70),
(11, 15, 75.00, 80.00, 78.00, 'ELIGIBLE', 'C', 2.00);