
# FTMS Project Report — TG2088 සහ System Flow

## වාර්තාවේ සීමාව

මෙම වාර්තාව repository එකේ තිබෙන source code සහ SQL files කියවා සකස් කළ එකකි. **Java/SQL source code එකට කිසිදු වෙනසක් කර නැත.** TG2088 module එක පමණක් නොව, එය FTMS system එකේ අනෙක් කොටස් සමඟ සම්බන්ධ වන ආකාරයත් මෙහි විස්තර කර ඇත.

## 1. ව්‍යාපෘතියේ කෙටි හැඳින්වීම

FTMS (Faculty Technical Management System) යනු Java Swing GUI, JDBC සහ MySQL භාවිතා කරන desktop application එකකි. System එකේ ප්‍රධාන භාවිතාකරුවන්:

| Module | ප්‍රධාන වගකීම | Source package |
|---|---|---|
| Admin | Users, departments, courses, notices, timetables | `tg2086` |
| Lecturer — TG2088 | Marks/grades, eligibility, course materials, quizzes, lecturer profile | `tg2088` |
| Undergraduate | තමන්ගේ courses, attendance, medicals, grades, quizzes, profile | `tg2082` |
| Technical Officer | Attendance, medical approvals, timetables, profile | `tg2123` |
| Shared | Login, session, database connection, common models/UI utilities | `common` |

TG2088 කියන්නේ වෙනම application එකක් නොවෙයි. `Main` එකෙන් ආරම්භ වන එකම FTMS app එකේ Lecturer role එකට අදාළ module එකයි. TG2088 විසින් භාවිතා කරන quiz classes undergraduate module එකෙන්ද භාවිතා වෙනවා.

## 2. පරිගණකයේ run වන අනුපිළිවෙළ

1. MySQL Server එක `localhost:3306` හි ක්‍රියාත්මක විය යුතුය. JDBC driver එක `lib/mysql-connector-j-26.7.0.jar` තුළ ඇත.
2. Database එක `ftms_db` ලෙස සකස් කර data ඇතුළත් කළ යුතුය. ව්‍යාපෘතියේ `database/schema.sql` එක database එක drop කර නැවත සාදන script එකක් බැවින්, දැනට තිබෙන දත්ත අවශ්‍ය නම් එය ධාවනය නොකරන්න. පිරිසිදු demo database එකක් සඳහා schema එකෙන් පසු `database/seed.sql` ධාවනය කරන්න.
3. `compile.bat` මඟින් `src` යටතේ ඇති Java files සියල්ල compile කර class files `out` වෙත දමයි.
4. `run.bat` මඟින් `Main` ධාවනය කර login window එක විවෘත කරයි.
5. Login button එකෙන් `LoginFrame.handleLogin()` → `AuthDAO.authenticate()` වෙත username/password යවයි.
6. `AuthDAO` database එකේ user row එක, active status එක සහ failed-login count එක පරීක්ෂා කරයි. වැරදි login පහක් වූ විට account එක lock වෙයි. සාර්ථක වුවහොත් role එක අනුව එම user subtype එක database එකෙන් load කරයි.
7. `UserSession` එකේ current user සුරැකේ. `user.openDashboard(...)`-ийн runtime polymorphism එකෙන් Admin/Lecturer/Technical Officer/Undergraduate dashboard එක համապատասխանವಾಗಿ විවෘත වෙයි.
8. Lecturer role එකට `Lecturer.openDashboard()` මඟින් `LecturerDashboard` විවෘත වේ.
9. Dashboard එකේ button එකක් තෝරාගත් විට GUI එක DAO එකට data ඉල්ලීමක් හෝ update එකක් යවයි. DAO එක `DBConnection` හරහා MySQL වෙත JDBC query යවයි.
10. ResultSet එකෙන් ලැබෙන data model objects බවට පත් කර dashboard table එකට පෙන්වයි. Save/submit කිරීමෙන් data database එකට ලියවෙයි; export/backup කළ විට තෝරාගත් local file එකට ලියවෙයි.

### Login එක ගැන වැදගත් වෙනස

`database/seed.sql` හි lecturer account එක `wosada` ලෙස ඇත (`LECTURER` role). `tg2088` username එක වෙනම undergraduate demo account එකකි (`UNDERGRADUATE` role). එනිසා `tg2088` දමා login වුවහොත් lecturer dashboard එක නොව student dashboard එක ලැබේ. Seed data භාවිතා කරන demo setup එකේ password එක `1234` ය.

