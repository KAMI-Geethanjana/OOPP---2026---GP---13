package tg2088.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.model.Course;
import common.model.Notice;
import common.model.Result;
import common.util.GradeCalculator;
import common.util.UITheme;
import common.util.UserSession;
import tg2088.dao.LecturerDAO;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;


public class LecturerDashboard extends JFrame {

    private Lecturer lecturer;
    private LecturerDAO lecturerDAO;
    private NoticeDAO noticeDAO;

    // UI Panels
    private JPanel contentCards;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton btnNavMarks;
    private JButton btnNavEligibility;
    private JButton btnNavMaterials;
    private JButton btnNavAttendance;
    private JButton btnNavNotices;
    private JButton btnNavProfile;

    // Marks Management Components
    private JComboBox<String> cbMarksCourse;
    private JTable tblMarks;
    private DefaultTableModel marksTableModel;

    // Eligibility Components
    private JComboBox<String> cbEligCourse;
    private JTable tblEligibility;
    private DefaultTableModel eligTableModel;
    private JLabel lblEligSummary;

    // Course Materials Components
    private JComboBox<String> cbMaterialCourse;
    private JTable tblMaterials;
    private DefaultTableModel materialsTableModel;
    private JTextField txtMaterialTitle;
    private JTextField txtMaterialPath;

    // Attendance View Components
    private JComboBox<String> cbAttCourse;
    private JTable tblAttendance;
    private DefaultTableModel attTableModel;

    // Notices Components
    private JTable tblNotices;
    private DefaultTableModel noticesTableModel;

    // Profile Components
    private JTextField txtProfFullName;
    private JTextField txtProfEmail;
    private JTextField txtProfPhone;
    private JLabel lblProfUsername;
    private JLabel lblProfEmpId;

    public LecturerDashboard(Lecturer lecturer) {
        this.lecturer = lecturer;
        this.lecturerDAO = new LecturerDAO();
        this.noticeDAO = new NoticeDAO();

        // If lecturer model didn't have department populated, refresh from DB
        if (lecturer != null && lecturer.getDepartmentName() == null) {
            Lecturer fullLec = lecturerDAO.getLecturerByUserId(lecturer.getUserId());
            if (fullLec != null) {
                this.lecturer = fullLec;
            }
        }

        initUI();
    }

    private void initUI() {
        setTitle("Faculty of Technology Management System - Lecturer Portal (" + (lecturer != null ? lecturer.getFullName() : "") + ")");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        // Left Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Center Content Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.CONTENT_BG);

        // Top Header
        JPanel topHeader = createTopHeader();
        mainArea.add(topHeader, BorderLayout.NORTH);

        // Card Panels
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(UITheme.CONTENT_BG);
        contentCards.setBorder(new EmptyBorder(15, 25, 20, 25));

        contentCards.add(createMarksPanel(), "MARKS");
        contentCards.add(createEligibilityPanel(), "ELIGIBILITY");
        contentCards.add(createMaterialsPanel(), "MATERIALS");
        contentCards.add(createAttendancePanel(), "ATTENDANCE");
        contentCards.add(createNoticesPanel(), "NOTICES");
        contentCards.add(createProfilePanel(), "PROFILE");

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Initial Data Load
        loadLecturerCourses();
        loadMarksData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // System Logo & Title
        JLabel lblLogo = new JLabel("  FTMS PORTAL", SwingConstants.LEFT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(0, 24, 5, 0));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("  Lecturer Workspace", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UITheme.SIDEBAR_TEXT);
        lblSub.setBorder(new EmptyBorder(0, 24, 25, 0));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSub);

        // Navigation Items
        btnNavMarks = UITheme.createSidebarButton("Marks & Grading", true);
        btnNavEligibility = UITheme.createSidebarButton("Exam Eligibility", false);
        btnNavMaterials = UITheme.createSidebarButton("Course Materials", false);
        btnNavAttendance = UITheme.createSidebarButton("Student Attendance", false);
        btnNavNotices = UITheme.createSidebarButton("Notices", false);
        btnNavProfile = UITheme.createSidebarButton("My Profile", false);

        btnNavMarks.addActionListener(e -> switchCard("MARKS", btnNavMarks));
        btnNavEligibility.addActionListener(e -> {
            switchCard("ELIGIBILITY", btnNavEligibility);
            loadEligibilityData();
        });
        btnNavMaterials.addActionListener(e -> {
            switchCard("MATERIALS", btnNavMaterials);
            loadMaterialsData();
        });
        btnNavAttendance.addActionListener(e -> {
            switchCard("ATTENDANCE", btnNavAttendance);
            loadAttendanceData();
        });
        btnNavNotices.addActionListener(e -> {
            switchCard("NOTICES", btnNavNotices);
            loadNoticesData();
        });
        btnNavProfile.addActionListener(e -> {
            switchCard("PROFILE", btnNavProfile);
            loadProfileData();
        });

