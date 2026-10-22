# Module TG2088 - Lecturer Module & Marks/Grading Business Rules
**Developer:** T.H.D. Wosada (Registration No: TG/2024/2088)  
**Degree:** BICT (Hons), Faculty of Technology, University of Ruhuna  
**Course Units:** ICT2122 (Object Oriented Programming) & ICT2132 (OOP Practicum)  

---

## 1. Overview of My Module
As per the SRS, Project Specification, and Course Syllabus, my primary responsibility is the **Lecturer Module** along with the **Academic Grading and Eligibility Business Rules Engine**:
1. **Marks & Grading Management (FR-LEC-02, FR-MRK-03, FR-MRK-04):**
   - Upload and update Continuous Assessment (CA) and End-of-Semester Exam marks.
   - Calculate total weighted marks (40% CA + 60% Final Exam).
   - Compute letter grades according to **UGC Commission Circular No. 12-2024**.
   - Grade capping: Repeat attempts are automatically capped to maximum Grade 'C'.
2. **CA & Exam Eligibility Checking (FR-LEC-04, FR-MRK-01, FR-MRK-02):**
   - Check if CA mark is $\ge 40\%$. If not, student is marked **NOT_ELIGIBLE** and awarded grade **E**.
   - Check overall exam eligibility by combining the **80% attendance rule** and **CA $\ge 40\%$ rule**.
3. **Course Materials Repository & File Handling (FR-LEC-01):**
   - Lecturers can upload lecture slides and notes for their assigned courses.
   - Character Stream Export: Export grade sheets as formatted `.txt` reports (`PrintWriter`, `BufferedWriter`, `FileWriter`).
   - Binary Stream Backup: Backup course materials to `.dat` files (`DataOutputStream`, `DataInputStream`).
4. **Student Attendance & Medical Records Monitoring (FR-LEC-06):**
   - Lecturers can view student attendance counts, approved medical records, and excused percentages.
5. **Lecturer Profile Management (FR-AUTH-03):**
   - Lecturers can update their full name, email, and contact number.

---

## 2. Complete Mapping to Lecture Notes (Lessons 01 – 07)

