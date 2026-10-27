package tg2088.dao;

import common.config.DBConnection;
import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO implements IQuizDAO {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver registration error in QuizDAO: " + e.getMessage());
        }
    }

    public QuizDAO() {
        printQuizDAOStatus();
        initTables();
    }

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
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

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
                "FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE)";

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
                "FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE)";

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlQuiz);
            stmt.executeUpdate(sqlQuestions);
            stmt.executeUpdate(sqlAttempts);
        } catch (SQLException e) {
            System.err.println("Notice: Quiz tables init notice: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
    }

    public int createQuiz(Quiz quiz) throws QuizOperationException {
        String sql = "INSERT INTO quiz (course_code, lecturer_id, title, description, duration_minutes, total_marks, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getCourseCode());
            ps.setInt(2, quiz.getLecturerId());
            ps.setString(3, quiz.getTitle());
            ps.setString(4, quiz.getDescription());
            ps.setInt(5, quiz.getDurationMinutes());
            ps.setDouble(6, quiz.getTotalMarks());
            ps.setBoolean(7, quiz.isActive());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Failed to create quiz: " + e.getMessage(), e);
        }
    }

    public boolean updateQuiz(Quiz quiz) throws QuizOperationException {
        String sql = "UPDATE quiz SET title = ?, description = ?, duration_minutes = ?, total_marks = ?, is_active = ? WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getDurationMinutes());
            ps.setDouble(4, quiz.getTotalMarks());
            ps.setBoolean(5, quiz.isActive());
            ps.setInt(6, quiz.getQuizId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Failed to update quiz: " + e.getMessage(), e);
        }
    }

    public boolean deleteQuiz(int quizId) throws QuizOperationException {
        String sql = "DELETE FROM quiz WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new QuizOperationException("Failed to delete quiz ID " + quizId, e);
        }
    }

    public Quiz getQuizById(int quizId) {
        String sql = "SELECT * FROM quiz WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Quiz(rs.getInt("quiz_id"), rs.getString("course_code"), rs.getInt("lecturer_id"),
                        rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"),
                        rs.getDouble("total_marks"), rs.getBoolean("is_active"), rs.getString("created_at"));
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public List<Quiz> getQuizzesByCourse(String courseCode) {
        return getQuizzesByCourse(courseCode, false);
    }

    public List<Quiz> getQuizzesByCourse(String courseCode, boolean activeOnly) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE course_code = ?" + (activeOnly ? " AND is_active = TRUE" : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Quiz(rs.getInt("quiz_id"), rs.getString("course_code"), rs.getInt("lecturer_id"),
                        rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"),
                        rs.getDouble("total_marks"), rs.getBoolean("is_active"), rs.getString("created_at")));
            }
        } catch (SQLException ignored) {}
        return list;
    }

    public List<Quiz> getQuizzesByLecturer(int lecturerId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE lecturer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Quiz(rs.getInt("quiz_id"), rs.getString("course_code"), rs.getInt("lecturer_id"),
                        rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"),
                        rs.getDouble("total_marks"), rs.getBoolean("is_active"), rs.getString("created_at")));
            }
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean toggleQuizStatus(int quizId, boolean isActive) throws QuizOperationException { return false; }
    public int addQuestion(QuizQuestion question) throws QuizOperationException { return 0; }
    public boolean updateQuestion(QuizQuestion question) throws QuizOperationException { return false; }
    public boolean deleteQuestion(int questionId) throws QuizOperationException { return false; }
    public List<QuizQuestion> getQuestionsByQuizId(int quizId) { return new ArrayList<>(); }
    public boolean recordAttempt(QuizAttempt attempt) throws QuizOperationException { return false; }
    public List<QuizAttempt> getAttemptsByQuizId(int quizId) { return new ArrayList<>(); }
}
