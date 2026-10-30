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

/** TimetablePanel owns the UI and actions for this student portal section. */
final class TimetablePanel {
    private TimetablePanel() {}

    static JPanel createTimetablePanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("My Timetable");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Day", "Start", "End", "Course", "Venue"};
        d.timetableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblTimetable = new JTable(d.timetableModel);
        UITheme.styleTable(d.tblTimetable);
        card.add(new JScrollPane(d.tblTimetable), BorderLayout.CENTER);

        return card;
    }

    static void loadTimetableData(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Timetable> list = d.studentDAO.getTimetableForStudent(stuId);
        d.timetableModel.setRowCount(0);

        for (Timetable t : list) {
            d.timetableModel.addRow(new Object[]{
                    t.getDayOfWeek(),
                    t.getStartTime(),
                    t.getEndTime(),
                    t.getCourseCode(),
                    t.getVenue()
            });
        }
    }
}
