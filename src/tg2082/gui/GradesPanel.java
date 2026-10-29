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

/** GradesPanel owns the UI and actions for this student portal section. */
final class GradesPanel {
    private GradesPanel() {}

    static JPanel createGradesPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Academic Grades & GPA (UGC Circular 12-2024)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        top.add(lblTitle, BorderLayout.WEST);

        d.lblSgpa = new JLabel("Credit-weighted GPA: 0.00");
        d.lblSgpa.setFont(UITheme.FONT_BODY_BOLD);
        d.lblSgpa.setForeground(UITheme.PRIMARY_BLUE);
        top.add(d.lblSgpa, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Attempt", "Grade", "Grade Point (GPV)"};
        d.gradesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblGrades = new JTable(d.gradesModel);
        UITheme.styleTable(d.tblGrades);
        card.add(new JScrollPane(d.tblGrades), BorderLayout.CENTER);

        return card;
    }

    static void loadGradesData(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Result> list = d.studentDAO.getResultsForStudent(stuId);
        d.gradesModel.setRowCount(0);

        for (Result r : list) {
            d.gradesModel.addRow(new Object[]{
                    r.getCourseCode(), r.getAttemptType(),
                    r.getGrade(), r.getGradePoint()
            });
        }
        double gpa = d.studentDAO.calculateGpa(stuId);
        d.lblSgpa.setText(String.format("Credit-weighted GPA: %.2f", gpa));
    }
}
