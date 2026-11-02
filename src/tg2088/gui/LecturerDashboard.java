package tg2088.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.model.Course;
import common.model.Notice;
import common.model.Result;
import common.util.GradeCalculator;
import common.util.UITheme;
import common.util.UserSession;
import tg2088.dao.ILecturerDAO;
import tg2088.dao.LecturerDAO;
import tg2088.dao.IQuizDAO;
import tg2088.dao.QuizDAO;
import tg2088.exception.LecturerOperationException;
import tg2088.exception.QuizOperationException;
import tg2088.model.CourseMaterial;
import tg2088.model.Lecturer;
import tg2088.model.Quiz;
import tg2088.model.QuizQuestion;
import tg2088.util.LecturerFileHandler;
import tg2088.util.QuizFileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * Lessons 02, 04, 05, 06: GUI, Interfaces, Exception Handling & File Handling
 * ============================================================================
 * Reference:
 * - Lesson 04, Slide 29: Using an Interface as a Type ('ILecturerDAO lecturerDAO')
 * - Lesson 04, Slide 30: Using Interfaces for Callbacks (ActionListener event handlers)
 * - Lesson 05, Slide 25-29: Structured try-catch blocks with custom checked exceptions
 * - Lesson 06, Slide 12-18: Character stream report export (PrintWriter, FileWriter)
 * - Lesson 06, Slide 27-33: Binary stream backup (DataOutputStream)
 * - Lesson 02 Part 02, Slide 5-7: Usage of 'this' keyword as current object reference
 * ============================================================================
 */
public class LecturerDashboard extends JFrame {

    // ========================================================================
    // Instance Fields (Encapsulation - Lesson 03 Part 04, Slide 11)
    // ========================================================================
    private Lecturer lecturer;
    
    // Interface as a Type (Lesson 04, Slide 29: Using an Interface as a Type)
    private ILecturerDAO lecturerDAO;
    private IQuizDAO quizDAO;
    private NoticeDAO noticeDAO;

    // UI Panels
    private JPanel contentCards;
    private CardLayout cardLayout;

    // Sidebar navigation buttons
    private JButton btnNavMarks;
    private JButton btnNavEligibility;
    private JButton btnNavMaterials;
    private JButton btnNavQuizzes;
    private JButton btnNavAttendance;
    private JButton btnNavNotices;
    private JButton btnNavProfile;

    // Quiz Management Components
    private JComboBox<String> cbQuizCourse;
    private JTable tblQuizzes;
    private DefaultTableModel quizzesTableModel;

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

    // Profile Components (Enhanced to match Image 2)
    private JTextField txtProfFullName;
    private JTextField txtProfFirstName;
    private JTextField txtProfLastName;
    private JTextField txtProfEmail;
    private JTextField txtProfPhone;
    private JTextField txtProfNic;
    private JTextField txtProfDob;
    private JTextField txtProfStreet;
    private JTextField txtProfCity;
    private JTextField txtProfPostal;
    private JTextField txtProfCountry;
    private JLabel lblProfUsername;
    private JLabel lblProfEmpId;
    private JLabel lblProfileAvatar;

    /**
     * Parameterized Constructor (Lesson 02, Slide 38)
     * Demonstrates 'this' keyword to initialize instance variables (Lesson 02 Part 02, Slide 5-7).
     */
    public LecturerDashboard(Lecturer lecturer) {
        this.lecturer = lecturer;
        // Instantiating interface implementation (Lesson 04, Slide 10 & 29)
        this.lecturerDAO = new LecturerDAO();
        this.quizDAO = new QuizDAO();
        this.noticeDAO = new NoticeDAO();

        // If lecturer model didn't have department populated, refresh from DB
        if (this.lecturer != null && this.lecturer.getDepartmentName() == null) {
            Lecturer fullLec = this.lecturerDAO.getLecturerByUserId(this.lecturer.getUserId());
            if (fullLec != null) {
                this.lecturer = fullLec;
            }
        }

        initUI();
    }

