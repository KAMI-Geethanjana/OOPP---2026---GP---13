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

/** MedicalPanel owns the UI and actions for this student portal section. */
final class MedicalPanel {
    private MedicalPanel() {}

    static JPanel createMedicalPanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Medical Requests");
        lblTitle.setFont(UITheme.FONT_HEADER);
        JLabel lblSub = new JLabel("Submit a new medical request and track its current status.");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        topHeader.add(lblTitle);
        topHeader.add(Box.createVerticalStrut(2));
        topHeader.add(lblSub);
        card.add(topHeader, BorderLayout.NORTH);

        // Center split: Form on top, Table on bottom
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 8));
        formPanel.setBackground(new Color(248, 250, 252));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblFormTitle = new JLabel("New Medical Submission");
        lblFormTitle.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(lblFormTitle, BorderLayout.NORTH);

        d.txtMedReason = new JTextArea(2, 30);
        d.txtMedReason.setFont(UITheme.FONT_BODY);
        d.txtMedReason.setBorder(BorderFactory.createTitledBorder("Describe medical reason"));
        // Reason input intentionally hidden; database requires a non-null reason.
        d.txtMedReason.setText("Medical certificate submitted");

        // Separate rows keep the Add Medical action visible on narrow windows.
        JPanel formFields = new JPanel();
        formFields.setOpaque(false);
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));

        JPanel courseDateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        courseDateRow.setOpaque(false);
        courseDateRow.add(new JLabel("Course:"));
        d.cbMedCourse = new JComboBox<>();
        d.cbMedCourse.setPreferredSize(new Dimension(160, 32));
        courseDateRow.add(d.cbMedCourse);
        courseDateRow.add(new JLabel("Medical Date (YYYY-MM-DD):"));
        d.txtMedDate = UITheme.createTextField(10);
        d.txtMedDate.setText(new Date(System.currentTimeMillis()).toString());
        courseDateRow.add(d.txtMedDate);
        courseDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(courseDateRow);

        JPanel fileRow = new JPanel(new BorderLayout(10, 0));
        fileRow.setOpaque(false);
        fileRow.add(new JLabel("Certificate Path:"), BorderLayout.WEST);
        d.txtMedPath = UITheme.createTextField(15);
        d.txtMedPath.setEditable(false);
        fileRow.add(d.txtMedPath, BorderLayout.CENTER);
        JButton btnChooseFile = UITheme.createButton("Choose File", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnChooseFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose medical certificate");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Medical certificates (PDF, JPG, JPEG, PNG)", "pdf", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                d.txtMedPath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        fileRow.add(btnChooseFile, BorderLayout.EAST);
        fileRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fileRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(fileRow);

        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionsRow.setOpaque(false);
        JButton btnAddMedical = UITheme.createButton("Add Medical", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnAddMedical.addActionListener(e -> d.handleSubmitMedical());
        actionsRow.add(btnAddMedical);
        JButton btnRefresh = UITheme.createButton("Refresh List", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> d.loadMedicalData());
        actionsRow.add(btnRefresh);
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(actionsRow);

        formPanel.add(formFields, BorderLayout.SOUTH);
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        JLabel lblTblTitle = new JLabel("My Medical Records");
        lblTblTitle.setFont(UITheme.FONT_BODY_BOLD);
        lblTblTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        tableWrapper.add(lblTblTitle, BorderLayout.NORTH);

        String[] cols = {"Record ID", "Course", "Date", "Description / Reason", "Document", "Status"};
        d.medModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        d.tblMedical = new JTable(d.medModel);
        UITheme.styleTable(d.tblMedical);
        tableWrapper.add(new JScrollPane(d.tblMedical), BorderLayout.CENTER);

        JButton btnDelete = UITheme.createButton("Delete Selected Pending Request", new Color(185, 48, 48), Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedMedical(d));
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActions.setOpaque(false);
        tableActions.add(btnDelete);
        tableWrapper.add(tableActions, BorderLayout.SOUTH);


        centerPanel.add(tableWrapper, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    static void loadMedicalData(UndergraduateDashboard d) {
        int stuId = d.student != null ? d.student.getStudentId() : 1;
        List<Medical> list = d.studentDAO.getMedicalRecords(stuId);
        d.medModel.setRowCount(0);

        for (Medical m : list) {
            d.medModel.addRow(new Object[]{
                    m.getMedicalId(),
                    m.getCourseCode(),
                    m.getMedicalDate(),
                    m.getReason(),
                    m.getDocumentPath(),
                    m.getStatus()
            });
        }

        // Populate course dropdown for submission
        List<Course> courses = d.studentDAO.getEnrolledCourses(stuId);
        d.cbMedCourse.removeAllItems();
        for (Course c : courses) {
            d.cbMedCourse.addItem(c.getCourseCode());
        }
    }

    static void handleSubmitMedical(UndergraduateDashboard d) {
        String course = (String) d.cbMedCourse.getSelectedItem();
        String reason = "Medical certificate submitted";
        String path = d.txtMedPath.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(d, "Please describe the medical reason!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (course == null) {
            JOptionPane.showMessageDialog(d, "No enrolled course selected.");
            return;
        }
        if (path.isBlank() || !java.nio.file.Files.isRegularFile(java.nio.file.Paths.get(path))) {
            JOptionPane.showMessageDialog(d, "Choose an existing medical certificate first.");
            return;
        }
        try {
            Date dt = Date.valueOf(d.txtMedDate.getText().trim());
            int stuId = d.student != null ? d.student.getStudentId() : 1;

            java.nio.file.Path source = java.nio.file.Paths.get(path);
            java.nio.file.Path folder = java.nio.file.Paths.get("medical_certificates");
            java.nio.file.Files.createDirectories(folder);
            String fileName = source.getFileName().toString();
            String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase(java.util.Locale.ROOT);
            java.nio.file.Path target = folder.resolve("student_" + stuId + "_" +
                    java.util.UUID.randomUUID() + ext);
            java.nio.file.Files.copy(source, target);
            Medical m = new Medical(0, stuId, course, dt, reason, target.toString(), "PENDING", null);
            if (d.studentDAO.submitMedical(m)) {
                JOptionPane.showMessageDialog(d, "Medical request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                d.txtMedReason.setText("");
                d.txtMedPath.setText("");
                d.loadMedicalData();
            } else {
                java.nio.file.Files.deleteIfExists(target);
                JOptionPane.showMessageDialog(d, "Could not save medical request to database.");
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(d, "Could not save certificate: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(d, "Please enter date in format YYYY-MM-DD", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        }
    }
    private static void deleteSelectedMedical(UndergraduateDashboard d) {
        int selected = d.tblMedical.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(d, "Select a medical record first.");
            return;
        }
        int row = d.tblMedical.convertRowIndexToModel(selected);
        String status = String.valueOf(d.medModel.getValueAt(row, 5));
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(d, "Only PENDING requests can be deleted.");
            return;
        }
        int medicalId = ((Number) d.medModel.getValueAt(row, 0)).intValue();
        int studentId = d.student.getStudentId();
        if (JOptionPane.showConfirmDialog(d, "Delete selected pending medical request?",
                "Confirm deletion", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        if (d.studentDAO.deletePendingMedical(medicalId, studentId)) {
            d.loadMedicalData();
            JOptionPane.showMessageDialog(d, "Medical request deleted.");
        } else {
            JOptionPane.showMessageDialog(d, "Deletion failed. The request may no longer be pending.");
        }
    }

}
