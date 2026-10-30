
package tg2123.gui;

import tg2123.dao.MedicalDAO;
import tg2123.model.TechnicalOfficer;
import common.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MedicalPanel extends JPanel {

    private final TechnicalOfficer officer;
    private final MedicalDAO dao = new MedicalDAO();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Medical ID", "Reg. No.", "Student", "Date",
                            "Reason", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);

    public MedicalPanel(TechnicalOfficer officer) {
        this.officer = officer;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.CONTENT_BG);

        UITheme.styleTable(table);

        JButton addButton = new JButton("Add Medical");
        JButton refreshButton = new JButton("Refresh Requests");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        addButton.addActionListener(e -> openAddMedicalForm());
        refreshButton.addActionListener(e -> loadRequests());
        approveButton.addActionListener(e -> updateStatus("APPROVED"));
        rejectButton.addActionListener(e -> updateStatus("REJECTED"));

        JPanel buttons = new JPanel(new FlowLayout());

        buttons.add(addButton);
        buttons.add(refreshButton);
        buttons.add(approveButton);
        buttons.add(rejectButton);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadRequests();
    }

    private void loadRequests() {
        try {
            List<String[]> requests =
                    dao.getMedicalRequestsForDepartment(
                            officer.getDepartmentId());

            tableModel.setRowCount(0);

            for (String[] request : requests) {
                tableModel.addRow(request);
            }

        } catch (SQLException ex) {
            showMessage("Could not load medical requests: "
                    + ex.getMessage());
        }
    }

    private void openAddMedicalForm() {

        JComboBox<StudentOption> studentCombo = new JComboBox<>();
        JComboBox<String> courseCombo = new JComboBox<>();

        JTextField dateField =
                new JTextField(LocalDate.now().toString(), 15);

        JTextArea reasonArea = new JTextArea(4, 15);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        try {
            List<String[]> students =
                    dao.getStudentsForDepartment(
                            officer.getDepartmentId());

            for (String[] student : students) {
                studentCombo.addItem(
                        new StudentOption(
                                Integer.parseInt(student[0]),
                                student[1],
                                student[2]
                        )
                );
            }

        } catch (SQLException ex) {
            showMessage("Could not load students: " + ex.getMessage());
            return;
        }

        if (studentCombo.getItemCount() == 0) {
            showMessage("No students found in your department.");
            return;
        }

        studentCombo.addActionListener(e -> {
            StudentOption selected =
                    (StudentOption) studentCombo.getSelectedItem();

            courseCombo.removeAllItems();

            if (selected == null) {
                return;
            }

            try {
                List<String> courses =
                        dao.getCoursesForStudent(selected.id);

                for (String course : courses) {
                    courseCombo.addItem(course);
                }

            } catch (SQLException ex) {
                showMessage("Could not load courses: "
                        + ex.getMessage());
            }
        });

        studentCombo.setSelectedIndex(0);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Student:"));
        form.add(studentCombo);

        form.add(new JLabel("Course:"));
        form.add(courseCombo);

        form.add(new JLabel("Medical Date (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Reason:"));
        form.add(new JScrollPane(reasonArea));

        int answer = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Medical Request",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        StudentOption selectedStudent =
                (StudentOption) studentCombo.getSelectedItem();

        String selectedCourse =
                (String) courseCombo.getSelectedItem();

        String dateText = dateField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (selectedStudent == null) {
            showMessage("Please select a student.");
            return;
        }

        if (selectedCourse == null) {
            showMessage("Please select a course.");
            return;
        }

        if (reason.isEmpty()) {
            showMessage("Please enter the medical reason.");
            return;
        }

        try {
            Date medicalDate = Date.valueOf(dateText);

            boolean added = dao.addMedicalRequest(
                    selectedStudent.id,
                    selectedCourse,
                    medicalDate,
                    reason
            );

            if (added) {
                showMessage("Medical request added successfully.");
                loadRequests();
            } else {
                showMessage("Could not add medical request.");
            }

        } catch (IllegalArgumentException ex) {
            showMessage("Invalid date. Please use YYYY-MM-DD.");
        } catch (SQLException ex) {
            showMessage("Could not add medical request: "
                    + ex.getMessage());
        }
    }

    private void updateStatus(String status) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            showMessage("Select a medical request first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Change the selected request to " + status + "?",
                "Confirm Status Change",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int medicalId = Integer.parseInt(
                    tableModel.getValueAt(selectedRow, 0).toString());

            if (dao.updateMedicalStatus(
                    medicalId,
                    status,
                    officer.getDepartmentId(),
                    officer.getToId())) {

                showMessage("Medical request updated.");
                loadRequests();

            } else {
                showMessage("No request was updated.");
            }

        } catch (SQLException ex) {
            showMessage("Could not update request: "
                    + ex.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private static class StudentOption {

        private final int id;
        private final String regNo;
        private final String fullName;

        public StudentOption(int id, String regNo, String fullName) {
            this.id = id;
            this.regNo = regNo;
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return regNo + " - " + fullName;
        }
    }
}
