package tg2088.dao;

import common.config.DBConnection;
import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Lesson 04 (Interfaces) & Lesson 07 (JDBC Connectivity)
 * ============================================================================
 * Reference:
 * - Lesson 04, Slide 17-18: 'class QuizDAO implements IQuizDAO'
 * - Lesson 07, Slide 25: 05 Basic Steps to Use a Database in Java
 *   1. Register the Driver Class
 *   2. Create a Connection
 *   3. Create a Statement (PreparedStatement)
 *   4. Execute Queries (executeQuery, executeUpdate)
 *   5. Close the Connection and Resources
 * - Lesson 07, Slide 33-34: Parameterized Queries with PreparedStatement (?)
 * - Lesson 07, Slide 35-36: ResultSet traversal & Column Index / Column Name access
 * - Lesson 03 Part 02, Slide 13-17: Method Overloading (Compile-Time Polymorphism)
 * - Lesson 05, Slide 25-37: Exception Handling (try-catch-finally, custom exceptions)
 * ============================================================================
 */
public class QuizDAO implements IQuizDAO {

    // ========================================================================
    // Static Initializer Block (Lesson 02 Part 02, Slide 29-30)
    // Ensures JDBC Driver is loaded when class is first referenced
    // ========================================================================
    static {
        try {
            // Step 1: Register the Driver Class (Lesson 07, Slide 26)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver registration error in QuizDAO: " + e.getMessage());
        }
    }

    public QuizDAO() {
        // Invoking interface default method (Lesson 04, Slide 32)
        printQuizDAOStatus();
        initTables();
    }

