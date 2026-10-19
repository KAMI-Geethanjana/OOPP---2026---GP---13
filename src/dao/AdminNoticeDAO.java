package dao;

import config.DBConn;
import model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminNoticeDAO - Data Access Object for Notice Publishing and Maintenance.
 * Implements FR-ADM-03 (Create and maintain notices with target audience)
 * and FR-NOT-01 (Notices display most recent first).
 */
public class AdminNoticeDAO {

    /**
     * Retrieves all published notices ordered by most recent first.
     */
    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.notice_id, n.title, n.content, n.target_audience, n.posted_by, n.posted_date, " +
                     "u.full_name AS posted_by_name " +
                     "FROM notice n " +
                     "JOIN users u ON n.posted_by = u.user_id " +
                     "ORDER BY n.posted_date DESC";

        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Creates and publishes a new administrative notice.
     */
    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notice (title, content, target_audience, posted_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle().trim());
            ps.setString(2, notice.getContent().trim());
            ps.setString(3, notice.getTargetAudience() != null ? notice.getTargetAudience().trim() : "ALL");
            ps.setInt(4, notice.getPostedBy() > 0 ? notice.getPostedBy() : 1);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing notice's title, content, or target audience.
     */
    public boolean updateNotice(Notice notice) {
        String sql = "UPDATE notice SET title = ?, content = ?, target_audience = ? WHERE notice_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle().trim());
            ps.setString(2, notice.getContent().trim());
            ps.setString(3, notice.getTargetAudience() != null ? notice.getTargetAudience().trim() : "ALL");
            ps.setInt(4, notice.getNoticeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a notice by ID.
     */
    public boolean deleteNotice(int noticeId) {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, noticeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
