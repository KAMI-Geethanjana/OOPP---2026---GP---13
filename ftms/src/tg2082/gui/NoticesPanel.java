package tg2082.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.model.Course;
import common.model.Medical;
import common.model.Notice;
import common.model.Result;
import common.model.Timetable;
import common.util.UITheme;
import common.util.UserSession;
import tg2082.dao.UndergraduateDAO;
import tg2082.model.Undergraduate;
import tg2088.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/** NoticesPanel owns the UI and actions for this student portal section. */
final class NoticesPanel {
    private NoticesPanel() {}

    static JPanel createNoticesPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Faculty Notices & Announcements");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Notice ID", "Title", "Content", "Date", "Posted By"};
        d.noticesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblNotices = new JTable(d.noticesModel);
        UITheme.styleTable(d.tblNotices);
        card.add(new JScrollPane(d.tblNotices), BorderLayout.CENTER);

        return card;
    }

    static void loadNoticesData(UndergraduateDashboard d) {
        List<Notice> list = d.noticeDAO.getNoticesForRole("UNDERGRADUATE");
        d.noticesModel.setRowCount(0);

        for (Notice n : list) {
            d.noticesModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    n.getContent(),
                    n.getCreatedDate(),
                    n.getPostedBy()
            });
        }
    }
}