`common/config/DBConnection.java` හි database URL එක `ftms_db` සහ user එක `root` ලෙස සකසා ඇත. Password එක මුලින් `DB_PASSWORD` environment variable එකෙන් හෝ `db.password` system property එකෙන් කියවයි. එම දෙක නැත්නම් development candidate passwords උත්සාහ කරයි. MySQL credentials වෙනස් නම් connection එකට පෙර ඒවා ඔබේ local MySQL setup එකට ගැළපිය යුතුය.

## 3. TG2088 module එකේ file-by-file කාර්යය

### 3.1 Dashboard සහ GUI

| File | කාර්යය |
|---|---|
| `src/tg2088/gui/LecturerDashboard.java` | Lecturer portal එකේ ප්‍රධාන window එක. Marks & Grading, Eligibility, Materials, Quizzes, Attendance, Notices, Profile යන cards සකස් කර buttons වලට ක්‍රියා සම්බන්ධ කරයි. Data load කිරීම, grade ගණනය, save කිරීම, quiz කළමනාකරණය සහ export/backup actions මෙහි සම්බන්ධ වේ. |
| `src/tg2088/gui/QuizQuestionDialog.java` | තෝරාගත් quiz එකට MCQ ප්‍රශ්න add/edit/delete කරයි. ප්‍රශ්නය, A-D options, correct option සහ points `IQuizDAO` හරහා save කරයි. |
| `src/tg2088/gui/QuizTestRunnerDialog.java` | Lecturer හට quiz එක තමන්ම test කර පිළිතුරු evaluate කිරීමට ඉඩ දෙයි. Pass/fail එක පෙන්වයි. “Save this attempt…” තෝරා ඇත්නම් `LEC/PREVIEW` attempt එක database එකට ලියයි. |
| `src/tg2088/gui/QuizResultsDialog.java` | Quiz එකක student attempts, marks සහ pass rate පෙන්වයි; results `.txt` ලෙස export කරයි. “Demo Submission” button එකෙන් sample submission එකක් ඇතුළත් කළ හැක. |

### 3.2 Database layer — DAO

DAO යනු GUI සහ database අතර ඇති සේවා ස්ථරයයි. SQL query එකක් GUI එක තුළ සෘජුව නොතබා DAO එකෙන් ක්‍රියාත්මක කරයි.

| File | කාර්යය |
|---|---|
| `src/tg2088/dao/ILecturerDAO.java` | Lecturer operations සඳහා interface/contract එක. Course, result, material, attendance, profile operations සහ 80% attendance, 40% CA වැනි constants අර්ථ දක්වයි. |
| `src/tg2088/dao/LecturerDAO.java` | `ILecturerDAO` ක්‍රියාත්මක කර MySQL queries යවයි: lecturer/course/result/material/attendance/profile data. Marks සඳහා `INSERT ... ON DUPLICATE KEY UPDATE` යොදා තිබෙන නිසා enrollment එකට result එකක් තිබේ නම් update වෙයි; නැත්නම් insert වෙයි. |
| `src/tg2088/dao/IQuizDAO.java` | Quiz, question, attempt operations සඳහා interface එක. Quiz pass threshold එක 50% ලෙස සපයයි. |
| `src/tg2088/dao/QuizDAO.java` | Quiz CRUD, MCQ CRUD සහ attempt save/read කිරීම කරයි. JDBC `PreparedStatement` භාවිතා කරයි. DAO එක නිර්මාණය කරන අවස්ථාවේ `quiz`, `quiz_question`, `quiz_attempt` tables `CREATE TABLE IF NOT EXISTS` මඟින් පරීක්ෂා/සාදයි. |

### 3.3 Models — data නිරූපණය කරන classes

| File | නිරූපණය |
|---|---|
| `src/tg2088/model/Lecturer.java` | `common.model.User` extend කරන Lecturer model එක. Lecturer/employee/department fields, validated setters සහ Lecturer dashboard route එක අඩංගුය. |
| `src/tg2088/model/CourseMaterial.java` | Course material එකක ID, course code, lecturer, title, file path සහ upload date තබයි. “Upload” මෙහි database එකට title/path metadata එකක් ලිවීමයි; තෝරාගත් file එක app එකෙන් copy/upload කිරීමක් биш. |
| `src/tg2088/model/Quiz.java` | Quiz ID, course, lecturer, title, duration, total marks, active status, created time зэрэг quiz metadata. |
| `src/tg2088/model/QuizQuestion.java` | MCQ ප්‍රශ්නය, options A-D, නිවැරදි option සහ එක් ප්‍රශ්නයකට points. |
| `src/tg2088/model/QuizAttempt.java` | Student/preview submission එකේ score, maximum score, percentage, status සහ time. Score හෝ max score සකසන විට model එක percentage/status නැවත ගණනය කරයි. |

