package tg2086.dao;

import common.config.DBConnection;
import common.dao.NoticeDAO;
import common.model.Notice;
import tg2086.exception.AdminOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminNoticeDAO - Data Access Object for Notice Publishing and Maintenance.
 * Extends the shared common.dao.NoticeDAO to provide full administrative controls.
 * - Inheritance (extends common.dao.NoticeDAO)
 * - JDBC PreparedStatements & ResultSet traversal
 * - Method Overloading (getAllNotices with/without audience, deleteNotice by int vs object)
 * - Exception Handling (throws AdminOperationException)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminNoticeDAO extends NoticeDAO {

    /**
     * Retrieves all published notices ordered by most recent first.
     */
    public List<Notice> getAllAdminNotices() throws AdminOperationException {
        return getAllAdminNotices(null);
    }

    /**
     * Method Overloading: Retrieves notices optionally filtered by target audience.
     */
    public List<Notice> getAllAdminNotices(String targetAudience) throws AdminOperationException {
        List<Notice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT n.notice_id, n.title, n.content, n.target_audience, n.posted_by, n.created_date, " +
                "COALESCE(u.full_name, n.posted_by, 'System Admin') AS posted_by_name " +
                "FROM notice n " +
                "LEFT JOIN users u ON (n.posted_by = CAST(u.user_id AS CHAR) OR n.posted_by = u.username) "
        );

        if (targetAudience != null && !targetAudience.trim().isEmpty() && !"ALL".equalsIgnoreCase(targetAudience.trim())) {
            sql.append("WHERE n.target_audience = ? OR n.target_audience = 'ALL' ");
        }
        sql.append("ORDER BY n.created_date DESC, n.notice_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (targetAudience != null && !targetAudience.trim().isEmpty() && !"ALL".equalsIgnoreCase(targetAudience.trim())) {
                ps.setString(1, targetAudience.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date createdDate = rs.getDate("created_date");
                    Notice notice = new Notice(
                            rs.getInt("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("target_audience"),
                            createdDate,
                            rs.getString("posted_by_name")
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
     * Publishes a new notice to the faculty portal.
     */
    public boolean publishNotice(String title, String content, String audience, int postedByUserId)
            throws AdminOperationException {
        String sql = "INSERT INTO notice (title, content, target_audience, created_date, posted_by) VALUES (?, ?, ?, CURRENT_DATE, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setString(2, content.trim());
            ps.setString(3, (audience != null && !audience.isEmpty()) ? audience.trim().toUpperCase() : "ALL");
            ps.setString(4, postedByUserId > 0 ? "Admin" : "System Admin");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error publishing notice: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing notice's title, body content, and target audience.
     */
    public boolean updateNotice(int noticeId, String title, String content, String audience)
            throws AdminOperationException {
        String sql = "UPDATE notice SET title = ?, content = ?, target_audience = ? WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setString(2, content.trim());
            ps.setString(3, (audience != null && !audience.isEmpty()) ? audience.trim().toUpperCase() : "ALL");
            ps.setInt(4, noticeId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AdminOperationException("Database error updating notice ID " + noticeId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Delete notice by ID.
     */
    public boolean deleteNoticeById(int noticeId) throws AdminOperationException {
        try {
            return super.deleteNotice(noticeId);
        } catch (Exception e) {
            throw new AdminOperationException("Database error deleting notice: " + e.getMessage(), e);
        }
    }

    /**
     * Method Overloading: Delete notice by passing a Notice object.
     */
    public boolean deleteNotice(Notice notice) throws AdminOperationException {
        if (notice == null) {
            throw new AdminOperationException("Cannot delete a null notice reference.");
        }
        return deleteNoticeById(notice.getNoticeId());
    }
}
