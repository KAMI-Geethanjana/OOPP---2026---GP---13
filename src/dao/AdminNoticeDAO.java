package dao;

import config.DBConn;
import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminNoticeDAO {
    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notice (title, content, target_audience, posted_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConn.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());
            ps.setString(3, notice.getTargetAudience());
            ps.setInt(4, notice.getPostedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT * FROM notice ORDER BY posted_date DESC";
        try (Connection conn = DBConn.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notices.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("target_audience"),
                        rs.getInt("posted_by"),
                        rs.getTimestamp("posted_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }
}
