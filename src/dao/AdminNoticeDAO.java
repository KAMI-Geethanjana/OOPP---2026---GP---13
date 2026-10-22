package dao;

import config.DBConn;
import exception.AdminOperationException;
import model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminNoticeDAO - Data Access Object for Notice Publishing and Maintenance.
 * Demonstrates:
 * 1. JDBC PreparedStatements & ResultSet traversal (Lesson 07)
 * 2. Method Overloading (getAllNotices with/without audience, deleteNotice by int vs object - Lesson 03 Part 02)
 * 3. Exception Handling (throws AdminOperationException - Lesson 05)
 */
public class AdminNoticeDAO {

    /**
     * Retrieves all published notices ordered by most recent first.
     */
    public List<Notice> getAllNotices() throws AdminOperationException {
        return getAllNotices(null);
    }

    /**
     * Method Overloading: Retrieves notices optionally filtered by target audience.
     * Demonstrates Lesson 03 Part 02 (Method Overloading by changing parameters).
     */
    public List<Notice> getAllNotices(String targetAudience) throws AdminOperationException {
        List<Notice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT n.notice_id, n.title, n.content, n.target_audience, n.posted_by, n.posted_date, " +
                "u.full_name AS posted_by_name " +
                "FROM notice n " +
                "JOIN users u ON n.posted_by = u.user_id "
        );

        if (targetAudience != null && !targetAudience.trim().isEmpty() && !"ALL".equalsIgnoreCase(targetAudience.trim())) {
            sql.append("WHERE n.target_audience = ? OR n.target_audience = 'ALL' ");
        }
        sql.append("ORDER BY n.posted_date DESC");

        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (targetAudience != null && !targetAudience.trim().isEmpty() && !"ALL".equalsIgnoreCase(targetAudience.trim())) {
                ps.setString(1, targetAudience.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notice notice = new Notice(
                            rs.getInt("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("target_audience"),
                            rs.getInt("posted_by"),
                            rs.getString("posted_by_name"),
                            rs.getTimestamp("posted_date")
                    );
                    list.add(notice);
                }
            }
        } catch (SQLException e) {
            throw new AdminOperationException("Database error retrieving notices: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Creates and publishes a new administrative notice.
     */
    public boolean addNotice(Notice notice) throws AdminOperationException {
        if (notice == null || notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new AdminOperationException("Notice title cannot be empty.");
        }

        String sql = "INSERT INTO notice (title, content, target_audience, posted_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle().trim());
            ps.setString(2, notice.getContent().trim());
            ps.setString(3, notice.getTargetAudience() != null ? notice.getTargetAudience().trim() : "ALL");
            ps.setInt(4, notice.getPostedBy() > 0 ? notice.getPostedBy() : 1);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error publishing notice: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing notice's title, content, or target audience.
     */
    public boolean updateNotice(Notice notice) throws AdminOperationException {
        if (notice == null) {
            throw new AdminOperationException("Notice cannot be null.");
        }

        String sql = "UPDATE notice SET title = ?, content = ?, target_audience = ? WHERE notice_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle().trim());
            ps.setString(2, notice.getContent().trim());
            ps.setString(3, notice.getTargetAudience() != null ? notice.getTargetAudience().trim() : "ALL");
            ps.setInt(4, notice.getNoticeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating notice: " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes a notice by ID.
     */
    public boolean deleteNotice(int noticeId) throws AdminOperationException {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, noticeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error deleting notice ID " + noticeId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Deletes a notice by Notice object.
     * Demonstrates Lesson 03 Part 02 (Method Overloading by different argument types).
     */
    public boolean deleteNotice(Notice notice) throws AdminOperationException {
        if (notice == null) return false;
        return deleteNotice(notice.getNoticeId());
    }
}
