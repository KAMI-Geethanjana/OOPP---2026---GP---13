# Module TG2088 - Lecturer Module & Marks/Grading Business Rules
**Developer:** T.H.D. Wosada (Registration No: TG/2024/2088)  
**Degree:** BICT (Hons), Faculty of Technology, University of Ruhuna  
**Course Unit:** ICT2132 - Object Oriented Programming Practicum  

---

## 1. Overview of My Module
As per the SRS and Project Specification, my primary responsibility is the **Lecturer Module** along with the **Academic Grading and Eligibility Business Rules Engine**:
1. **Marks & Grading Management (FR-LEC-02, FR-MRK-03, FR-MRK-04):**
   - Upload and update Continuous Assessment (CA) and End-of-Semester Exam marks.
   - Calculate total weighted marks (40% CA + 60% Final Exam).
   - Compute letter grades according to **UGC Commission Circular No. 12-2024**.
   - Grade capping: Repeat attempts are automatically capped to maximum Grade 'C'.
2. **CA & Exam Eligibility Checking (FR-LEC-04, FR-MRK-01, FR-MRK-02):**
   - Check if CA mark is $\ge 40\%$. If not, student is marked **NOT_ELIGIBLE** and awarded grade **E**.
   - Check overall exam eligibility by combining the **80% attendance rule** and **CA $\ge 40\%$ rule**.
3. **Course Materials Repository (FR-LEC-01):**
   - Lecturers can upload lecture slides, lab manuals, and notes for their enrolled courses.
4. **Student Attendance & Medical Records Monitoring (FR-LEC-06):**
   - Lecturers can view the attendance percentages (Theory, Practical, Combined) and approved medical excuses of enrolled students.
5. **Lecturer Profile Management (FR-AUTH-03):**
   - Lecturers can update their name, email, and contact number.

---

## 2. OOP Concepts Demonstrated in My Code

| OOP Concept | How I Implemented It in My Module |
|---|---|
| **Classes & Objects** | Created `Lecturer`, `CourseMaterial`, `Result`, and `GradeCalculator` classes with runtime objects representing lecturers, course materials, and student marks. |
| **Inheritance** | `Lecturer extends User` (inherits user ID, username, password, email, full name, phone number). |
| **Polymorphism** | Overrode `getRoleDisplayName()` to return `"Lecturer"` and `openDashboard(JFrame)` to polymorphically launch the `LecturerDashboard`. |
| **Encapsulation** | All fields in `Result` and `Lecturer` are `private`. In `Result.setCaMark()` and `setFinalExamMark()`, setter validation ensures marks are strictly between 0 and 100. |
| **Exception Handling** | Created custom exceptions `InvalidMarksException` and `IneligibleStudentException`. Handled SQL and NumberFormat exceptions with user-friendly dialogs. |
| **Database Handling** | Encapsulated all database persistence inside `LecturerDAO` using JDBC `PreparedStatement` with transactions. |
| **GUI** | Designed a modern Java Swing desktop interface with custom card layouts, dark sidebar navigation, and interactive table editing. |

---

## 3. Key Files in My Package (`tg2088`)
- `tg2088/model/Lecturer.java`: Subclass extending `User`.
- `tg2088/model/CourseMaterial.java`: Entity for lecture materials.
- `tg2088/dao/LecturerDAO.java`: Isolated SQL queries for lecturer operations.
- `tg2088/gui/LecturerDashboard.java`: Complete desktop GUI interface.
- `common/util/GradeCalculator.java`: Core grading rules engine (UGC Circular 12-2024).
