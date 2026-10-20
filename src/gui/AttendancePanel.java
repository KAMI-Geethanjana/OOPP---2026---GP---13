
package gui;

import dao.AttendanceDAO;
import model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final TechnicalOfficer officer;
    private final AttendanceDAO dao = new AttendanceDAO();

    private final JTextField courseField = new JTextField(12);
    private final JTextField dateField = new JTextField(10);

    private final JComboBox<String> sessionBox =
            new JComboBox<>(new String[]{"THEORY", "PRACTICAL"});

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Student ID", "Student Name", "Status"}, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2;
                }
            };

    private final JTable table = new JTable(tableModel);

    public AttendancePanel(TechnicalOfficer officer) {

        this.officer = officer;

        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));

        form.add(new JLabel("Course Code:"));
        form.add(courseField);

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Session:"));
        form.add(sessionBox);

        JButton loadButton = new JButton("Load Students");
        JButton saveButton = new JButton("Save Attendance");

        loadButton.addActionListener(e -> loadStudents());
        saveButton.addActionListener(e -> saveAttendance());

        form.add(loadButton);
        form.add(saveButton);

        table.getColumnModel().getColumn(2).setCellEditor(
                new DefaultCellEditor(
                        new JComboBox<>(
                                new String[]{"PRESENT", "ABSENT"})));

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private Date getSessionDate() {
        return Date.valueOf(dateField.getText().trim());
    }

    private void loadStudents() {

        String course = courseField.getText().trim();

        if (course.isEmpty() || dateField.getText().trim().isEmpty()) {
            showMessage("Enter the course code and session date.");
            return;
        }

        try {
            Date date = getSessionDate();
            String session = (String) sessionBox.getSelectedItem();

            List<String[]> students =
                    dao.getStudentsForAttendance(course, date, session);

            tableModel.setRowCount(0);

            for (String[] student : students) {
                tableModel.addRow(student);
            }

            if (students.isEmpty()) {
                showMessage(
                        "No enrolled students were found for this course.");
            }

        } catch (IllegalArgumentException ex) {
            showMessage(
                    "Invalid date. Use YYYY-MM-DD, for example 2026-02-02.");
        } catch (SQLException ex) {
            showMessage("Could not load students: " + ex.getMessage());
        }
    }

    private void saveAttendance() {

        if (table.isEditing()
                && !table.getCellEditor().stopCellEditing()) {
            showMessage("Please select a valid attendance status.");
            return;
        }

        String course = courseField.getText().trim();

        if (course.isEmpty() || tableModel.getRowCount() == 0) {
            showMessage("Load students before saving attendance.");
            return;
        }

        if (officer == null || officer.getToId() <= 0) {
            showMessage(
                    "Invalid Technical Officer account. Please log in again.");
            return;
        }

        try {
            Date date = getSessionDate();
            String session = (String) sessionBox.getSelectedItem();

            int saved = 0;

            for (int i = 0; i < tableModel.getRowCount(); i++) {

                int studentId = Integer.parseInt(
                        tableModel.getValueAt(i, 0).toString().trim());

                Object statusValue = tableModel.getValueAt(i, 2);

                if (statusValue == null) {
                    showMessage(
                            "Select PRESENT or ABSENT for every student.");
                    return;
                }

                String status = statusValue.toString();

                if (!"PRESENT".equals(status)
                        && !"ABSENT".equals(status)) {
                    showMessage(
                            "Select PRESENT or ABSENT for student "
                                    + studentId + ".");
                    return;
                }
            }

            // Save after validating all statuses.
            for (int i = 0; i < tableModel.getRowCount(); i++) {

                int studentId = Integer.parseInt(
                        tableModel.getValueAt(i, 0).toString().trim());

                String status =
                        tableModel.getValueAt(i, 2).toString();

                System.out.println(
                        "Technical Officer ID: " + officer.getToId());
                System.out.println("Student ID: " + studentId);
                System.out.println("Course Code: " + course);

                if (dao.saveAttendance(
                        studentId,
                        course,
                        date,
                        session,
                        status,
                        officer.getToId())) {

                    saved++;
                }
            }

            showMessage(
                    "Attendance saved successfully for "
                            + saved + " student(s).");

        } catch (IllegalArgumentException ex) {
            showMessage(
                    "Invalid date or student ID. Check your entries.");
        } catch (SQLException ex) {
            showMessage("Could not save attendance: " + ex.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
