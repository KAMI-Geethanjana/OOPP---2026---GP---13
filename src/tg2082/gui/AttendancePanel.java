package tg2082.gui;

import common.model.Course;
import common.util.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/** Per-course daily attendance view for the undergraduate. */
final class AttendancePanel {
    private AttendancePanel() {}

    static JPanel createAttendancePanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));
        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setOpaque(false);
        JLabel title = new JLabel("My Attendance");
        title.setFont(UITheme.FONT_HEADER);
        header.add(title, BorderLayout.NORTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filters.setOpaque(false);
        filters.add(new JLabel("Select Course:"));
        d.cbAttendanceCourse = new JComboBox<>();
        d.cbAttendanceCourse.setPreferredSize(new Dimension(250, 34));
        filters.add(d.cbAttendanceCourse);
        d.lblAttendanceTotals = new JLabel("Present: 0    Absent: 0");
        d.lblAttendanceTotals.setFont(UITheme.FONT_BODY_BOLD);
        filters.add(d.lblAttendanceTotals);
        header.add(filters, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        d.attModel = new DefaultTableModel(new String[]{"Session Date", "Session Type", "Attendance Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        d.tblAttendance = new JTable(d.attModel);
        UITheme.styleTable(d.tblAttendance);
        card.add(new JScrollPane(d.tblAttendance), BorderLayout.CENTER);
        d.cbAttendanceCourse.addActionListener(e -> showSelectedCourse(d));
        return card;
    }

    static void loadAttendanceData(UndergraduateDashboard d) {
        if (d.cbAttendanceCourse == null) return;
        String previous = (String) d.cbAttendanceCourse.getSelectedItem();
        d.cbAttendanceCourse.removeAllItems();
        if (d.student == null) { showSelectedCourse(d); return; }
        List<Course> courses = d.studentDAO.getEnrolledCourses(d.student.getStudentId());
        for (Course c : courses) d.cbAttendanceCourse.addItem(c.getCourseCode());
        if (previous != null) d.cbAttendanceCourse.setSelectedItem(previous);
        showSelectedCourse(d);
    }

    private static void showSelectedCourse(UndergraduateDashboard d) {
        if (d.attModel == null || d.lblAttendanceTotals == null) return;
        d.attModel.setRowCount(0);
        String course = d.cbAttendanceCourse == null ? null : (String) d.cbAttendanceCourse.getSelectedItem();
        if (course == null || d.student == null) {
            d.lblAttendanceTotals.setText("Present: 0    Absent: 0");
            return;
        }
        List<Map<String, Object>> rows = d.studentDAO.getAttendanceByCourse(d.student.getStudentId(), course);
        int present = 0, absent = 0;
        for (Map<String, Object> row : rows) {
            String status = String.valueOf(row.get("status"));
            if ("PRESENT".equalsIgnoreCase(status)) present++;
            else if ("ABSENT".equalsIgnoreCase(status)) absent++;
            d.attModel.addRow(new Object[]{row.get("date"), row.get("type"), status});
        }
        d.lblAttendanceTotals.setText("Present: " + present + "    Absent: " + absent + "    Total: " + rows.size());
    }
}