### 3.4 Exceptions සහ file utilities

| File | කාර්යය |
|---|---|
| `src/tg2088/exception/LecturerOperationException.java` | Lecturer operation වල validation/database errors පැහැදිලිව උඩට දැනුම්දෙන checked exception එක. |
| `src/tg2088/exception/QuizOperationException.java` | Quiz operation වල validation/database errors සඳහා checked exception එක. |
| `src/tg2088/util/LecturerFileHandler.java` | Marks report `.txt` export, text-file read සහ materials metadata `.dat` binary backup/read. |
| `src/tg2088/util/QuizFileHandler.java` | Quiz paper සහ result report `.txt` export, quiz/questions `.dat` backup සහ binary backup එක read කිරීම. Answer key එක export වෙද්දී ඇතුළත් කරනවාදැයි තෝරාගත හැක. |

### 3.5 TG2088 dashboard එකේ sections

- **Marks & Grading:** Course එක තෝරා student enrollments/results load කරයි. CA සහ final marks edit කර Calculate Grades කිරීමෙන් total, eligibility, grade, GPV ලැබේ. Save Results කිරීමෙන් `result` table එකට ලියයි.
- **Eligibility:** CA eligibility සහ attendance eligibility වෙන්ව ගණනය කරයි; අවසාන exam eligibility සඳහා දෙකම අවශ්‍යයි.
- **Course Materials:** Course එකකට title සහ path එකක් ලියා database එකට save/delete කරයි. Materials list එක `.dat` file එකකට backup කළ හැක.
- **Quizzes:** Quiz create/activate/deactivate/delete, questions manage, lecturer preview, submissions view, quiz paper export සහ binary backup.
- **Attendance:** Course එකේ enrolled students සඳහා attendance summary සහ approved medical count පෙන්වයි.
- **Notices:** Lecturer audience එකට අදාල notices පෙන්වයි.
- **Profile:** Lecturer ගේ name/email/phone update කරයි.
- **Sign out:** `UserSession` clear කර login window එකට ආපසු යයි.

## 4. TG2088 calculations

### 4.1 Marks, eligibility, grade සහ GPV

`LecturerDashboard.calculateMarksOnTable()` විසින් marks කියවා `common.util.GradeCalculator` හරහා මේ පියවර ගනී:

1. CA සහ Final Exam දෙකම 0–100 අතරදැයි validate කරයි.
2. CA eligibility: `CA >= 40`.
3. Weighted total:

   `Total = CA × 0.40 + FinalExam × 0.60`

4. Total එක දශම ස්ථානයකට round කරයි.
5. Marks range එක අනුව letter grade සහ grade point (GPV) තීරණය කරයි.

| Total marks | Grade | GPV |
|---:|:---:|---:|
| 85+ | A+ | 4.00 |
| 80–84.9 | A | 4.00 |
| 75–79.9 | A- | 3.70 |
| 70–74.9 | B+ | 3.30 |
| 65–69.9 | B | 3.00 |
| 60–64.9 | B- | 2.70 |
| 55–59.9 | C+ | 2.30 |
| 50–54.9 | C | 2.00 |
| 45–49.9 | C- | 1.70 |
| 40–44.9 | D+ | 1.30 |
| 35–39.9 | D | 1.00 |
| 35 ට අඩු | E | 0.00 |

CA `< 40` නම් `NOT_ELIGIBLE`, grade `E`, GPV `0.00` වේ. `REPEAT` attempt එකක raw grade එක C ට වඩා ඉහළ නම් grade එක C දක්වා cap කරයි.

**උදාහරණය:** CA 82, Final 86 නම් `82×0.40 + 86×0.60 = 84.4`; CA එක 40 හෝ වැඩි නිසා eligible; grade `A`, GPV `4.00`.

**Implementation එකෙන් පෙනෙන වැදගත් සටහන:** Marks calculation එකේ `determineGrade()` වෙත යන්නේ CA eligibility එක පමණයි. Attendance eligibility එක `Eligibility` panel එකේ වෙනම `CA >= 40 AND excused attendance >= 80%` ලෙස පෙන්වයි; එම attendance ප්‍රතිඵලය marks calculation එකේ grade ලබාදීමට එක් නොකරයි. ඒ නිසා attendance 80% ට අඩු වුවත් CA සුදුසු නම් grade calculation එක eligible ලෙස ඉදිරියට යා හැක. මෙය code එකේ දැනට ඇති behavior එක පිළිබඳ සටහනක් මිස source change එකක් නොවේ.

