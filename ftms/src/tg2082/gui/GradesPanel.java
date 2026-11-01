package tg2082.gui;

import common.model.Course;
import common.model.Medical;
import common.model.Result;
import common.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Student grades from lecturer-entered results, with approved medical display rules. */
final class GradesPanel {
    private GradesPanel() {}

    static JPanel createGradesPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel title = new JLabel("My Grades & GPA");
        title.setFont(UITheme.FONT_HEADER);
        card.add(title, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Name", "Grade"};
        d.gradesModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        d.tblGrades = new JTable(d.gradesModel);
        UITheme.styleTable(d.tblGrades);
        card.add(new JScrollPane(d.tblGrades), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));
        d.lblSgpa = new JLabel("Overall GPA: Not Available");
        d.lblSgpa.setFont(UITheme.FONT_BODY_BOLD);
        d.lblSgpa.setForeground(UITheme.PRIMARY_BLUE);
        footer.add(d.lblSgpa);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    static void loadGradesData(UndergraduateDashboard d) {
        if (d.student == null) {
            d.gradesModel.setRowCount(0);
            d.lblSgpa.setText("Overall GPA: Not Available");
            return;
        }

        int studentId = d.student.getStudentId();
        List<Result> results = d.studentDAO.getResultsForStudent(studentId);
        List<Course> courses = d.studentDAO.getEnrolledCourses(studentId);
        List<Medical> medicals = d.studentDAO.getMedicalRecords(studentId);

        Map<String, Result> resultsByCourse = new HashMap<>();
        for (Result result : results) {
            resultsByCourse.put(result.getCourseCode(), result);
        }

        Set<String> approvedMedicalCourses = new HashSet<>();
        for (Medical medical : medicals) {
            if ("APPROVED".equalsIgnoreCase(medical.getStatus())
                    && medical.getCourseCode() != null) {
                approvedMedicalCourses.add(medical.getCourseCode());
            }
        }

        d.gradesModel.setRowCount(0);
        boolean hasApprovedMedical = false;
        for (Course course : courses) {
            String code = course.getCourseCode();
            Result result = resultsByCourse.get(code);
            boolean approved = approvedMedicalCourses.contains(code);
            hasApprovedMedical |= approved;

            String grade = result == null ? null : result.getGrade();
            if (approved) {
                grade = "MC";
            } else if (grade == null || grade.isBlank() || "-".equals(grade)
                    || "N/A".equalsIgnoreCase(grade)) {
                grade = "Not Released";
            }

            d.gradesModel.addRow(new Object[]{code, course.getCourseTitle(), grade});
        }

        if (hasApprovedMedical) {
            d.lblSgpa.setText("Overall GPA: WH");
        } else {
            double gpa = d.studentDAO.calculateGpa(studentId);
            d.lblSgpa.setText(String.format(java.util.Locale.US, "Overall GPA: %.2f", gpa));
        }
    }
}
