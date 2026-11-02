package common.dao;

import common.config.DBConnection;
import common.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NoticeDAO - Handles database queries for faculty notices.
 */
public class NoticeDAO {

    /**
     * Retrieves notices filtered by target audience and sorted newest first.
     */
    public List<Notice> getNoticesForRole(String role) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice WHERE target_audience = 'ALL' OR target_audience = ? ORDER BY created_date DESC, notice_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("target_audience"),
                        rs.getDate("created_date"),
                        rs.getString("posted_by")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all notices (for Admin view).
     */
    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice ORDER BY created_date DESC, notice_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("target_audience"),
                        rs.getDate("created_date"),
                        rs.getString("posted_by")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Creates a new notice.
     */
    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notice (title, content, target_audience, created_date, posted_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());
            ps.setString(3, notice.getTargetAudience());
            ps.setDate(4, notice.getCreatedDate());
            ps.setString(5, notice.getPostedBy());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a notice.
     */
    public boolean deleteNotice(int noticeId) {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noticeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
