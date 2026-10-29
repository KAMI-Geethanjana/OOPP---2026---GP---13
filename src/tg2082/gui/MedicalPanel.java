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
        formPanel.add(new JScrollPane(d.txtMedReason), BorderLayout.CENTER);

        JPanel rowFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rowFields.setOpaque(false);
        rowFields.add(new JLabel("Course:"));
        d.cbMedCourse = new JComboBox<>();
        d.cbMedCourse.setPreferredSize(new Dimension(120, 32));
        rowFields.add(d.cbMedCourse);

        rowFields.add(new JLabel("Medical Date:"));
        d.txtMedDate = UITheme.createTextField(10);
        d.txtMedDate.setText(new Date(System.currentTimeMillis()).toString());
        rowFields.add(d.txtMedDate);

        rowFields.add(new JLabel("Certificate Path:"));
        d.txtMedPath = UITheme.createTextField(15);
        d.txtMedPath.setText("docs/med_cert.pdf");
        rowFields.add(d.txtMedPath);

        JButton btnSubmit = UITheme.createButton("Submit Request", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnSubmit.addActionListener(e -> d.handleSubmitMedical());
        rowFields.add(btnSubmit);

        JButton btnRefresh = UITheme.createButton("Refresh List", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> d.loadMedicalData());
        rowFields.add(btnRefresh);

        formPanel.add(rowFields, BorderLayout.SOUTH);
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
        String reason = d.txtMedReason.getText().trim();
        String path = d.txtMedPath.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(d, "Please describe the medical reason!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date dt = Date.valueOf(d.txtMedDate.getText().trim());
            int stuId = d.student != null ? d.student.getStudentId() : 1;

            Medical m = new Medical(0, stuId, course, dt, reason, path, "PENDING", null);
            if (d.studentDAO.submitMedical(m)) {
                JOptionPane.showMessageDialog(d, "Medical request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                d.txtMedReason.setText("");
                d.loadMedicalData();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(d, "Please enter date in format YYYY-MM-DD", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        }
    }
}