    private void initUI() {
        setTitle("Faculty of Technology Management System - Lecturer Portal (" + (lecturer != null ? lecturer.getFullName() : "") + ")");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1000, 620));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        contentCards.add(createQuizzesPanel(), "QUIZZES");
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
        btnNavQuizzes = UITheme.createSidebarButton("Online Quizzes", false);
        btnNavAttendance = UITheme.createSidebarButton("Student Attendance", false);
        btnNavNotices = UITheme.createSidebarButton("Notices", false);
        btnNavProfile = UITheme.createSidebarButton("My Profile", false);

        // Lesson 04 Slide 30: Using Interfaces for Callbacks (ActionListener event dispatch)
        btnNavMarks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchCard("MARKS", btnNavMarks);
            }
        });
        btnNavEligibility.addActionListener(e -> {
            switchCard("ELIGIBILITY", btnNavEligibility);
            loadEligibilityData();
        });
        btnNavMaterials.addActionListener(e -> {
            switchCard("MATERIALS", btnNavMaterials);
            loadMaterialsData();
        });
        btnNavQuizzes.addActionListener(e -> {
            switchCard("QUIZZES", btnNavQuizzes);
            loadQuizzesData();
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
        sidebar.add(btnNavQuizzes);
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

        JButton[] buttons = {btnNavMarks, btnNavEligibility, btnNavMaterials, btnNavQuizzes, btnNavAttendance, btnNavNotices, btnNavProfile};
        for (JButton b : buttons) {
            boolean isActive = (b == activeBtn);
            b.setSelected(isActive);
            b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
            b.setForeground(isActive ? UITheme.SIDEBAR_TEXT_ACTIVE : UITheme.SIDEBAR_TEXT);
            b.repaint();
        }
    }

    // ========================================================================
    // 1. MARKS & GRADING PANEL
    // ========================================================================
    private JPanel createMarksPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        // Top Toolbar (Structured in 2 clean rows to prevent overlapping on any resolution)
        JPanel topToolbar = new JPanel();
        topToolbar.setLayout(new BoxLayout(topToolbar, BoxLayout.Y_AXIS));
        topToolbar.setOpaque(false);

        // Row 1: Section Title
        JLabel lblTitle = new JLabel("Undergraduate Marks Management (UGC 12-2024)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Row 2: Controls Toolbar using FlowLayout so controls NEVER overlap on ANY resolution
        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controlRow.setOpaque(false);
        controlRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);

        cbMarksCourse = new JComboBox<>();
        cbMarksCourse.setFont(UITheme.FONT_BODY);
        cbMarksCourse.setPreferredSize(new Dimension(150, 34));
        cbMarksCourse.addActionListener(e -> loadMarksData());

        JButton btnCalc = UITheme.createButton("Calculate Grades", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnCalc.addActionListener(e -> calculateMarksOnTable());

        JButton btnSave = UITheme.createButton("Save Results", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnSave.addActionListener(e -> saveMarksToDatabase());

        // Lesson 06: File Handling - Character Stream Export Button
        JButton btnExport = UITheme.createButton("Export Report (.txt)", new Color(79, 70, 229), Color.WHITE);
        btnExport.setToolTipText("Export marks to a character stream text report (Lesson 06)");
        btnExport.addActionListener(e -> handleExportMarksReport());

        controlRow.add(lblCourse);
        controlRow.add(cbMarksCourse);
        controlRow.add(Box.createHorizontalStrut(15));
        controlRow.add(btnCalc);
        controlRow.add(btnSave);
        controlRow.add(btnExport);

        topToolbar.add(lblTitle);
        topToolbar.add(Box.createVerticalStrut(10));
        topToolbar.add(controlRow);

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
        JLabel lblNote = new JLabel("Note: Double-click CA Marks or Final Exam cells to edit. Click 'Calculate Grades' then 'Save Results'. Export report generates .txt via Lesson 06 Character Streams.");
        lblNote.setFont(UITheme.FONT_SMALL);
        lblNote.setForeground(UITheme.TEXT_MUTED);
        card.add(lblNote, BorderLayout.SOUTH);

        return card;
    }

    // ========================================================================
    // 2. EXAM ELIGIBILITY PANEL
    // ========================================================================
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

    // ========================================================================
    // 3. COURSE MATERIALS PANEL
    // ========================================================================
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Upload & Backup Lecture Materials"));

        formPanel.add(new JLabel("Title:"));
        txtMaterialTitle = UITheme.createTextField(16);
        formPanel.add(txtMaterialTitle);

        formPanel.add(new JLabel("File Path:"));
        txtMaterialPath = UITheme.createTextField(18);
        formPanel.add(txtMaterialPath);

        JButton btnUpload = UITheme.createButton("Upload Material", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnUpload.addActionListener(e -> handleAddMaterial());
        formPanel.add(btnUpload);

        JButton btnDelete = UITheme.createButton("Delete Selected", UITheme.DANGER_RED, Color.WHITE);
        btnDelete.addActionListener(e -> handleDeleteMaterial());
        formPanel.add(btnDelete);

        // Lesson 06: Binary Stream Backup Button (DataOutputStream)
        JButton btnBinaryBackup = UITheme.createButton("Binary Backup (.dat)", new Color(13, 148, 136), Color.WHITE);
        btnBinaryBackup.setToolTipText("Backup materials list to a binary stream file (Lesson 06)");
        btnBinaryBackup.addActionListener(e -> handleBackupMaterials());
        formPanel.add(btnBinaryBackup);

        card.add(formPanel, BorderLayout.SOUTH);
        return card;
    }

    // ========================================================================
    // 3.5. ONLINE QUIZZES & ASSESSMENTS PANEL
    // ========================================================================
    private JPanel createQuizzesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        // Top Toolbar
        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblTitle = new JLabel("Online Quiz Assessments & Question Banks");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);

        cbQuizCourse = new JComboBox<>();
        cbQuizCourse.setFont(UITheme.FONT_BODY);
        cbQuizCourse.setPreferredSize(new Dimension(160, 34));
        cbQuizCourse.addActionListener(e -> loadQuizzesData());

        leftFilters.add(lblTitle);
        leftFilters.add(Box.createHorizontalStrut(15));
        leftFilters.add(lblCourse);
        leftFilters.add(cbQuizCourse);

        topToolbar.add(leftFilters, BorderLayout.WEST);

        // Right side: New Quiz button
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        JButton btnNewQuiz = UITheme.createButton("+ Create New Quiz", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnNewQuiz.addActionListener(e -> handleCreateQuiz());

        JButton btnRefresh = UITheme.createButton("Refresh", UITheme.SIDEBAR_BG, Color.WHITE);
        btnRefresh.addActionListener(e -> loadQuizzesData());

        rightActions.add(btnNewQuiz);
        rightActions.add(btnRefresh);
        topToolbar.add(rightActions, BorderLayout.EAST);

        card.add(topToolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Quiz ID", "Course", "Title", "Duration (Mins)", "Total Marks", "Questions", "Status", "Created At"};
        quizzesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuizzes = new JTable(quizzesTableModel);
        UITheme.styleTable(tblQuizzes);
        card.add(new JScrollPane(tblQuizzes), BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createTitledBorder("Quiz Actions & Exports"));

        JButton btnManageQ = UITheme.createButton("Manage Questions", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnManageQ.setToolTipText("Add or edit MCQ questions for the selected quiz");
        btnManageQ.addActionListener(e -> handleManageQuestions());

        JButton btnPreview = UITheme.createButton("Preview / Take Quiz", new Color(139, 92, 246), Color.WHITE);
        btnPreview.setToolTipText("Test-drive the quiz and evaluate grading live");
        btnPreview.addActionListener(e -> handlePreviewQuiz());

        JButton btnSubmissions = UITheme.createButton("View Submissions", new Color(14, 165, 233), Color.WHITE);
        btnSubmissions.setToolTipText("View student attempts, scores and pass rates");
        btnSubmissions.addActionListener(e -> handleViewSubmissions());

        JButton btnExportTxt = UITheme.createButton("Export Paper (.txt)", UITheme.SIDEBAR_BG, Color.WHITE);
        btnExportTxt.setToolTipText("Export formatted question paper with answer key (Lesson 06 Character Stream)");
        btnExportTxt.addActionListener(e -> handleExportQuizPaper());

        JButton btnBackupDat = UITheme.createButton("Backup (.dat)", new Color(13, 148, 136), Color.WHITE);
        btnBackupDat.setToolTipText("Backup quiz and question bank to binary file (Lesson 06 Binary Stream)");
        btnBackupDat.addActionListener(e -> handleBackupQuizBinary());

        JButton btnToggleStatus = UITheme.createButton("Toggle Status", UITheme.WARNING_AMBER, Color.WHITE);
        btnToggleStatus.setToolTipText("Activate or deactivate quiz for students");
        btnToggleStatus.addActionListener(e -> handleToggleQuizStatus());

        JButton btnDeleteQuiz = UITheme.createButton("Delete Quiz", UITheme.DANGER_RED, Color.WHITE);
        btnDeleteQuiz.addActionListener(e -> handleDeleteQuiz());

        bottomBar.add(btnManageQ);
        bottomBar.add(btnPreview);
        bottomBar.add(btnSubmissions);
        bottomBar.add(btnExportTxt);
        bottomBar.add(btnBackupDat);
        bottomBar.add(btnToggleStatus);
        bottomBar.add(btnDeleteQuiz);

        card.add(bottomBar, BorderLayout.SOUTH);
        return card;
    }

    // ========================================================================
    // 4. STUDENT ATTENDANCE PANEL
    // ========================================================================
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

    // ========================================================================
    // 5. NOTICES PANEL
    // ========================================================================
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

    // ========================================================================
    // 6. ENHANCED PROFILE PANEL (Matches Image 2 - Avatar, Account, Personal & Address)
    // ========================================================================
    private JPanel createProfilePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10, 15, 20, 15));

        // 0. Title Header
        JLabel lblTitle = new JLabel("Edit Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(UITheme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(15));

        // 1. Header with Avatar & "Change Picture"
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblProfileAvatar = new JLabel("L", SwingConstants.CENTER);
        lblProfileAvatar.setPreferredSize(new Dimension(85, 85));
        lblProfileAvatar.setMinimumSize(new Dimension(85, 85));
        lblProfileAvatar.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 2));
        lblProfileAvatar.setOpaque(true);
        lblProfileAvatar.setBackground(new Color(224, 231, 255));
        lblProfileAvatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblProfileAvatar.setForeground(UITheme.PRIMARY_BLUE);

        JPanel avatarTextPanel = new JPanel();
        avatarTextPanel.setLayout(new BoxLayout(avatarTextPanel, BoxLayout.Y_AXIS));
        avatarTextPanel.setOpaque(false);

        JLabel lblNameTitle = new JLabel(lecturer != null ? lecturer.getFullName() : "Mr. T.H.D. Wosada");
        lblNameTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNameTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblEmpSub = new JLabel((lecturer != null ? lecturer.getEmployeeId() : "LEC-0002") + " | Lecturer");
        lblEmpSub.setFont(UITheme.FONT_SMALL);
        lblEmpSub.setForeground(UITheme.TEXT_MUTED);

        JButton btnChangePic = new JButton("Change Picture");
        btnChangePic.setFont(UITheme.FONT_SMALL);
        btnChangePic.setFocusPainted(false);
        btnChangePic.addActionListener(e -> handleChangeProfilePicture());

        avatarTextPanel.add(lblNameTitle);
        avatarTextPanel.add(Box.createVerticalStrut(3));
        avatarTextPanel.add(lblEmpSub);
        avatarTextPanel.add(Box.createVerticalStrut(8));
        avatarTextPanel.add(btnChangePic);

        headerPanel.add(lblProfileAvatar);
        headerPanel.add(avatarTextPanel);
        container.add(headerPanel);
        container.add(Box.createVerticalStrut(20));

        // 2. Account (cannot be changed here)
        container.add(createProfileSectionTitle("Account (cannot be changed here)"));
        container.add(Box.createVerticalStrut(6));

        JPanel accountGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        accountGrid.setOpaque(false);
        accountGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblProfUsername = new JLabel(lecturer != null ? lecturer.getUsername() : "wosada");
        lblProfEmpId = new JLabel(lecturer != null ? lecturer.getEmployeeId() : "LEC-0002");

        JTextField tfUser = createReadOnlyField(lblProfUsername.getText());
        JTextField tfRole = createReadOnlyField("Lecturer");
        JTextField tfPass = createReadOnlyField("Changed by the administrator");

        accountGrid.add(createLabeledField("Username", tfUser));
        accountGrid.add(createLabeledField("Role", tfRole));
        accountGrid.add(createLabeledField("Password", tfPass));
        container.add(accountGrid);
        container.add(Box.createVerticalStrut(20));

        // 3. Personal Details
        container.add(createProfileSectionTitle("Personal Details"));
        container.add(Box.createVerticalStrut(6));

        JPanel personalGrid = new JPanel(new GridLayout(2, 3, 15, 12));
        personalGrid.setOpaque(false);
        personalGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        String full = (lecturer != null && lecturer.getFullName() != null) ? lecturer.getFullName() : "T.H.D. Wosada";
        String[] nameParts = full.split(" ", 2);

        txtProfFirstName = UITheme.createTextField(15);
        txtProfFirstName.setText(nameParts.length > 0 ? nameParts[0] : "T.H.D.");

        txtProfLastName = UITheme.createTextField(15);
        txtProfLastName.setText(nameParts.length > 1 ? nameParts[1] : "Wosada");

        txtProfEmail = UITheme.createTextField(15);
        txtProfEmail.setText(lecturer != null && lecturer.getEmail() != null ? lecturer.getEmail() : "wosada@faculty.local");

        txtProfNic = UITheme.createTextField(15);
        txtProfNic.setText("199512345678");

        txtProfDob = UITheme.createTextField(15);
        txtProfDob.setText("1990-05-15");

        txtProfPhone = UITheme.createTextField(15);
        txtProfPhone.setText(lecturer != null && lecturer.getPhoneNo() != null ? lecturer.getPhoneNo() : "0713000001");

        txtProfFullName = new JTextField();
        txtProfFullName.setText(full);

        personalGrid.add(createLabeledField("First name", txtProfFirstName));
        personalGrid.add(createLabeledField("Last name", txtProfLastName));
        personalGrid.add(createLabeledField("E-mail", txtProfEmail));
        personalGrid.add(createLabeledField("NIC", txtProfNic));
        personalGrid.add(createLabeledField("Date of birth", txtProfDob));
        personalGrid.add(createLabeledField("Phone", txtProfPhone));
        container.add(personalGrid);
        container.add(Box.createVerticalStrut(20));

        // 4. Address
        container.add(createProfileSectionTitle("Address"));
        container.add(Box.createVerticalStrut(6));

        JPanel addressGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        addressGrid.setOpaque(false);
        addressGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtProfStreet = UITheme.createTextField(15);
        txtProfStreet.setText("Faculty Road");

        txtProfCity = UITheme.createTextField(15);
        txtProfCity.setText("Matara");

        txtProfPostal = UITheme.createTextField(15);
        txtProfPostal.setText("81000");

        txtProfCountry = UITheme.createTextField(15);
        txtProfCountry.setText("Sri Lanka");

        addressGrid.add(createLabeledField("Street", txtProfStreet));
        addressGrid.add(createLabeledField("City", txtProfCity));
        addressGrid.add(createLabeledField("Postal code", txtProfPostal));
        addressGrid.add(createLabeledField("Country", txtProfCountry));
        container.add(addressGrid);
        container.add(Box.createVerticalStrut(25));

        // 5. Action Buttons (Save Changes & Reset)
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSave = UITheme.createButton("Save Changes", new Color(30, 41, 59), Color.WHITE);
        btnSave.setPreferredSize(new Dimension(140, 38));
        btnSave.addActionListener(e -> handleUpdateProfile());

        JButton btnReset = UITheme.createButton("Reset", new Color(241, 245, 249), UITheme.TEXT_DARK);
        btnReset.setPreferredSize(new Dimension(100, 38));
        btnReset.addActionListener(e -> loadProfileData());

        actionRow.add(btnSave);
        actionRow.add(btnReset);
        container.add(actionRow);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);

        loadProfileAvatar();

        return card;
    }

    private JLabel createProfileSectionTitle(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createReadOnlyField(String text) {
        JTextField tf = UITheme.createTextField(15);
        tf.setText(text);
        tf.setEditable(false);
        tf.setBackground(new Color(241, 245, 249));
        tf.setForeground(new Color(71, 85, 105));
        return tf;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    // ========================================================================
    // DATA LOADERS & ACTIONS
    // ========================================================================
    private void loadLecturerCourses() {
        int lecId = lecturer != null ? lecturer.getLecturerId() : 1;
        List<Course> courses = lecturerDAO.getCoursesByLecturer(lecId);

        cbMarksCourse.removeAllItems();
        cbEligCourse.removeAllItems();
        cbMaterialCourse.removeAllItems();
        if (cbQuizCourse != null) cbQuizCourse.removeAllItems();
        cbAttCourse.removeAllItems();

        for (Course c : courses) {
            cbMarksCourse.addItem(c.getCourseCode());
            cbEligCourse.addItem(c.getCourseCode());
            cbMaterialCourse.addItem(c.getCourseCode());
            if (cbQuizCourse != null) cbQuizCourse.addItem(c.getCourseCode());
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

        // Structured Exception Handling (Lesson 05, Slide 25-29)
        try {
            for (int i = 0; i < rows; i++) {
                String attempt = marksTableModel.getValueAt(i, 3).toString();

                double ca = Double.parseDouble(marksTableModel.getValueAt(i, 4).toString());
                double fin = Double.parseDouble(marksTableModel.getValueAt(i, 5).toString());

                // Validate range with custom exception (Lesson 05, Slide 40 & 44)
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

            // 'this' passed as parent component to dialog (Lesson 02 Part 02, Slide 10)
            JOptionPane.showMessageDialog(this,
                    "Marks and UGC grades calculated successfully!\nClick 'Save Results' to persist into database.",
                    "Calculated", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException nfe) {
            // Multiple Catch Blocks: catching specific format exception first (Lesson 05, Slide 28)
            JOptionPane.showMessageDialog(this, "Please enter valid numeric marks: " + nfe.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
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
        try {
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

        } catch (LecturerOperationException loe) {
            // Custom Checked Exception caught and presented gracefully (Lesson 05, Slide 25)
            JOptionPane.showMessageDialog(this, "Operation Error: " + loe.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lesson 06: File Handling - Character Stream Export Handler
     * Uses PrintWriter and BufferedWriter to write a formatted .txt report.
     */
    private void handleExportMarksReport() {
        String courseCode = (String) cbMarksCourse.getSelectedItem();
        if (courseCode == null) return;

        List<Result> results = lecturerDAO.getResultsByCourse(courseCode);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student results found to export for " + courseCode, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Course_" + courseCode + "_Marks_Report.txt"));
        int choice = fileChooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            boolean success = LecturerFileHandler.exportMarksReport(courseCode, results, chosenFile);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Marks Report exported successfully using Character Streams!\nSaved to: " + chosenFile.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write report to file.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            boolean attElig = excAtt >= ILecturerDAO.MIN_ATTENDANCE_REQUIREMENT;
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

        try {
            boolean ok = lecturerDAO.addCourseMaterial(cm);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Course material uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtMaterialTitle.setText("");
                txtMaterialPath.setText("");
                loadMaterialsData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to upload course material.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (LecturerOperationException loe) {
            JOptionPane.showMessageDialog(this, loe.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Lesson 06: Binary Stream Backup Handler
     * Uses DataOutputStream and BufferedOutputStream to serialize materials into a .dat binary file.
     */
    private void handleBackupMaterials() {
        String courseCode = (String) cbMaterialCourse.getSelectedItem();
        if (courseCode == null) return;

        List<CourseMaterial> materials = lecturerDAO.getCourseMaterials(courseCode);
        if (materials.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No materials found to backup for " + courseCode, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Materials_" + courseCode + "_Backup.dat"));
        int choice = fileChooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            boolean success = LecturerFileHandler.saveMaterialsToBinaryFile(materials, chosenFile);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Course Materials binary backup created successfully via DataOutputStream!\nFile: " + chosenFile.getAbsolutePath(),
                        "Backup Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create binary backup.", "Backup Error", JOptionPane.ERROR_MESSAGE);
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
            lblProfUsername.setText(lecturer.getUsername() != null ? lecturer.getUsername() : "");
            lblProfEmpId.setText(lecturer.getEmployeeId() != null ? lecturer.getEmployeeId() : "");
            String full = lecturer.getFullName() != null ? lecturer.getFullName() : "";
            txtProfFullName.setText(full);
            String[] parts = full.split(" ", 2);
            if (txtProfFirstName != null) txtProfFirstName.setText(parts.length > 0 ? parts[0] : "");
            if (txtProfLastName != null) txtProfLastName.setText(parts.length > 1 ? parts[1] : "");
            if (txtProfEmail != null) txtProfEmail.setText(lecturer.getEmail() != null ? lecturer.getEmail() : "");
            if (txtProfPhone != null) txtProfPhone.setText(lecturer.getPhoneNo() != null ? lecturer.getPhoneNo() : "");
            loadProfileAvatar();
        }
    }

    private void handleChangeProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images (JPG, PNG)", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                File destDir = new File("resources/profile_photos");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, "lecturer_" + (lecturer != null ? lecturer.getUserId() : 1) + ".png");
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                loadProfileAvatar();
                JOptionPane.showMessageDialog(this, "Profile picture updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving picture: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProfileAvatar() {
        if (lblProfileAvatar == null) return;
        try {
            File photoFile = new File("resources/profile_photos/lecturer_" + (lecturer != null ? lecturer.getUserId() : 1) + ".png");
            if (photoFile.exists()) {
                ImageIcon icon = new ImageIcon(photoFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH);
                lblProfileAvatar.setText("");
                lblProfileAvatar.setIcon(new ImageIcon(scaled));
            } else {
                lblProfileAvatar.setIcon(null);
                lblProfileAvatar.setText(lecturer != null && lecturer.getFullName() != null && !lecturer.getFullName().isEmpty() ? lecturer.getFullName().substring(0, 1).toUpperCase() : "L");
                lblProfileAvatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
                lblProfileAvatar.setForeground(UITheme.PRIMARY_BLUE);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private void handleUpdateProfile() {
        String fName = txtProfFirstName != null ? txtProfFirstName.getText().trim() : "";
        String lName = txtProfLastName != null ? txtProfLastName.getText().trim() : "";
        String name = (fName + " " + lName).trim();
        if (name.isEmpty() && txtProfFullName != null) {
            name = txtProfFullName.getText().trim();
        }
        String email = txtProfEmail != null ? txtProfEmail.getText().trim() : "";
        String phone = txtProfPhone != null ? txtProfPhone.getText().trim() : "";

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = lecturerDAO.updateProfile(lecturer.getUserId(), name, email, phone);
        if (ok) {
            lecturer.setFullName(name);
            lecturer.setEmail(email);
            lecturer.setPhoneNo(phone);
            txtProfFullName.setText(name);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========================================================================
    // QUIZ DATA LOADERS & ACTIONS
    // ========================================================================
    private void loadQuizzesData() {
        if (cbQuizCourse == null || quizzesTableModel == null) return;
        String courseCode = (String) cbQuizCourse.getSelectedItem();
        if (courseCode == null) return;

        List<Quiz> quizzes = quizDAO.getQuizzesByCourse(courseCode);
        quizzesTableModel.setRowCount(0);

        for (Quiz q : quizzes) {
            quizzesTableModel.addRow(new Object[]{
                    q.getQuizId(),
                    q.getCourseCode(),
                    q.getTitle(),
                    q.getDurationMinutes(),
                    String.format("%.1f", q.getTotalMarks()),
                    q.getQuestionCount(),
                    q.isActive() ? "ACTIVE" : "INACTIVE",
                    q.getCreatedAt() != null ? q.getCreatedAt() : "-"
            });
        }
    }

    private Quiz getSelectedQuiz() {
        if (tblQuizzes == null) return null;
        int row = tblQuizzes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int quizId = (int) tblQuizzes.getValueAt(row, 0);
        return quizDAO.getQuizById(quizId);
    }

    private void handleCreateQuiz() {
        String courseCode = (String) cbQuizCourse.getSelectedItem();
        if (courseCode == null || courseCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an assigned course first.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtTitle = new JTextField();
        JTextArea txtDesc = new JTextArea(3, 20);
        JTextField txtDuration = new JTextField("30");
        JTextField txtMarks = new JTextField("100.0");

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Quiz Title:"));
        form.add(txtTitle);
        form.add(new JLabel("Description / Instructions:"));
        form.add(new JScrollPane(txtDesc));
        form.add(new JLabel("Duration (Minutes):"));
        form.add(txtDuration);
        form.add(new JLabel("Total Marks:"));
        form.add(txtMarks);

        int opt = JOptionPane.showConfirmDialog(this, form, "Create New Online Quiz (" + courseCode + ")", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String title = txtTitle.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Quiz title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int duration = Integer.parseInt(txtDuration.getText().trim());
                double marks = Double.parseDouble(txtMarks.getText().trim());
                int lecId = (lecturer != null) ? lecturer.getLecturerId() : 1;

                Quiz q = new Quiz(0, courseCode, lecId, title, txtDesc.getText().trim(), duration, marks, true, null);
                int genId = quizDAO.createQuiz(q);
                if (genId > 0) {
                    JOptionPane.showMessageDialog(this, "Quiz created successfully! Now you can add questions.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadQuizzesData();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duration and Total Marks must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (QuizOperationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error Creating Quiz", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleManageQuestions() {
        Quiz q = getSelectedQuiz();
        if (q != null) {
            QuizQuestionDialog dlg = new QuizQuestionDialog(this, q, quizDAO);
            dlg.setVisible(true);
            loadQuizzesData(); // refresh questions count
        }
    }

    private void handlePreviewQuiz() {
        Quiz q = getSelectedQuiz();
        if (q != null) {
            QuizTestRunnerDialog dlg = new QuizTestRunnerDialog(this, q, quizDAO);
            dlg.setVisible(true);
        }
    }

    private void handleViewSubmissions() {
        Quiz q = getSelectedQuiz();
        if (q != null) {
            QuizResultsDialog dlg = new QuizResultsDialog(this, q, quizDAO);
            dlg.setVisible(true);
        }
    }

    private void handleExportQuizPaper() {
        Quiz q = getSelectedQuiz();
        if (q == null) return;

        List<QuizQuestion> questions = quizDAO.getQuestionsByQuizId(q.getQuizId());
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This quiz has no questions yet. Add questions first!", "Empty Quiz", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int includeKeyOpt = JOptionPane.showConfirmDialog(this,
                "Do you want to include the Confidential Answer Key at the end of the report?",
                "Include Answer Key?", JOptionPane.YES_NO_OPTION);
        boolean includeKey = (includeKeyOpt == JOptionPane.YES_OPTION);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Quiz_" + q.getCourseCode() + "_" + q.getQuizId() + "_Paper.txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = fileChooser.getSelectedFile();
            boolean ok = QuizFileHandler.exportQuizPaper(q, questions, target, includeKey);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Quiz Paper exported to:\n" + target.getAbsolutePath(),
                        "Export Succeeded (Lesson 06)", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export quiz paper.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleBackupQuizBinary() {
        Quiz q = getSelectedQuiz();
        if (q == null) return;

        List<QuizQuestion> questions = quizDAO.getQuestionsByQuizId(q.getQuizId());
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Quiz_" + q.getCourseCode() + "_" + q.getQuizId() + "_Backup.dat"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = fileChooser.getSelectedFile();
            boolean ok = QuizFileHandler.backupQuizToBinary(q, questions, target);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Binary backup created successfully at:\n" + target.getAbsolutePath(),
                        "Binary Backup (Lesson 06)", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write binary backup file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleToggleQuizStatus() {
        Quiz q = getSelectedQuiz();
        if (q == null) return;

        boolean newStatus = !q.isActive();
        try {
            boolean updated = quizDAO.toggleQuizStatus(q.getQuizId(), newStatus);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Quiz is now " + (newStatus ? "ACTIVE" : "INACTIVE") + ".", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadQuizzesData();
            }
        } catch (QuizOperationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteQuiz() {
        Quiz q = getSelectedQuiz();
        if (q == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Quiz ID " + q.getQuizId() + " (" + q.getTitle() + ")?\nAll questions and student attempts will also be deleted.",
                "Confirm Quiz Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = quizDAO.deleteQuiz(q.getQuizId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Quiz deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadQuizzesData();
                }
            } catch (QuizOperationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
            }
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
