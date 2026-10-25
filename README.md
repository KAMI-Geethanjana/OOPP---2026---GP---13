# Faculty Technical Management System (FTMS)
**Course:** ICT2122 - Object Oriented Programming  
**Faculty:** Faculty of Technology, University of Ruhuna  
**Group:** GP-13  
**Assigned Module:** Admin Module  
**Student ID:** TG/2024/2086  

---

## 📌 System Overview
The **Faculty Technical Management System (FTMS)** is a desktop management portal built in Java Swing and JDBC to handle academic operations within the Faculty of Technology. 

The **Admin Module** serves as the central administrative hub, empowering system administrators to:
1. Manage users (Lecturers, Technical Officers, Undergraduates) with role-based security, account lock/unlock, and profiles.
2. Manage academic departments and course allocations.
3. Publish and target official faculty notices.
4. Schedule and maintain lecture/practical/exam timetables.
5. Export administrative audit reports using Character Streams.

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher (Tested on Java 21)
* **MySQL Server:** Running on `localhost:3306`
* **MySQL Database Name:** `ftms_db` (Import script located at `db/ftms_db.sql`)

### Setup Instructions
1. **Import Database:**
   Execute `db/ftms_db.sql` in MySQL Workbench or via terminal:
   ```bash
   mysql -u root -p < db/ftms_db.sql
   ```
2. **Compile Application:**
   Run the batch script:
   ```cmd
   compile.bat
   ```
3. **Run Application:**
   Run the launcher script:
   ```cmd
   run.bat
   ```

---

## 🔑 Default Login Credentials
| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `1234` | Full access to Admin Dashboard |
| **Development Fallback** | `admin` / `TG2086` | Any | Allows login if MySQL is offline |

---

## 🎓 Academic OOP Concepts Mapping (ICT2122 Syllabus)

This module has been designed to implement all concepts covered in the course lecture slides:

| Lesson | OOP & Java Concept | Implementation Details | Relevant Files / Classes |
| :--- | :--- | :--- | :--- |
| **Lesson 01 & 02** | Classes, Objects & Constructors | Parameterized constructors, Constructor Chaining via `this(...)` | [`User.java`](file:///src/model/User.java), [`Admin.java`](file:///src/model/Admin.java) |
| **Lesson 02 (Part 02)** | Singleton Pattern & Static Members | Private constructor, static initializer block, static constants | [`DBConn.java`](file:///src/config/DBConn.java) |
| **Lesson 03 (Part 01)** | Inheritance & Type Relationships | `Admin extends User`, `super(...)` constructor calls, Upcasting & Downcasting with `instanceof` | [`Admin.java`](file:///src/model/Admin.java), [`AdminDashboardUI.java`](file:///src/gui/AdminDashboardUI.java) |
| **Lesson 03 (Part 02)** | Polymorphism | **Runtime:** Overriding `getRoleDisplayName()`, `getDashboardRoute()`<br>**Compile-time:** Overloaded methods in DAOs and models | [`User.java`](file:///src/model/User.java), [`Admin.java`](file:///src/model/Admin.java), [`AdminUserDAO.java`](file:///src/dao/AdminUserDAO.java) |
| **Lesson 03 (Part 03)** | Abstraction | Abstract class `User` with abstract methods | [`User.java`](file:///src/model/User.java) |
| **Lesson 03 (Part 04)** | Encapsulation & Data Validation | Private fields, validated accessors/mutators throwing `ValidationException` | [`User.java`](file:///src/model/User.java), [`Admin.java`](file:///src/model/Admin.java) |
| **Lesson 04** | Interfaces | `interface UserOperations` with constants, abstract methods, Java 8 `default` & `static` methods | [`UserOperations.java`](file:///src/dao/UserOperations.java), [`AdminUserDAO.java`](file:///src/dao/AdminUserDAO.java) |
| **Lesson 05** | Exception Handling | Custom checked exceptions (`AdminOperationException`, `ValidationException`), `try-catch-finally`, `throws` | [`AdminOperationException.java`](file:///src/exception/AdminOperationException.java), [`ValidationException.java`](file:///src/exception/ValidationException.java), [`AdminDashboardUI.java`](file:///src/gui/AdminDashboardUI.java) |
| **Lesson 06** | File Handling (Character Streams) | Report export via `FileWriter` -> `BufferedWriter` -> `PrintWriter`, and reading via `FileReader` -> `BufferedReader` | [`AdminReportUtil.java`](file:///src/util/AdminReportUtil.java), [`AdminDashboardUI.java`](file:///src/gui/AdminDashboardUI.java) |
| **Lesson 07** | Database Connectivity & Transactions | JDBC PreparedStatements, ResultSets, ACID Transactions (`setAutoCommit(false)`, `commit()`, `rollback()`) | [`DBConn.java`](file:///src/config/DBConn.java), [`AdminUserDAO.java`](file:///src/dao/AdminUserDAO.java) |
| **Lesson 08** | Multithreading | Continuous Live Clock thread via `Runnable`, Asynchronous background data loading | [`AdminDashboardUI.java`](file:///src/gui/AdminDashboardUI.java) |

---

## 📂 Project Directory Structure
```
OOPP---2026---GP---13/
├── db/
│   └── ftms_db.sql              # MySQL Schema & seed records
├── lib/
│   └── mysql-connector-j-26.7.0.jar # MySQL JDBC Driver
├── src/
│   ├── Main.java                # Application Entry Point
│   ├── config/
│   │   └── DBConn.java          # JDBC Singleton Connection Manager
│   ├── dao/
│   │   ├── UserOperations.java  # Interface Contract
│   │   ├── AdminUserDAO.java    # User CRUD & Transactions
│   │   ├── AdminCourseDAO.java  # Course Management
│   │   ├── AdminNoticeDAO.java  # Notice Management
│   │   └── AdminTimetableDAO.java # Timetable Management
│   ├── exception/
│   │   ├── AdminOperationException.java # Checked Exception
│   │   └── ValidationException.java     # Format Validation Exception
│   ├── gui/
│   │   ├── LoginUI.java         # Authentication GUI
│   │   └── AdminDashboardUI.java # Multithreaded Admin Dashboard
│   ├── model/
│   │   ├── User.java            # Abstract Base User Model
│   │   ├── Admin.java           # Concrete Admin Model
│   │   ├── Department.java      # Department Entity
│   │   ├── Course.java          # Course Entity
│   │   ├── Notice.java          # Notice Entity
│   │   └── Timetable.java       # Timetable Entity
│   ├── resources/
│   │   └── logo.jpg             # Faculty / University Logo
│   └── util/
│       └── AdminReportUtil.java # File I/O & Character Stream Utilities
├── compile.bat                  # Automated Compilation Script
├── run.bat                      # Automated Execution Script
└── README.md                    # Project Documentation
```