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

/** DashboardPanel owns the UI and actions for this student portal section. */
final class DashboardPanel {
    private DashboardPanel() {}

    static JPanel createDashboardOverviewPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 20));

        JLabel lblTitle = new JLabel("Academic Progress Overview");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);

        d.lblCoursesVal = new JLabel("0", SwingConstants.CENTER);
        d.lblGpaVal = new JLabel("0.00", SwingConstants.CENTER);
        d.lblAttendanceVal = new JLabel("0%", SwingConstants.CENTER);
        d.lblMedicalsVal = new JLabel("0", SwingConstants.CENTER);

        statsGrid.add(d.createStatCard("Enrolled Courses", d.lblCoursesVal, new Color(59, 130, 246)));
        statsGrid.add(d.createStatCard("Credit-weighted GPA", d.lblGpaVal, new Color(16, 185, 129)));
        statsGrid.add(d.createStatCard("Average Attendance", d.lblAttendanceVal, new Color(139, 92, 246)));
        statsGrid.add(d.createStatCard("Approved Medicals", d.lblMedicalsVal, new Color(245, 158, 11)));

        card.add(statsGrid, BorderLayout.CENTER);
        return card;
    }

    static JPanel createStatCard(UndergraduateDashboard d, String title, JLabel lblValue, Color accent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY_BOLD);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(UITheme.TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    static void loadDashboardOverview(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Course> courses = d.studentDAO.getEnrolledCourses(stuId);
        List<Map<String, Object>> attList = d.studentDAO.getAttendanceSummary(stuId);
        List<Medical> meds = d.studentDAO.getMedicalRecords(stuId);
        List<Result> results = d.studentDAO.getResultsForStudent(stuId);

        d.lblCoursesVal.setText(String.valueOf(courses.size()));

        double totalAtt = 0.0;
        for (Map<String, Object> a : attList) {
            totalAtt += (Double) a.get("excusedPercentage");
        }
        double avgAtt = attList.isEmpty() ? 0.0 : totalAtt / attList.size();
        d.lblAttendanceVal.setText((Math.round(avgAtt * 10.0) / 10.0) + "%");

        int approvedMeds = 0;
        for (Medical m : meds) {
            if ("APPROVED".equalsIgnoreCase(m.getStatus())) approvedMeds++;
        }
        d.lblMedicalsVal.setText(String.valueOf(approvedMeds));

        double gpa = d.studentDAO.calculateGpa(stuId);
        d.lblGpaVal.setText(String.format("%.2f", gpa));
    }
}
