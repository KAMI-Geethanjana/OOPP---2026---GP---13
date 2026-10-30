package tg2123.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.model.Course;
import common.model.Medical;
import common.model.Notice;
import common.model.Timetable;
import common.util.UITheme;
import common.util.UserSession;
import tg2123.dao.TechnicalOfficerDAO;
import tg2123.dao.TechnicalOfficerRepository;
import tg2123.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * TechnicalOfficerDashboard - Portal for Technical Officers.
 * Features:
 * 1. Mark undergraduate session attendance (Theory/Practical)
 * 2. Review and approve/reject medical certificate requests
 * 3. View Departmental Timetable (restricted to own department per FR-TO-05)
 * 4. View notices and update profile
 *
 * Authored under: Module TG2123 (B. G. N. M. Weerawardhana)
 */
public class TechnicalOfficerDashboard extends JFrame {

    private TechnicalOfficer techOfficer;
    private TechnicalOfficerRepository toDAO;
    private NoticeDAO noticeDAO;

    private JPanel contentCards;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton btnNavAttendance;
    private JButton btnNavMedicals;
    private JButton btnNavTimetable;
    private JButton btnNavNotices;
    private JButton btnNavProfile;

    // Attendance components
    private JComboBox<String> cbAttCourse;
    private JTextField txtAttDate;
    private JComboBox<String> cbAttType;
    private JTable tblAttendance;
    private DefaultTableModel attTableModel;

    // Medicals components
    private JTable tblMedicals;
    private DefaultTableModel medicalsTableModel;

    // Timetable components
    private JTable tblTimetable;
    private DefaultTableModel timetableModel;

    // Notices components
    private JTable tblNotices;
    private DefaultTableModel noticesModel;

    // Profile components
    private JTextField txtProfFullName, txtProfEmail, txtProfPhone;
    private JLabel lblProfUsername, lblProfEmpId;

    public TechnicalOfficerDashboard(TechnicalOfficer techOfficer) {
        this.techOfficer = techOfficer;
        this.toDAO = new TechnicalOfficerDAO();
        this.noticeDAO = new NoticeDAO();

        if (techOfficer != null && techOfficer.getDepartmentName() == null) {
            TechnicalOfficer fullTo = toDAO.getTechnicalOfficerByUserId(techOfficer.getUserId());
            if (fullTo != null) {
                this.techOfficer = fullTo;
            }
        }

        initUI();
    }

    private void initUI() {
        setTitle("Faculty of Technology Management System - Technical Officer Portal");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.CONTENT_BG);
        mainArea.add(createTopHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(UITheme.CONTENT_BG);
        contentCards.setBorder(new EmptyBorder(15, 25, 20, 25));

        contentCards.add(createAttendancePanel(), "ATTENDANCE");
        contentCards.add(createMedicalsPanel(), "MEDICALS");
        contentCards.add(createTimetablePanel(), "TIMETABLE");
        contentCards.add(createNoticesPanel(), "NOTICES");
        contentCards.add(createProfilePanel(), "PROFILE");

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Initial load
        loadDepartmentCourses();
        loadAttendanceData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblLogo = new JLabel("  FTMS PORTAL", SwingConstants.LEFT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(0, 24, 5, 0));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("  Technical Officer Unit", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UITheme.SIDEBAR_TEXT);
        lblSub.setBorder(new EmptyBorder(0, 24, 25, 0));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSub);

        btnNavAttendance = UITheme.createSidebarButton("Mark Attendance", true);
        btnNavMedicals = UITheme.createSidebarButton("Medical Requests", false);
        btnNavTimetable = UITheme.createSidebarButton("Dept Timetable", false);
        btnNavNotices = UITheme.createSidebarButton("Notices", false);
        btnNavProfile = UITheme.createSidebarButton("My Profile", false);

        btnNavAttendance.addActionListener(e -> switchCard("ATTENDANCE", btnNavAttendance));
        btnNavMedicals.addActionListener(e -> { switchCard("MEDICALS", btnNavMedicals); loadMedicalsData(); });
        btnNavTimetable.addActionListener(e -> { switchCard("TIMETABLE", btnNavTimetable); loadTimetableData(); });
        btnNavNotices.addActionListener(e -> { switchCard("NOTICES", btnNavNotices); loadNoticesData(); });
        btnNavProfile.addActionListener(e -> { switchCard("PROFILE", btnNavProfile); loadProfileData(); });