| Lesson | Topic in Course Syllabus | Implementation in TG2088 Module |
|---|---|---|
| **Lesson 01 & 02** | Classes & Objects, Anatomy of a Class | `Lecturer.java`, `CourseMaterial.java` modeled with private fields, methods, constructors, and access modifiers (`public`, `private`, `protected`). |
| **Lesson 02 (Part 02)** | `this` Keyword, Constructor Chaining, Initializers | • Differentiating instance variables: `this.lecturerId = lecturerId`<br>• Constructor chaining in same class: `this(...)`<br>• Passing current object reference: `JOptionPane.showMessageDialog(this, ...)`<br>• Instance Initializer Blocks (`{ ... }`) executed before constructors. |
| **Lesson 03 (Part 01)** | Inheritance (`extends`, `super`, `@Override`, casting) | • Single Inheritance: `public class Lecturer extends User`<br>• Subclass constructor explicitly calls `super(...)` as first statement<br>• Method overriding with `@Override`: `getRoleDisplayName()`, `openDashboard(JFrame)`<br>• Type checking using `instanceof` operator. |
| **Lesson 03 (Part 02)** | Polymorphism (Static vs Dynamic Binding) | • **Dynamic Binding / Runtime Polymorphism:** `user.openDashboard(this)` resolves at runtime based on the actual object type.<br>• **Static Polymorphism / Method Overloading:** `getResultsByCourse(code)` and `getResultsByCourse(code, filter)` in `LecturerDAO`. |
| **Lesson 03 (Part 04)** | Encapsulation & Data Hiding | • All fields are strictly `private`.<br>• Access provided exclusively via `public` Getters (Accessors) and Setters (Mutators).<br>• Setter validation prevents negative IDs or blank names. |
| **Lesson 04** | Interfaces (Contract, Default & Static Methods) | • Created interface `ILecturerDAO.java` establishing contract for lecturer operations.<br>• Implicit `public static final` constants: `MIN_ATTENDANCE_REQUIREMENT = 80.0`.<br>• Java 8 `default` method (`printModuleStatus()`) and `static` method (`isPassGrade()`).<br>• Using Interface as a Type: `ILecturerDAO lecturerDAO = new LecturerDAO();`.<br>• Callbacks: GUI buttons implementing `ActionListener` callbacks. |
| **Lesson 05** | Exception Handling (`try-catch-finally`, `throw`, `throws`, Custom Exceptions) | • Custom Checked Exception: `LecturerOperationException extends Exception`.<br>• Explicit `throw new LecturerOperationException(...)` upon validation errors.<br>• Method signatures declared with `throws`.<br>• Multi-catch blocks and `finally` blocks ensuring resources (ResultSets, Statements, Streams) are always closed without swallowing exceptions. |
| **Lesson 06** | File Handling (Character & Binary Streams) | • **Character Streams - Writing:** `PrintWriter` wrapped around `BufferedWriter` and `FileWriter` to export student marks reports to `.txt` files with `flush()` and `close()`.<br>• **Character Streams - Reading:** `BufferedReader` wrapped around `FileReader` using `readLine()`.<br>• **Binary Streams:** `DataOutputStream` (`writeUTF`, `writeInt`) and `DataInputStream` (`readUTF`, `readInt`) for materials backup. |
| **Lesson 07** | JDBC (Java Database Connectivity) | Follows the 05 Basic Steps:<br>1. Register Driver: `Class.forName("com.mysql.cj.jdbc.Driver")`<br>2. Establish Connection: `DBConnection.getConnection()`<br>3. Create Statement: `PreparedStatement ps = conn.prepareStatement(sql)`<br>4. Execute Query/Update: `ps.executeQuery()`, `ps.executeUpdate()`<br>5. Close Connection & Resources in `finally`. |

---

## 3. Key Files in My Package (`tg2088`)

```
tg2088/
├── dao/
│   ├── ILecturerDAO.java             <- (Interface contract - Lesson 04)
│   └── LecturerDAO.java              <- (Implements ILecturerDAO, JDBC 5 steps, Overloading)
├── exception/
│   └── LecturerOperationException.java <- (Custom checked exception - Lesson 05)
├── gui/
│   └── LecturerDashboard.java        <- (Desktop GUI, ActionListeners, File export buttons)
├── model/
│   ├── CourseMaterial.java           <- (Encapsulated model, Initializer block, this chaining)
│   └── Lecturer.java                 <- (Extends User, super constructor, dynamic polymorphism)
├── util/
│   └── LecturerFileHandler.java      <- (File handling: Character & Binary streams - Lesson 06)
└── README_TG2088.md                  <- (This documentation guide)
```

---

## 4. UGC Circular No. 12-2024 Grading Scheme

| Marks Range | Grade | Grade Point (GPV) | Notes |
|:---:|:---:|:---:|:---|
| 85 – 100 | **A+** | 4.00 | Excellent |
| 80 – 84 | **A** | 4.00 | Excellent |
| 75 – 79 | **A-** | 3.70 | Very Good |
| 70 – 74 | **B+** | 3.30 | Good |
| 65 – 69 | **B** | 3.00 | Good |
| 60 – 64 | **B-** | 2.70 | Satisfactory |
| 55 – 59 | **C+** | 2.30 | Satisfactory |
| 50 – 54 | **C** | 2.00 | Pass (Repeat maximum cap) |
| 45 – 49 | **C-** | 1.70 | Conditional Pass |
| 40 – 44 | **D+** | 1.30 | Conditional Pass |
| 35 – 39 | **D** | 1.00 | Conditional Pass |
| 00 – 34 | **E** | 0.00 | Fail / Ineligible |
