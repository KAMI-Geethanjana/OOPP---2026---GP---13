package common.dao;

import common.config.DBConnection;
import common.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NoticeDAO - Shared Base Data Access Object for faculty notices.
 * Accommodates both standard schema variations (created_date vs posted_date).
 */
public class NoticeDAO {

    private Notice mapResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("notice_id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        String audience = rs.getString("target_audience");

        Date date = null;
        try {
            date = rs.getDate("created_date");
        } catch (SQLException e) {
            try {
                Timestamp ts = rs.getTimestamp("posted_date");
                if (ts != null) date = new Date(ts.getTime());
            } catch (SQLException ignored) {}
        }

        String postedBy = "System";
        try {
            postedBy = rs.getString("posted_by");
        } catch (SQLException ignored) {}

        return new Notice(id, title, content, audience, date, postedBy);
    }

    /**
     * Retrieves notices filtered by target audience and sorted newest first.
     */
    public List<Notice> getNoticesForRole(String role) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice WHERE target_audience = 'ALL' OR target_audience = ? ORDER BY notice_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all notices.
     */
    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice ORDER BY notice_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
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
        // Try schema with created_date first
        String sql1 = "INSERT INTO notice (title, content, target_audience, created_date, posted_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql1)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());
            ps.setString(3, notice.getTargetAudience());
            ps.setDate(4, notice.getCreatedDate() != null ? notice.getCreatedDate() : new Date(System.currentTimeMillis()));
            ps.setString(5, notice.getPostedBy());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex1) {
            // Try fallback schema with posted_by as INT and posted_date
            String sql2 = "INSERT INTO notice (title, content, target_audience, posted_by) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql2)) {

                ps.setString(1, notice.getTitle());
                ps.setString(2, notice.getContent());
                ps.setString(3, notice.getTargetAudience());
                int pId = notice.getPostedById() > 0 ? notice.getPostedById() : 1;
                ps.setInt(4, pId);

                return ps.executeUpdate() > 0;
            } catch (SQLException ex2) {
                ex2.printStackTrace();
                return false;
            }
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