        sidebar.add(btnNavAttendance);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavMedicals);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavTimetable);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavNotices);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavProfile);

        sidebar.add(Box.createVerticalGlue());

        JButton btnSignOut = UITheme.createButton("Sign Out", new Color(185, 28, 28), Color.WHITE);
        btnSignOut.setMaximumSize(new Dimension(200, 38));
        btnSignOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignOut.addActionListener(e -> handleSignOut());

        JPanel signOutWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signOutWrapper.setOpaque(false);
        signOutWrapper.add(btnSignOut);
        sidebar.add(signOutWrapper);

        return sidebar;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CONTENT_BG);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        String welcomeName = techOfficer != null ? techOfficer.getFullName() : "Technical Officer";
        JLabel lblWelcome = new JLabel("Welcome, " + welcomeName);
        lblWelcome.setFont(UITheme.FONT_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        String deptStr = techOfficer != null && techOfficer.getDepartmentName() != null ? techOfficer.getDepartmentName() : "Faculty of Technology";
        JLabel lblSub = new JLabel(deptStr + " | Module TG2123 (Attendance & Medicals)");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(lblWelcome);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private void switchCard(String name, JButton activeBtn) {
        cardLayout.show(contentCards, name);
        JButton[] buttons = {btnNavAttendance, btnNavMedicals, btnNavTimetable, btnNavNotices, btnNavProfile};
        for (JButton b : buttons) {
            boolean isActive = (b == activeBtn);
            b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
            b.setForeground(isActive ? UITheme.SIDEBAR_TEXT_ACTIVE : UITheme.SIDEBAR_TEXT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. ATTENDANCE PANEL
    // ==========================================
    private JPanel createAttendancePanel() {

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        // Top section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        JLabel lblTitle = new JLabel(
                "Record Undergraduate Attendance (FR-TO-01)"
        );

        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(lblTitle);
        topSection.add(Box.createVerticalStrut(15));

        // Attendance input fields
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        inputPanel.setOpaque(false);

        inputPanel.add(new JLabel("Course:"));

        cbAttCourse = new JComboBox<>();
        cbAttCourse.setPreferredSize(new Dimension(130, 34));
        cbAttCourse.addActionListener(e -> loadAttendanceData());

        inputPanel.add(cbAttCourse);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));

        txtAttDate = UITheme.createTextField(10);
        txtAttDate.setText(
                new Date(System.currentTimeMillis()).toString()
        );

        inputPanel.add(txtAttDate);

        inputPanel.add(new JLabel("Session Type:"));

        cbAttType = new JComboBox<>(
                new String[]{"THEORY", "PRACTICAL"}
        );

        cbAttType.setPreferredSize(new Dimension(120, 34));

        inputPanel.add(cbAttType);

        JButton btnLoad = UITheme.createButton(
                "Load Students",
                UITheme.PRIMARY_BLUE,
                Color.WHITE
        );

        btnLoad.addActionListener(e -> loadAttendanceData());

        inputPanel.add(btnLoad);

        topSection.add(inputPanel);

        card.add(topSection, BorderLayout.NORTH);

        // Attendance table
        String[] cols = {
                "Student ID",
                "Student Reg No",
                "Full Name",
                "Status (PRESENT / ABSENT)"
        };

        attTableModel = new DefaultTableModel(cols, 0) {

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3;
            }
        };

        tblAttendance = new JTable(attTableModel);

        UITheme.styleTable(tblAttendance);

        JComboBox<String> statusEditor = new JComboBox<>(
                new String[]{"NOT_MARKED", "PRESENT", "ABSENT"}
        );

        tblAttendance.getColumnModel()
                .getColumn(3)
                .setCellEditor(new DefaultCellEditor(statusEditor));

        card.add(new JScrollPane(tblAttendance), BorderLayout.CENTER);

        // Bottom action buttons
        JPanel bottomBar = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );

        bottomBar.setOpaque(false);

        JButton btnMarkAllPresent = UITheme.createButton(
                "Set All PRESENT",
                UITheme.PRIMARY_BLUE,
                Color.WHITE
        );

        btnMarkAllPresent.addActionListener(
                e -> setAllStatus("PRESENT")
        );

        bottomBar.add(btnMarkAllPresent);

        JButton btnMarkAllAbsent = UITheme.createButton(
                "Set All ABSENT",
                UITheme.WARNING_AMBER,
                Color.WHITE
        );

        btnMarkAllAbsent.addActionListener(
                e -> setAllStatus("ABSENT")
        );

        bottomBar.add(btnMarkAllAbsent);

        JButton btnSaveAttendance = UITheme.createButton(
                "Save Attendance Records",
                UITheme.SUCCESS_GREEN,
                Color.WHITE
        );

        btnSaveAttendance.addActionListener(
                e -> handleSaveAttendance()
        );

        bottomBar.add(btnSaveAttendance);

        card.add(bottomBar, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================
    // 2. MEDICALS PANEL
    // ==========================================
    private JPanel createMedicalsPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Undergraduate Medical Certificate Requests (FR-TO-02, FR-TO-03)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        top.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = UITheme.createButton("Refresh List", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> loadMedicalsData());
        top.add(btnRefresh, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        String[] cols = {"ID", "Student Reg No", "Student Name", "Course", "Date", "Reason", "Document Path", "Status"};
        medicalsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMedicals = new JTable(medicalsTableModel);
        UITheme.styleTable(tblMedicals);
        card.add(new JScrollPane(tblMedicals), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        bottomBar.setOpaque(false);

        JButton btnApprove = UITheme.createButton("Approve Medical", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnApprove.addActionListener(e -> handleUpdateMedicalStatus("APPROVED"));
        bottomBar.add(btnApprove);

        JButton btnReject = UITheme.createButton("Reject Medical", UITheme.DANGER_RED, Color.WHITE);
        btnReject.addActionListener(e -> handleUpdateMedicalStatus("REJECTED"));
        bottomBar.add(btnReject);

        card.add(bottomBar, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // 3. TIMETABLE PANEL (FR-TO-05)
    // ==========================================
    private JPanel createTimetablePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Department Timetable (Restricted to My Department - FR-TO-05)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Course Code", "Day", "Start Time", "End Time", "Venue", "Session Type"};
        timetableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTimetable = new JTable(timetableModel);
        UITheme.styleTable(tblTimetable);
        card.add(new JScrollPane(tblTimetable), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 4. NOTICES PANEL
    // ==========================================
    private JPanel createNoticesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Faculty Notices & Circulars (FR-TO-04)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Content", "Audience", "Date", "Posted By"};
        noticesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNotices = new JTable(noticesModel);
        UITheme.styleTable(tblNotices);
        card.add(new JScrollPane(tblNotices), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 5. PROFILE PANEL
    // ==========================================
    private JPanel createProfilePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Technical Officer Profile Details");
        lblTitle.setFont(UITheme.FONT_HEADER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Username (Read-Only):"), gbc);
        lblProfUsername = new JLabel(techOfficer != null ? techOfficer.getUsername() : "");
        lblProfUsername.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(lblProfUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Employee ID:"), gbc);
        lblProfEmpId = new JLabel(techOfficer != null ? techOfficer.getEmployeeId() : "");
        lblProfEmpId.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(lblProfEmpId, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Full Name:"), gbc);
        txtProfFullName = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 3;
        card.add(txtProfFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("Email Address:"), gbc);
        txtProfEmail = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 4;
        card.add(txtProfEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Phone Number:"), gbc);
        txtProfPhone = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 5;
        card.add(txtProfPhone, gbc);

        JButton btnSaveProfile = UITheme.createButton("Update Profile", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnSaveProfile.addActionListener(e -> handleUpdateProfile());
        gbc.gridx = 1; gbc.gridy = 6;
        card.add(btnSaveProfile, gbc);

        return card;
    }

    // ==========================================
    // DATA LOADERS & ACTIONS
    // ==========================================
    private void loadDepartmentCourses() {
        if (techOfficer == null || techOfficer.getDepartmentId() <= 0) {
            JOptionPane.showMessageDialog(this, "The Technical Officer department is not available.", "Profile Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Course> courses = toDAO.getCoursesByDepartment(techOfficer.getDepartmentId());
        cbAttCourse.removeAllItems();
        for (Course c : courses) {
            cbAttCourse.addItem(c.getCourseCode());
        }
    }

    private void loadAttendanceData() {
        String course = (String) cbAttCourse.getSelectedItem();
        if (course == null) return;

        try {
            Date dt = Date.valueOf(txtAttDate.getText().trim());
            String type = (String) cbAttType.getSelectedItem();

            List<Map<String, Object>> students = toDAO.getStudentsForAttendance(course, dt, type, techOfficer.getDepartmentId());
            attTableModel.setRowCount(0);

            for (Map<String, Object> m : students) {
                attTableModel.addRow(new Object[]{
                        m.get("studentId"),
                        m.get("regNo"),
                        m.get("fullName"),
                        m.get("status")
                });
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Please enter date in format YYYY-MM-DD", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setAllStatus(String status) {
        int rows = attTableModel.getRowCount();
        for (int i = 0; i < rows; i++) {
            attTableModel.setValueAt(status, i, 3);
        }
    }

    private void handleSaveAttendance() {
        if (techOfficer == null || techOfficer.getToId() <= 0) {
            JOptionPane.showMessageDialog(this, "Your Technical Officer account is not fully loaded.", "Cannot Save", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tblAttendance.isEditing()) {
            tblAttendance.getCellEditor().stopCellEditing();
        }

        String course = (String) cbAttCourse.getSelectedItem();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Missing Course", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final Date dt;
        try {
            dt = Date.valueOf(txtAttDate.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Please enter the date as YYYY-MM-DD.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String type = (String) cbAttType.getSelectedItem();
        int toId = techOfficer.getToId();

        int rows = attTableModel.getRowCount();
        if (rows == 0) return;

        int savedCount = 0;
        for (int i = 0; i < rows; i++) {
            int studentId = (Integer) attTableModel.getValueAt(i, 0);
            String status = String.valueOf(attTableModel.getValueAt(i, 3)).trim().toUpperCase();
            if (!("PRESENT".equals(status) || "ABSENT".equals(status))) {
                JOptionPane.showMessageDialog(this, "Each attendance status must be PRESENT or ABSENT.", "Invalid Status", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (toDAO.saveAttendance(studentId, course, dt, type, status, toId)) {
                savedCount++;
            }
        }

        JOptionPane.showMessageDialog(this, savedCount + " attendance records saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadMedicalsData() {
        if (techOfficer == null || techOfficer.getDepartmentId() <= 0) return;
        List<Medical> list = toDAO.getMedicalRequestsByDepartment(techOfficer.getDepartmentId());
        medicalsTableModel.setRowCount(0);

        for (Medical m : list) {
            medicalsTableModel.addRow(new Object[]{
                    m.getMedicalId(),
                    m.getStudentRegNo(),
                    m.getStudentName(),
                    m.getCourseCode(),
                    m.getMedicalDate(),
                    m.getReason(),
                    m.getDocumentPath(),
                    m.getStatus()
            });
        }
    }

    private void handleUpdateMedicalStatus(String newStatus) {
        int selectedRow = tblMedicals.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from table!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medId = (Integer) medicalsTableModel.getValueAt(selectedRow, 0);
        if (techOfficer == null || techOfficer.getToId() <= 0) {
            JOptionPane.showMessageDialog(this, "Your Technical Officer account is not fully loaded.", "Cannot Update", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int toId = techOfficer.getToId();

        if (toDAO.updateMedicalStatus(medId, newStatus, toId, techOfficer.getDepartmentId())) {
            JOptionPane.showMessageDialog(this, "Medical status updated to " + newStatus + "!", "Updated", JOptionPane.INFORMATION_MESSAGE);
            loadMedicalsData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medical status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTimetableData() {
        if (techOfficer == null || techOfficer.getDepartmentId() <= 0) return;
        List<Timetable> list = toDAO.getTimetableByDepartment(techOfficer.getDepartmentId());
        timetableModel.setRowCount(0);

        for (Timetable t : list) {
            timetableModel.addRow(new Object[]{
                    t.getTimetableId(),
                    t.getCourseCode(),
                    t.getDayOfWeek(),
                    t.getStartTime(),
                    t.getEndTime(),
                    t.getVenue(),
                    t.getSessionType()
            });
        }
    }

    private void loadNoticesData() {
        List<Notice> list = noticeDAO.getNoticesForRole("TECH_OFFICER");
        noticesModel.setRowCount(0);

        for (Notice n : list) {
            noticesModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    n.getContent(),
                    n.getTargetAudience(),
                    n.getCreatedDate(),
                    n.getPostedBy()
            });
        }
    }

    private void loadProfileData() {
        if (techOfficer != null) {
            lblProfUsername.setText(techOfficer.getUsername());
            lblProfEmpId.setText(techOfficer.getEmployeeId());
            txtProfFullName.setText(techOfficer.getFullName());
            txtProfEmail.setText(techOfficer.getEmail());
            txtProfPhone.setText(techOfficer.getPhoneNo());
        }
    }

    private void handleUpdateProfile() {
        String name = txtProfFullName.getText().trim();
        String email = txtProfEmail.getText().trim();
        String phone = txtProfPhone.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (techOfficer == null) {
            JOptionPane.showMessageDialog(this, "No Technical Officer is logged in.", "Profile Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (toDAO.updateProfile(techOfficer.getUserId(), name, email, phone)) {
            techOfficer.setFullName(name);
            techOfficer.setEmail(email);
            techOfficer.setPhoneNo(phone);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Profile could not be updated. Please check the database connection.", "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignOut() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
