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

/** CoursesPanel owns the UI and actions for this student portal section. */
final class CoursesPanel {
    private CoursesPanel() {}

    static JPanel createCoursesPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(2, 1, 0, 15));

        // Upper: Enrolled Courses
        JPanel pnlEnrolled = new JPanel(new BorderLayout(0, 8));
        pnlEnrolled.setOpaque(false);
        JLabel lblEnrolled = new JLabel("My Enrolled Course Modules");
        lblEnrolled.setFont(UITheme.FONT_HEADER);
        pnlEnrolled.add(lblEnrolled, BorderLayout.NORTH);

        String[] cols1 = {"Course Code", "Course Title", "Theory Credits", "Practical Credits", "Total Credits", "Lecturer"};
        d.coursesModel = new DefaultTableModel(cols1, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblCourses = new JTable(d.coursesModel);
        UITheme.styleTable(d.tblCourses);
        pnlEnrolled.add(new JScrollPane(d.tblCourses), BorderLayout.CENTER);
        card.add(pnlEnrolled);

        // Lower: Lecture Materials
        JPanel pnlMaterials = new JPanel(new BorderLayout(0, 8));
        pnlMaterials.setOpaque(false);
        JLabel lblMats = new JLabel("Course Materials & Lecture Notes");
        lblMats.setFont(UITheme.FONT_HEADER);
        pnlMaterials.add(lblMats, BorderLayout.NORTH);

        String[] cols2 = {"Material ID", "Course Code", "Title", "File / Resource Path", "Uploaded Date"};
        d.materialsModel = new DefaultTableModel(cols2, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblMaterials = new JTable(d.materialsModel);
        UITheme.styleTable(d.tblMaterials);
        pnlMaterials.add(new JScrollPane(d.tblMaterials), BorderLayout.CENTER);
        card.add(pnlMaterials);

        return card;
    }

    static void loadCoursesData(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Course> list = d.studentDAO.getEnrolledCourses(stuId);
        d.coursesModel.setRowCount(0);

        for (Course c : list) {
            d.coursesModel.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getCourseTitle(),
                    c.getCreditTheory(),
                    c.getCreditPractical(),
                    c.getTotalCredits(),
                    c.getLecturerName() != null ? c.getLecturerName() : "TBA"
            });
        }

        List<CourseMaterial> mats = d.studentDAO.getCourseMaterialsForStudent(stuId);
        d.materialsModel.setRowCount(0);
        for (CourseMaterial m : mats) {
            d.materialsModel.addRow(new Object[]{
                    m.getMaterialId(),
                    m.getCourseCode(),
                    m.getTitle(),
                    m.getFilePath(),
                    m.getUploadedDate()
            });
        }
    }
}