        sidebar.add(btnNavMarks);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavEligibility);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavMaterials);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavAttendance);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavNotices);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavProfile);

        sidebar.add(Box.createVerticalGlue());

        // Sign Out Button
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

        String welcomeName = lecturer != null ? lecturer.getFullName() : "Lecturer";
        JLabel lblWelcome = new JLabel("Welcome, " + welcomeName);
        lblWelcome.setFont(UITheme.FONT_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        String deptStr = lecturer != null && lecturer.getDepartmentName() != null ? lecturer.getDepartmentName() : "Faculty of Technology";
        JLabel lblSub = new JLabel(deptStr + " | Module TG2088 (Marks & Grading System)");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(lblWelcome);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        header.add(textPanel, BorderLayout.WEST);

        // Avatar circle
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        avatarPanel.setOpaque(false);
        JLabel lblAvatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(203, 213, 225));
                g2.fillOval(0, 0, 40, 40);
                g2.setColor(new Color(71, 85, 105));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.drawString("L", 15, 25);
                g2.dispose();
            }
        };
        lblAvatar.setPreferredSize(new Dimension(42, 42));
        avatarPanel.add(lblAvatar);
        header.add(avatarPanel, BorderLayout.EAST);

        return header;
    }

    private void switchCard(String cardName, JButton activeBtn) {
        cardLayout.show(contentCards, cardName);

        JButton[] buttons = {btnNavMarks, btnNavEligibility, btnNavMaterials, btnNavAttendance, btnNavNotices, btnNavProfile};
        for (JButton b : buttons) {
            boolean isActive = (b == activeBtn);
            b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
            b.setForeground(isActive ? UITheme.SIDEBAR_TEXT_ACTIVE : UITheme.SIDEBAR_TEXT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. MARKS & GRADING PANEL
    // ==========================================
    private JPanel createMarksPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        // Top Toolbar
        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblTitle = new JLabel("Undergraduate Marks Management (UGC 12-2024)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);

        cbMarksCourse = new JComboBox<>();
        cbMarksCourse.setFont(UITheme.FONT_BODY);
        cbMarksCourse.setPreferredSize(new Dimension(160, 34));
        cbMarksCourse.addActionListener(e -> loadMarksData());

        leftFilters.add(lblTitle);
        leftFilters.add(Box.createHorizontalStrut(15));
        leftFilters.add(lblCourse);
        leftFilters.add(cbMarksCourse);

        topToolbar.add(leftFilters, BorderLayout.WEST);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnCalc = UITheme.createButton("Calculate Grades", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnCalc.addActionListener(e -> calculateMarksOnTable());

        JButton btnSave = UITheme.createButton("Save Results", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnSave.addActionListener(e -> saveMarksToDatabase());

        actionPanel.add(btnCalc);
        actionPanel.add(btnSave);
        topToolbar.add(actionPanel, BorderLayout.EAST);

        card.add(topToolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Enrollment ID", "Student Reg No", "Student Name", "Attempt", "CA Marks (40%)", "Final Exam (60%)", "Total Marks", "Eligibility", "Grade", "GPV"};
        marksTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Allow direct editing of CA Marks (col 4) and Final Exam (col 5)
                return col == 4 || col == 5;
            }
        };

        tblMarks = new JTable(marksTableModel);
        UITheme.styleTable(tblMarks);
        JScrollPane scroll = new JScrollPane(tblMarks);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        card.add(scroll, BorderLayout.CENTER);

        // Bottom Note
        JLabel lblNote = new JLabel("Note: Double-click CA Marks or Final Exam cells to edit. Click 'Calculate Grades' then 'Save Results'. Repeat attempts capped at 'C'.");
        lblNote.setFont(UITheme.FONT_SMALL);
        lblNote.setForeground(UITheme.TEXT_MUTED);
        card.add(lblNote, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================
    // 2. EXAM ELIGIBILITY PANEL
    // ==========================================
    private JPanel createEligibilityPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel lblTitle = new JLabel("Exam Eligibility Checking (Attendance >= 80% & CA >= 40%)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        cbEligCourse = new JComboBox<>();
        cbEligCourse.setFont(UITheme.FONT_BODY);
        cbEligCourse.setPreferredSize(new Dimension(160, 34));
        cbEligCourse.addActionListener(e -> loadEligibilityData());

        left.add(lblTitle);
        left.add(Box.createHorizontalStrut(15));
        left.add(lblCourse);
        left.add(cbEligCourse);
        topToolbar.add(left, BorderLayout.WEST);

        lblEligSummary = new JLabel("Summary: -");
        lblEligSummary.setFont(UITheme.FONT_BODY_BOLD);
        lblEligSummary.setForeground(UITheme.PRIMARY_BLUE);
        topToolbar.add(lblEligSummary, BorderLayout.EAST);

        card.add(topToolbar, BorderLayout.NORTH);

        String[] cols = {"Student Reg No", "Student Name", "CA Mark", "CA Status", "Raw Attendance %", "Approved Medicals", "Excused Attendance %", "Final Exam Eligibility"};
        eligTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblEligibility = new JTable(eligTableModel);
        UITheme.styleTable(tblEligibility);
        card.add(new JScrollPane(tblEligibility), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 3. COURSE MATERIALS PANEL
    // ==========================================
    private JPanel createMaterialsPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topToolbar.setOpaque(false);
        JLabel lblTitle = new JLabel("Course Materials Repository");
        lblTitle.setFont(UITheme.FONT_HEADER);
        topToolbar.add(lblTitle);

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        cbMaterialCourse = new JComboBox<>();
        cbMaterialCourse.setFont(UITheme.FONT_BODY);
        cbMaterialCourse.setPreferredSize(new Dimension(160, 34));
        cbMaterialCourse.addActionListener(e -> loadMaterialsData());
        topToolbar.add(lblCourse);
        topToolbar.add(cbMaterialCourse);
        card.add(topToolbar, BorderLayout.NORTH);

        String[] cols = {"Material ID", "Course Code", "Title", "File / Resource Path", "Uploaded Date"};
        materialsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMaterials = new JTable(materialsTableModel);
        UITheme.styleTable(tblMaterials);
        card.add(new JScrollPane(tblMaterials), BorderLayout.CENTER);

        // Upload Form Panel at bottom
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Upload New Lecture Material"));

        formPanel.add(new JLabel("Title:"));
        txtMaterialTitle = UITheme.createTextField(18);
        formPanel.add(txtMaterialTitle);

        formPanel.add(new JLabel("File Path:"));
        txtMaterialPath = UITheme.createTextField(22);
        formPanel.add(txtMaterialPath);

        JButton btnUpload = UITheme.createButton("Upload Material", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnUpload.addActionListener(e -> handleAddMaterial());
        formPanel.add(btnUpload);

        JButton btnDelete = UITheme.createButton("Delete Selected", UITheme.DANGER_RED, Color.WHITE);
        btnDelete.addActionListener(e -> handleDeleteMaterial());
        formPanel.add(btnDelete);

        card.add(formPanel, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // 4. STUDENT ATTENDANCE PANEL
    // ==========================================
    private JPanel createAttendancePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Undergraduate Attendance Monitoring");
        lblTitle.setFont(UITheme.FONT_HEADER);
        top.add(lblTitle);

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        cbAttCourse = new JComboBox<>();
        cbAttCourse.setFont(UITheme.FONT_BODY);
        cbAttCourse.setPreferredSize(new Dimension(160, 34));
        cbAttCourse.addActionListener(e -> loadAttendanceData());
        top.add(lblCourse);
        top.add(cbAttCourse);
        card.add(top, BorderLayout.NORTH);

        String[] cols = {"Student Reg No", "Student Name", "Sessions Attended", "Total Sessions", "Approved Medicals", "Attendance %", "80% Requirement"};
        attTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAttendance = new JTable(attTableModel);
        UITheme.styleTable(tblAttendance);
        card.add(new JScrollPane(tblAttendance), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 5. NOTICES PANEL
    // ==========================================
    private JPanel createNoticesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Faculty Announcements & Notices");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Notice ID", "Title", "Content", "Audience", "Date", "Posted By"};
        noticesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNotices = new JTable(noticesTableModel);
        UITheme.styleTable(tblNotices);
        card.add(new JScrollPane(tblNotices), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 6. PROFILE PANEL
    // ==========================================
    private JPanel createProfilePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Lecturer Profile Details");
        lblTitle.setFont(UITheme.FONT_HEADER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Username (Read-Only):"), gbc);
        lblProfUsername = new JLabel(lecturer != null ? lecturer.getUsername() : "");
        lblProfUsername.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(lblProfUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Employee ID:"), gbc);
        lblProfEmpId = new JLabel(lecturer != null ? lecturer.getEmployeeId() : "");
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
    private void loadLecturerCourses() {
        int lecId = lecturer != null ? lecturer.getLecturerId() : 1;
        List<Course> courses = lecturerDAO.getCoursesByLecturer(lecId);

        cbMarksCourse.removeAllItems();
        cbEligCourse.removeAllItems();
        cbMaterialCourse.removeAllItems();
        cbAttCourse.removeAllItems();

        for (Course c : courses) {
            cbMarksCourse.addItem(c.getCourseCode());
            cbEligCourse.addItem(c.getCourseCode());
            cbMaterialCourse.addItem(c.getCourseCode());
            cbAttCourse.addItem(c.getCourseCode());
        }
    }

    private void loadMarksData() {
        String courseCode = (String) cbMarksCourse.getSelectedItem();
        if (courseCode == null) return;

        List<Result> results = lecturerDAO.getResultsByCourse(courseCode);
        marksTableModel.setRowCount(0);

        for (Result r : results) {
            marksTableModel.addRow(new Object[]{
                    r.getEnrollmentId(),
                    r.getStudentRegNo(),
                    r.getStudentName(),
                    r.getAttemptType(),
                    r.getCaMark(),
                    r.getFinalExamMark(),
                    r.getTotalMark(),
                    r.getEligibilityStatus(),
                    r.getGrade(),
                    r.getGradePoint()
            });
        }
    }

    private void calculateMarksOnTable() {
        if (tblMarks.isEditing()) {
            tblMarks.getCellEditor().stopCellEditing();
        }

        int rows = marksTableModel.getRowCount();
        if (rows == 0) return;

        try {
            for (int i = 0; i < rows; i++) {
                String attempt = marksTableModel.getValueAt(i, 3).toString();

                double ca = Double.parseDouble(marksTableModel.getValueAt(i, 4).toString());
                double fin = Double.parseDouble(marksTableModel.getValueAt(i, 5).toString());

                // Validate range (OOP Exception handling)
                GradeCalculator.validateMark(ca, "CA");
                GradeCalculator.validateMark(fin, "Final Exam");

                // Check CA Eligibility (CA >= 40%)
                boolean caEligible = GradeCalculator.isCaEligible(ca);
                String eligStatus = caEligible ? "ELIGIBLE" : "NOT_ELIGIBLE";

                // Calculate weighted total (40% CA, 60% Final Exam)
                double total = GradeCalculator.calculateTotalMarks(ca, fin);
                total = Math.round(total * 10.0) / 10.0;

                // Grade & Grade Point per UGC Circular 12-2024
                String grade = GradeCalculator.determineGrade(total, caEligible, attempt);
                double gpv = GradeCalculator.getGradePoint(grade);

                marksTableModel.setValueAt(total, i, 6);
                marksTableModel.setValueAt(eligStatus, i, 7);
                marksTableModel.setValueAt(grade, i, 8);
                marksTableModel.setValueAt(gpv, i, 9);
            }

            JOptionPane.showMessageDialog(this, "Marks and UGC grades calculated successfully!\nClick 'Save Results' to persist into database.", "Calculated", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Calculation Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMarksToDatabase() {
        if (tblMarks.isEditing()) {
            tblMarks.getCellEditor().stopCellEditing();
        }

        int rows = marksTableModel.getRowCount();
        if (rows == 0) return;

        int successCount = 0;
        for (int i = 0; i < rows; i++) {
            int enrollmentId = Integer.parseInt(marksTableModel.getValueAt(i, 0).toString());
            double ca = Double.parseDouble(marksTableModel.getValueAt(i, 4).toString());
            double fin = Double.parseDouble(marksTableModel.getValueAt(i, 5).toString());
            double total = Double.parseDouble(marksTableModel.getValueAt(i, 6).toString());
            String elig = marksTableModel.getValueAt(i, 7).toString();
            String grade = marksTableModel.getValueAt(i, 8).toString();
            double gpv = Double.parseDouble(marksTableModel.getValueAt(i, 9).toString());

            boolean ok = lecturerDAO.saveOrUpdateResult(enrollmentId, ca, fin, total, elig, grade, gpv);
            if (ok) successCount++;
        }

        JOptionPane.showMessageDialog(this, successCount + " marks records saved successfully to database!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadEligibilityData() {
        String courseCode = (String) cbEligCourse.getSelectedItem();
        if (courseCode == null) return;

        List<Result> results = lecturerDAO.getResultsByCourse(courseCode);
        List<Map<String, Object>> attList = lecturerDAO.getStudentAttendanceSummary(courseCode);

        eligTableModel.setRowCount(0);

        int totalCount = 0;
        int overallEligibleCount = 0;

        for (Result r : results) {
            totalCount++;
            String regNo = r.getStudentRegNo();

            // Find matching attendance record
            double rawAtt = 0.0;
            double excAtt = 0.0;
            int medCount = 0;
            for (Map<String, Object> map : attList) {
                if (regNo.equals(map.get("regNo"))) {
                    rawAtt = (Double) map.get("rawPercentage");
                    excAtt = (Double) map.get("excusedPercentage");
                    medCount = (Integer) map.get("approvedMedicals");
                    break;
                }
            }

            boolean caElig = GradeCalculator.isCaEligible(r.getCaMark());
            boolean attElig = excAtt >= 80.0;
            boolean finalElig = caElig && attElig;
            if (finalElig) overallEligibleCount++;

            eligTableModel.addRow(new Object[]{
                    regNo,
                    r.getStudentName(),
                    r.getCaMark(),
                    caElig ? "ELIGIBLE (>=40%)" : "INELIGIBLE (<40%)",
                    rawAtt + "%",
                    medCount + " Approved",
                    excAtt + "%",
                    finalElig ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
        }

        lblEligSummary.setText("Eligible Students: " + overallEligibleCount + " / " + totalCount);
    }

    private void loadMaterialsData() {
        String courseCode = (String) cbMaterialCourse.getSelectedItem();
        if (courseCode == null) return;

        List<CourseMaterial> materials = lecturerDAO.getCourseMaterials(courseCode);
        materialsTableModel.setRowCount(0);

        for (CourseMaterial m : materials) {
            materialsTableModel.addRow(new Object[]{
                    m.getMaterialId(),
                    m.getCourseCode(),
                    m.getTitle(),
                    m.getFilePath(),
                    m.getUploadedDate()
            });
        }
    }

    private void handleAddMaterial() {
        String courseCode = (String) cbMaterialCourse.getSelectedItem();
        String title = txtMaterialTitle.getText().trim();
        String path = txtMaterialPath.getText().trim();

        if (title.isEmpty() || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both title and file path!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int lecId = lecturer != null ? lecturer.getLecturerId() : 1;
        CourseMaterial cm = new CourseMaterial(0, courseCode, lecId, title, path, new Date(System.currentTimeMillis()));
        boolean ok = lecturerDAO.addCourseMaterial(cm);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Course material uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtMaterialTitle.setText("");
            txtMaterialPath.setText("");
            loadMaterialsData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to upload course material.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMaterial() {
        int selectedRow = tblMaterials.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int matId = Integer.parseInt(materialsTableModel.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this material?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (lecturerDAO.deleteCourseMaterial(matId)) {
                JOptionPane.showMessageDialog(this, "Material deleted successfully!");
                loadMaterialsData();
            }
        }
    }

    private void loadAttendanceData() {
        String courseCode = (String) cbAttCourse.getSelectedItem();
        if (courseCode == null) return;

        List<Map<String, Object>> list = lecturerDAO.getStudentAttendanceSummary(courseCode);
        attTableModel.setRowCount(0);

        for (Map<String, Object> map : list) {
            attTableModel.addRow(new Object[]{
                    map.get("regNo"),
                    map.get("fullName"),
                    map.get("attendedCount"),
                    map.get("totalSessions"),
                    map.get("approvedMedicals"),
                    map.get("excusedPercentage") + "%",
                    (Boolean) map.get("isEligible") ? "MET (>= 80%)" : "SHORTAGE (< 80%)"
            });
        }
    }

    private void loadNoticesData() {
        List<Notice> list = noticeDAO.getNoticesForRole("LECTURER");
        noticesTableModel.setRowCount(0);

        for (Notice n : list) {
            noticesTableModel.addRow(new Object[]{
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
        if (lecturer != null) {
            lblProfUsername.setText(lecturer.getUsername());
            lblProfEmpId.setText(lecturer.getEmployeeId());
            txtProfFullName.setText(lecturer.getFullName());
            txtProfEmail.setText(lecturer.getEmail());
            txtProfPhone.setText(lecturer.getPhoneNo());
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

        boolean ok = lecturerDAO.updateProfile(lecturer.getUserId(), name, email, phone);
        if (ok) {
            lecturer.setFullName(name);
            lecturer.setEmail(email);
            lecturer.setPhoneNo(phone);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
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
