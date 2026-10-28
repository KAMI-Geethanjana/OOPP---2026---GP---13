
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

/** AttendancePanel owns the UI and actions for this student portal section. */
final class AttendancePanel {
    private AttendancePanel() {}

    static JPanel createAttendancePanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Attendance Summary (80% Requirement)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Total Sessions", "Present", "Percentage", "Exam Eligibility"};
        d.attModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblAttendance = new JTable(d.attModel);
        UITheme.styleTable(d.tblAttendance);

        JScrollPane scroll = new JScrollPane(d.tblAttendance);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    static void loadAttendanceData(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Map<String, Object>> list = d.studentDAO.getAttendanceSummary(stuId);
        d.attModel.setRowCount(0);

        for (Map<String, Object> m : list) {
            boolean elig = (Boolean) m.get("isEligible");
            d.attModel.addRow(new Object[]{
                    m.get("courseCode"),
                    m.get("totalSessions"),
                    m.get("presentCount"),
                    m.get("excusedPercentage") + "%",
                    elig ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
        }
    }
}