### 4.2 Attendance

`LecturerDAO` එක standard sessions ගණන 15 ලෙස ගනී:

- Raw attendance = `attended sessions / 15 × 100`
- Excused attendance = `(attended sessions + approved medical count) / 15 × 100`, උපරිම 100% දක්වා cap කරයි.
- Exam eligibility panel එකේ `excused attendance >= 80%` සහ `CA >= 40` දෙකම සත්‍ය විය යුතුය.

මෙම code එක approved medical **records ගණන** attendance sessions ගණනට එකතු කරයි. Database එකේ medical dates සහ absent dates එකිනෙක ගැළපේදැයි එම calculation එක පරීක්ෂා නොකරයි.

### 4.3 Online quiz

- Student module එක පෙන්වන්නේ තමන් enroll වී ඇති course වල **active** quizzes පමණි.
- Quiz එක open කිරීමට පෙර active status, course enrollment සහ ප්‍රශ්න තිබීම පරීක්ෂා කරයි.
- නිවැරදි option එකට එක් එක් ප්‍රශ්නයේ points එකතුවෙයි.
- Maximum score = සියලු ප්‍රශ්නවල points එකතුව.
- Percentage = `score / maximum score × 100`.
- `>= 50%` නම් PASS; නැත්නම් FAIL.
- `Quiz.durationMinutes` UI එකේ පෙන්වන නමුත් `StudentQuizPanel` තුළ countdown/timer එකක් හෝ කාලය අවසන් වූ විට submit කිරීමක් නොපෙනේ. එනම් duration එක දත්තයක් ලෙස සුරකින/පෙන්වන නමුත් දැනට quiz එකට enforce කරන බව code එකෙන් තහවුරු නොවේ.
- Lecturer preview එකෙන් optional ලෙස `LEC/PREVIEW` attempt එකක්, results dialog එකෙන් manual demo submission එකක් database එකට යැවිය හැක.

## 5. මුළු project එකේ files/modules — කෙටි map එක

### Application entry සහ shared `common` package

- `src/Main.java` — application entry point; system look-and-feel සකසා login screen එක Swing event thread එකේ විවෘත කරයි.
- `src/common/auth/LoginFrame.java`, `AuthDAO.java` — login UI, authentication, failed login lockout සහ role route.
- `src/common/config/DBConnection.java` — MySQL JDBC connection factory.
- `src/common/dao/NoticeDAO.java` — role අනුව notices කියවීම/කළමනාකරණයට shared DAO.
- `src/common/model/User.java`, `Department.java`, `Course.java`, `Notice.java`, `Timetable.java`, `Attendance.java`, `Medical.java`, `Mark.java`, `Result.java` — roles සහ academic data සඳහා shared data models.
- `src/common/exception/AccountLockedException.java`, `DatabaseException.java`, `IneligibleStudentException.java`, `InvalidMarksException.java` — shared error types.
- `src/common/util/GradeCalculator.java` — marks validation, weighted total, eligibility, grade සහ GPV calculation.
- `src/common/util/UserSession.java` — logged-in user session එක process එක තුළ තබයි.
- `src/common/util/UITheme.java` — shared colors, fonts, button/table helpers.
- `src/tg2088/README_TG2088.md`, `src/tg2086/README_TG2086.md`, `README.md` — module/root documentation. Root README හි setup path එක repository එකේ වත්මන් `database/` layout එකට නොගැළපේ.

### `tg2082` — Undergraduate module

- `model/Undergraduate.java` — User subclass සහ student-specific data.
- `dao/UndergraduateDAO.java` — student profile, enrolled courses, results, attendance, materials ආදී data access.
- `gui/UndergraduateDashboard.java` — student portal එක සහ navigation.
- `gui/DashboardPanel.java`, `CoursePanel.java`, `GradesPanel.java`, `AttendancePanel.java`, `MedicalPanel.java`, `NoticesPanel.java`, `ProfilePanel.java`, `TimetablePanel.java` — student view එකේ අදාළ sections.
- `gui/StudentQuizPanel.java` — TG2088 හි `IQuizDAO`, `Quiz`, `QuizQuestion`, `QuizAttempt` භාවිතා කර student quiz list, attempt, auto-marking සහ result save කරයි.

### `tg2086` — Admin module