    /**
     * Self-healing table initialization: ensures the quiz tables exist
     * without modifying any existing tables in the database.
     */
    private void initTables() {
        String sqlQuiz = "CREATE TABLE IF NOT EXISTS quiz (" +
                "quiz_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "course_code VARCHAR(20) NOT NULL, " +
                "lecturer_id INT NOT NULL, " +
                "title VARCHAR(150) NOT NULL, " +
                "description TEXT, " +
                "duration_minutes INT NOT NULL DEFAULT 30, " +
                "total_marks DECIMAL(5,2) DEFAULT 100.00, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String sqlQuestions = "CREATE TABLE IF NOT EXISTS quiz_question (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "quiz_id INT NOT NULL, " +
                "question_text TEXT NOT NULL, " +
                "option_a VARCHAR(255) NOT NULL, " +
                "option_b VARCHAR(255) NOT NULL, " +
                "option_c VARCHAR(255) NOT NULL, " +
                "option_d VARCHAR(255) NOT NULL, " +
                "correct_option CHAR(1) NOT NULL, " +
                "points DECIMAL(5,2) DEFAULT 10.00, " +
                "FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE" +
                ")";

        String sqlAttempts = "CREATE TABLE IF NOT EXISTS quiz_attempt (" +
                "attempt_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "quiz_id INT NOT NULL, " +
                "student_reg_no VARCHAR(50) NOT NULL, " +
                "student_name VARCHAR(150) DEFAULT 'Student', " +
                "score DECIMAL(5,2) NOT NULL, " +
                "max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00, " +
                "percentage DECIMAL(5,2) NOT NULL, " +
                "status ENUM('PASS', 'FAIL') DEFAULT 'PASS', " +
                "attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE" +
                ")";

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlQuiz);
            stmt.executeUpdate(sqlQuestions);
            stmt.executeUpdate(sqlAttempts);
        } catch (SQLException e) {
            System.err.println("Notice: Quiz tables check/creation notice: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    /**
     * Creates a new quiz.
     * Demonstrates JDBC Steps 1 to 5, PreparedStatement with Generated Keys.
     */
    @Override
    public int createQuiz(Quiz quiz) throws QuizOperationException {
        if (quiz == null || quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            throw new QuizOperationException("Validation Error: Quiz title cannot be empty.");
        }
        if (quiz.getCourseCode() == null || quiz.getCourseCode().trim().isEmpty()) {
            throw new QuizOperationException("Validation Error: Course code is required for quiz creation.");
        }

        String sql = "INSERT INTO quiz (course_code, lecturer_id, title, description, duration_minutes, total_marks, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quiz.getCourseCode());
            ps.setInt(2, quiz.getLecturerId());
            ps.setString(3, quiz.getTitle());
            ps.setString(4, quiz.getDescription());
            ps.setInt(5, quiz.getDurationMinutes());
            ps.setDouble(6, quiz.getTotalMarks());
            ps.setBoolean(7, quiz.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    quiz.setQuizId(generatedId);
                    return generatedId;
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to create quiz (" + e.getMessage() + ")", e);
        } finally {
            closeResources(rs, ps);
        }
    }

    @Override
    public boolean updateQuiz(Quiz quiz) throws QuizOperationException {
        if (quiz == null || quiz.getQuizId() <= 0) {
            throw new QuizOperationException("Validation Error: Invalid quiz ID for update.");
        }

        String sql = "UPDATE quiz SET title = ?, description = ?, duration_minutes = ?, total_marks = ?, is_active = ? " +
                "WHERE quiz_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getDurationMinutes());
            ps.setDouble(4, quiz.getTotalMarks());
            ps.setBoolean(5, quiz.isActive());
            ps.setInt(6, quiz.getQuizId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to update quiz (" + e.getMessage() + ")", e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public boolean deleteQuiz(int quizId) throws QuizOperationException {
        String sql = "DELETE FROM quiz WHERE quiz_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to delete quiz ID " + quizId, e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public Quiz getQuizById(int quizId) {
        String sql = "SELECT q.*, COUNT(qq.question_id) AS question_count " +
                "FROM quiz q " +
                "LEFT JOIN quiz_question qq ON q.quiz_id = qq.quiz_id " +
                "WHERE q.quiz_id = ? " +
                "GROUP BY q.quiz_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuiz(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quiz by ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return null;
    }

    /**
     * Demonstrates Static Polymorphism / Method Overloading (Lesson 03 Part 02, Slide 13-15)
     */
    @Override
    public List<Quiz> getQuizzesByCourse(String courseCode) {
        return getQuizzesByCourse(courseCode, false);
    }

    /**
     * Overloaded method to retrieve quizzes filtered by active status.
     */
    @Override
    public List<Quiz> getQuizzesByCourse(String courseCode, boolean activeOnly) {
        List<Quiz> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.*, COUNT(qq.question_id) AS question_count ");
        sb.append("FROM quiz q ");
        sb.append("LEFT JOIN quiz_question qq ON q.quiz_id = qq.quiz_id ");
        sb.append("WHERE q.course_code = ? ");
        if (activeOnly) {
            sb.append("AND q.is_active = TRUE ");
        }
        sb.append("GROUP BY q.quiz_id ORDER BY q.quiz_id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, courseCode);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToQuiz(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quizzes by course: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return list;
    }

    @Override
    public List<Quiz> getQuizzesByLecturer(int lecturerId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, COUNT(qq.question_id) AS question_count " +
                "FROM quiz q " +
                "LEFT JOIN quiz_question qq ON q.quiz_id = qq.quiz_id " +
                "WHERE q.lecturer_id = ? " +
                "GROUP BY q.quiz_id ORDER BY q.quiz_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lecturerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToQuiz(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quizzes by lecturer: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return list;
    }

    @Override
    public boolean toggleQuizStatus(int quizId, boolean isActive) throws QuizOperationException {
        String sql = "UPDATE quiz SET is_active = ? WHERE quiz_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Failed to update status for quiz ID " + quizId, e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public int addQuestion(QuizQuestion question) throws QuizOperationException {
        if (question == null || question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new QuizOperationException("Validation Error: Question text cannot be blank.");
        }

        String sql = "INSERT INTO quiz_question (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option, points) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, String.valueOf(question.getCorrectOption()));
            ps.setDouble(8, question.getPoints());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int genId = rs.getInt(1);
                    question.setQuestionId(genId);
                    return genId;
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to add question (" + e.getMessage() + ")", e);
        } finally {
            closeResources(rs, ps);
        }
    }

    @Override
    public boolean updateQuestion(QuizQuestion question) throws QuizOperationException {
        if (question == null || question.getQuestionId() <= 0) {
            throw new QuizOperationException("Validation Error: Invalid question ID for update.");
        }

        String sql = "UPDATE quiz_question SET question_text = ?, option_a = ?, option_b = ?, " +
                "option_c = ?, option_d = ?, correct_option = ?, points = ? WHERE question_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, question.getQuestionText());
            ps.setString(2, question.getOptionA());
            ps.setString(3, question.getOptionB());
            ps.setString(4, question.getOptionC());
            ps.setString(5, question.getOptionD());
            ps.setString(6, String.valueOf(question.getCorrectOption()));
            ps.setDouble(7, question.getPoints());
            ps.setInt(8, question.getQuestionId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to update question (" + e.getMessage() + ")", e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public boolean deleteQuestion(int questionId) throws QuizOperationException {
        String sql = "DELETE FROM quiz_question WHERE question_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to delete question ID " + questionId, e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public List<QuizQuestion> getQuestionsByQuizId(int quizId) {
        List<QuizQuestion> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_question WHERE quiz_id = ? ORDER BY question_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            while (rs.next()) {
                QuizQuestion q = new QuizQuestion(
                        rs.getInt("question_id"),
                        rs.getInt("quiz_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option") != null && !rs.getString("correct_option").isEmpty() ?
                                rs.getString("correct_option").charAt(0) : 'A',
                        rs.getDouble("points")
                );
                list.add(q);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching questions for quiz ID " + quizId + ": " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return list;
    }

    @Override
    public boolean recordAttempt(QuizAttempt attempt) throws QuizOperationException {
        if (attempt == null || attempt.getStudentRegNo() == null || attempt.getStudentRegNo().trim().isEmpty()) {
            throw new QuizOperationException("Validation Error: Student registration number is required.");
        }

        String sql = "INSERT INTO quiz_attempt (quiz_id, student_reg_no, student_name, score, max_score, percentage, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, attempt.getQuizId());
            ps.setString(2, attempt.getStudentRegNo());
            ps.setString(3, attempt.getStudentName());
            ps.setDouble(4, attempt.getScore());
            ps.setDouble(5, attempt.getMaxScore());
            ps.setDouble(6, attempt.getPercentage());
            ps.setString(7, attempt.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Database Error: Failed to record student attempt (" + e.getMessage() + ")", e);
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public List<QuizAttempt> getAttemptsByQuizId(int quizId) {
        List<QuizAttempt> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempt WHERE quiz_id = ? ORDER BY attempted_at DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            while (rs.next()) {
                QuizAttempt a = new QuizAttempt(
                        rs.getInt("attempt_id"),
                        rs.getInt("quiz_id"),
                        rs.getString("student_reg_no"),
                        rs.getString("student_name"),
                        rs.getDouble("score"),
                        rs.getDouble("max_score"),
                        rs.getDouble("percentage"),
                        rs.getString("status"),
                        rs.getString("attempted_at")
                );
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attempts for quiz ID " + quizId + ": " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return list;
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================
    private Quiz mapResultSetToQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz(
                rs.getInt("quiz_id"),
                rs.getString("course_code"),
                rs.getInt("lecturer_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("duration_minutes"),
                rs.getDouble("total_marks"),
                rs.getBoolean("is_active"),
                rs.getString("created_at")
        );
        try {
            q.setQuestionCount(rs.getInt("question_count"));
        } catch (SQLException ignored) {
        }
        return q;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            // Ignore
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            // Ignore
        }
    }
}