- `model/Admin.java` — Admin user model/subclass.
- `dao/AdminDAO.java`, `AdminUserDAO.java`, `AdminCourseDAO.java`, `AdminNoticeDAO.java`, `AdminTimetableDAO.java` — Admin identity, user, course, notice, timetable operations.
- `dao/UserOperations.java` — user operations සඳහා interface.
- `gui/BaseDashboardUI.java`, `AdminDashboardUI.java` — shared/admin dashboard UI.
- `exception/AdminOperationException.java`, `ValidationException.java` — admin operations සහ input validation errors.
- `util/AdminReportUtil.java` — admin report/file utility.
- `util/UITheme.java` — Admin module හි UI helpers.

### `tg2123` — Technical Officer module

- `model/TechnicalOfficer.java` — Technical Officer user model.
- `config/DBConnection.java` — module-specific connection helper.
- `dao/TechnicalOfficerDAO.java`, `TechnicalOfficerRepository.java`, `AttendanceDAO.java`, `MedicalDAO.java`, `TimetableDAO.java` — officer/profile සහ attendance/medical/timetable data access.
- `gui/TechnicalOfficerDashboard.java`, `AttendancePanel.java`, `MedicalPanel.java`, `ProfilePanel.java`, `TimetablePanel.java` — Technical Officer UI.
- `testing/TestDBConnection.java`, `TestAttendanceDAO.java`, `TestAttendancePanel.java`, `TestMedicalDAO.java`, `TestMedicalPanel.java`, `TestProfilePanel.java`, `TestTimetableDAO.java`, `TestTimetablePanel.java` — එම module එකේ manual/test launch classes.

### Database, scripts සහ resources

- `database/schema.sql` — FTMS tables සහ foreign keys නිර්වචනය කරයි; ආරම්භයේදී `ftms_db` drop/recreate කරන බැවින් සැලකිලිමත් වන්න.
- `database/seed.sql` — demo roles, courses, enrollments, marks, attendance සහ medical records ඇතුළු sample data.
- `database/data.sql` — seed data ඇතුළත් වෙනම data script එක; clean database එක සකස් කිරීමට සාමාන්‍යයෙන් `schema.sql` පසු `seed.sql` යොදාගන්න.
- `lib/mysql-connector-j-26.7.0.jar` — MySQL JDBC driver.
- `resources/`, `profile_pictures/`, `profile_photos/`, `medical_certificates/` — app assets සහ sample/profile/medical files.
- `out/` — compiled `.class` files; source code නොවේ.
- `compile.bat`, `run.bat` — compile සහ සාමාන්‍ය `Main` login flow.
- `run_admin.bat` — login flow එක මඟ හැර Admin dashboard class එක සෘජුව launch කරන වෙනම script එක.
- `OOP Group 13 Project/` — project specification, SRS, UML/ER diagrams ඇතුළත් documentation.

## 6. දැනගත යුතු සීමා සහ documentation වෙනස්කම්

1. Root `README.md` එකේ සඳහන් `db/ftms_db.sql` path එක repository එකේ නැත; මෙහි දක්නට ලැබෙන SQL scripts `database/` තුළ වෙන වෙනම ඇත.
2. Root README එකේ කියන MySQL-offline development fallback එක `AuthDAO`/`LoginFrame` login flow එකෙන් පෙනෙන්නේ නැත; සාමාන්‍ය login එකට MySQL connection එක අවශ්‍යය.
3. Database එකේ `password_hash` ලෙස column එක නම් කර තිබුණත් demo seed data එකේ password plain text ලෙස ඇත, `AuthDAO` එක `password.equals(passHash)` ලෙස සසඳයි. මෙය demo implementation එකක් ලෙස පමණක් සැලකිය යුතුය.
4. Quiz/marks results සහ backups local file/database එකට ලියන operations වේ; report එකෙන් system එක run කර tested කළ බවක් අදහස් නොවේ. මෙම කාර්යය code-reading/report එකක් පමණි.

## 7. සාරාංශය

TG2088 හි ප්‍රධාන භූමිකාව Lecturer dashboard එකට අදාළ marks/grade engine, eligibility display, course-material metadata, quiz creation/question bank/student marking සහ file exports/backup ය. App එක `Main → LoginFrame → AuthDAO → role model → role dashboard` ලෙස ආරම්භ වී, `Dashboard → DAO → DBConnection → MySQL` හරහා data කියවයි/ලියයි. Student quiz flow එක TG2082 module එකෙන් TG2088 quiz models සහ DAO නැවත භාවිතා කිරීම මෙම project එකේ වැදගත් module integration එකකි.
