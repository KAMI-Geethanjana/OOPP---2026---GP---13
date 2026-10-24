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

/**
 * UndergraduateDashboard - Interactive student portal for FTMS.
 * Faithfully styled to match the Student Portal UI specification:
 * - Dark navy navigation sidebar with purple active pills
 * - Clean white card containers with rounded borders
 * - Attendance summary with 80% eligibility status
 * - Medical request submission and tracking
 * - Enrolled courses and lecture materials
 * - Grades and SGPA/CGPA computation per UGC Circular 12-2024
 *
 * Authored under: Module TG2082 (W.D.P Kaveesha)
 */
public class UndergraduateDashboard extends JFrame {

    private Undergraduate student;
    private UndergraduateDAO studentDAO;
    private NoticeDAO noticeDAO;

    private JPanel contentCards;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton btnNavProfile;
    private JButton btnNavDashboard;
    private JButton btnNavCourses;
    private JButton btnNavAttendance;
    private JButton btnNavMedical;
    private JButton btnNavGrades;
    private JButton btnNavNotices;
    private JButton btnNavTimetable;

    // Top bar labels
    private JLabel lblWelcomeName;
    private JLabel lblRegNo;

    // Dashboard Overview labels
    private JLabel lblGpaVal;
    private JLabel lblCoursesVal;
    private JLabel lblAttendanceVal;
    private JLabel lblMedicalsVal;

    // Attendance components
    private JTable tblAttendance;
    private DefaultTableModel attModel;

    // Medical components
    private JTable tblMedical;
    private DefaultTableModel medModel;
    private JComboBox<String> cbMedCourse;
    private JTextField txtMedDate;
    private JTextArea txtMedReason;
    private JTextField txtMedPath;
    private java.io.File selectedMedicalCertificate;

    // Courses & Materials components
    private JTable tblCourses;
    private DefaultTableModel coursesModel;
    private JTable tblMaterials;
    private DefaultTableModel materialsModel;

    // Grades components
    private JTable tblGrades;
    private DefaultTableModel gradesModel;
    private JLabel lblSgpa;

    // Timetable components
    private JTable tblTimetable;
    private DefaultTableModel timetableModel;

    // Notices components
    private JTable tblNotices;
    private DefaultTableModel noticesModel;

    // Profile components
    private JTextField txtProfEmail, txtProfPhone;
    private JLabel lblProfRegNo, lblProfBatch, lblProfDept, lblProfStatus;
    private JLabel lblProfilePicture;
    private JLabel lblAvatar;

    public UndergraduateDashboard(Undergraduate student) {
        this.student = student;
        this.studentDAO = new UndergraduateDAO();
        this.noticeDAO = new NoticeDAO();

        if (student != null && student.getDepartmentName() == null) {
            Undergraduate fullStu = studentDAO.getUndergraduateByUserId(student.getUserId());
            if (fullStu != null) {
                this.student = fullStu;
            }
        }

        initUI();
    }

    private void initUI() {
        setTitle("Student Portal - Faculty of Technology");
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
        contentCards.add(createDashboardOverviewPanel(), "DASHBOARD");
        contentCards.add(createProfilePanel(), "PROFILE");
        contentCards.add(createCoursesPanel(), "COURSES");
        contentCards.add(createMedicalPanel(), "MEDICAL");
        contentCards.add(createGradesPanel(), "GRADES");
        contentCards.add(createTimetablePanel(), "TIMETABLE");
        contentCards.add(createNoticesPanel(), "NOTICES");

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Initial load
        loadAttendanceData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblLogo = new JLabel("  STUDENT PORTAL", SwingConstants.LEFT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(0, 24, 25, 0));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);

        btnNavProfile = UITheme.createSidebarButton("My Profile", false);
        btnNavDashboard = UITheme.createSidebarButton("Dashboard", false);
        btnNavCourses = UITheme.createSidebarButton("Courses", false);
        btnNavAttendance = UITheme.createSidebarButton("Attendance", true); // Matches photo default
        btnNavMedical = UITheme.createSidebarButton("Medical", false);
        btnNavGrades = UITheme.createSidebarButton("Grades & GPA", false);
        btnNavNotices = UITheme.createSidebarButton("Notices", false);
        btnNavTimetable = UITheme.createSidebarButton("Timetable", false);

        btnNavProfile.addActionListener(e -> { switchCard("PROFILE", btnNavProfile); loadProfileData(); });
        btnNavDashboard.addActionListener(e -> { switchCard("DASHBOARD", btnNavDashboard); loadDashboardOverview(); });
        btnNavCourses.addActionListener(e -> { switchCard("COURSES", btnNavCourses); loadCoursesData(); });
        btnNavAttendance.addActionListener(e -> { switchCard("ATTENDANCE", btnNavAttendance); loadAttendanceData(); });
        btnNavMedical.addActionListener(e -> { switchCard("MEDICAL", btnNavMedical); loadMedicalData(); });
        btnNavGrades.addActionListener(e -> { switchCard("GRADES", btnNavGrades); loadGradesData(); });
        btnNavNotices.addActionListener(e -> { switchCard("NOTICES", btnNavNotices); loadNoticesData(); });
        btnNavTimetable.addActionListener(e -> { switchCard("TIMETABLE", btnNavTimetable); loadTimetableData(); });

        sidebar.add(btnNavProfile);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavCourses);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavAttendance);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavMedical);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavGrades);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavNotices);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavTimetable);

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

        String sName = student != null ? student.getFullName() : "Kasun";
        lblWelcomeName = new JLabel("Welcome, " + sName);
        lblWelcomeName.setFont(UITheme.FONT_TITLE);
        lblWelcomeName.setForeground(UITheme.TEXT_DARK);

        String reg = student != null ? student.getRegNo() : "TG/2024/2082";
        lblRegNo = new JLabel(reg);
        lblRegNo.setFont(UITheme.FONT_SUBTITLE);
        lblRegNo.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(lblWelcomeName);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblRegNo);

        header.add(textPanel, BorderLayout.WEST);

        // Circular Avatar icon on right
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        avatarPanel.setOpaque(false);
        lblAvatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    int diameter = Math.min(getWidth(), getHeight()) - 2;
                    int left = (getWidth() - diameter) / 2;
                    int top = (getHeight() - diameter) / 2;
                    java.awt.Shape circle = new java.awt.geom.Ellipse2D.Double(
                            left, top, diameter, diameter);
                    g2.setColor(new Color(203, 213, 225));
                    g2.fill(circle);

                    String path = student == null ? null : student.getProfilePic();
                    java.io.File photo = path == null ? null : new java.io.File(path);
                    Image image = null;
                    if (photo != null && photo.isFile()) {
                        ImageIcon icon = new ImageIcon(photo.getAbsolutePath());
                        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                            image = icon.getImage();
                        }
                    }

                    if (image != null) {
                        int imageWidth = image.getWidth(this);
                        int imageHeight = image.getHeight(this);
                        double scale = Math.max((double) diameter / imageWidth,
                                (double) diameter / imageHeight);
                        int width = (int) Math.ceil(imageWidth * scale);
                        int height = (int) Math.ceil(imageHeight * scale);
                        java.awt.Shape previousClip = g2.getClip();
                        g2.clip(circle);
                        g2.drawImage(image, left + (diameter - width) / 2,
                                top + (diameter - height) / 2, width, height, this);
                        g2.setClip(previousClip);
                    } else {
                        String name = student == null ? null : student.getFullName();
                        String initial = (name == null || name.trim().isEmpty())
                                ? "?" : name.trim().substring(0, 1).toUpperCase();
                        g2.setColor(new Color(71, 85, 105));
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                        FontMetrics metrics = g2.getFontMetrics();
                        int textX = left + (diameter - metrics.stringWidth(initial)) / 2;
                        int textY = top + (diameter - metrics.getHeight()) / 2
                                + metrics.getAscent();
                        g2.drawString(initial, textX, textY);
                    }
                } finally {
                    g2.dispose();
                }
            }
        };
        lblAvatar.setPreferredSize(new Dimension(42, 42));
        avatarPanel.add(lblAvatar);
        header.add(avatarPanel, BorderLayout.EAST);

        return header;
    }

    private void switchCard(String name, JButton activeBtn) {
        cardLayout.show(contentCards, name);
        JButton[] buttons = {btnNavProfile, btnNavDashboard, btnNavCourses, btnNavAttendance, btnNavMedical, btnNavGrades, btnNavNotices, btnNavTimetable};
        for (JButton b : buttons) {
            boolean isActive = (b == activeBtn);
            b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
            b.setForeground(isActive ? UITheme.SIDEBAR_TEXT_ACTIVE : UITheme.SIDEBAR_TEXT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. ATTENDANCE PANEL (Matching media_1791519097574.jpg)
    // ==========================================
    private JPanel createAttendancePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Attendance Summary (80% Requirement)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Total Sessions", "Present", "Percentage", "Exam Eligibility"};
        attModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAttendance = new JTable(attModel);
        UITheme.styleTable(tblAttendance);

        JScrollPane scroll = new JScrollPane(tblAttendance);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 2. DASHBOARD OVERVIEW PANEL
    // ==========================================
    private JPanel createDashboardOverviewPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 20));

        JLabel lblTitle = new JLabel("Academic Progress Overview");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);

        lblCoursesVal = new JLabel("0", SwingConstants.CENTER);
        lblGpaVal = new JLabel("0.00", SwingConstants.CENTER);
        lblAttendanceVal = new JLabel("0%", SwingConstants.CENTER);
        lblMedicalsVal = new JLabel("0", SwingConstants.CENTER);

        statsGrid.add(createStatCard("Enrolled Courses", lblCoursesVal, new Color(59, 130, 246)));
        statsGrid.add(createStatCard("Credit-weighted GPA", lblGpaVal, new Color(16, 185, 129)));
        statsGrid.add(createStatCard("Average Attendance", lblAttendanceVal, new Color(139, 92, 246)));
        statsGrid.add(createStatCard("Approved Medicals", lblMedicalsVal, new Color(245, 158, 11)));

        card.add(statsGrid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatCard(String title, JLabel lblValue, Color accent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BODY_BOLD);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(UITheme.TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 3. MEDICAL REQUESTS PANEL (Matching WhatsApp Image 2026-10-07 at 12.30.40.jpeg)
    // ==========================================
    private JPanel createMedicalPanel() {
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

        txtMedReason = new JTextArea(2, 30);
        txtMedReason.setFont(UITheme.FONT_BODY);
        txtMedReason.setBorder(BorderFactory.createTitledBorder("Describe medical reason"));
        formPanel.add(new JScrollPane(txtMedReason), BorderLayout.CENTER);

        // Two rows prevent the Choose File button from being pushed off-screen.
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        fieldsPanel.setOpaque(false);

        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        firstRow.setOpaque(false);
        firstRow.add(new JLabel("Course:"));
        cbMedCourse = new JComboBox<>();
        cbMedCourse.setPreferredSize(new Dimension(120, 32));
        firstRow.add(cbMedCourse);

        firstRow.add(new JLabel("Medical Date:"));
        txtMedDate = UITheme.createTextField(10);
        txtMedDate.setText(new Date(System.currentTimeMillis()).toString());
        firstRow.add(txtMedDate);

        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        secondRow.setOpaque(false);
        secondRow.add(new JLabel("Medical Certificate:"));
        txtMedPath = UITheme.createTextField(15);
        txtMedPath.setText("No file selected");
        txtMedPath.setEditable(false);
        secondRow.add(txtMedPath);

        JButton btnChooseMedical = UITheme.createButton("Choose File", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnChooseMedical.addActionListener(e -> chooseMedicalCertificate());
        secondRow.add(btnChooseMedical);

        JButton btnSubmit = UITheme.createButton("Submit Request", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnSubmit.addActionListener(e -> handleSubmitMedical());
        secondRow.add(btnSubmit);

        JButton btnRefresh = UITheme.createButton("Refresh List", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> loadMedicalData());
        secondRow.add(btnRefresh);

        fieldsPanel.add(firstRow);
        fieldsPanel.add(secondRow);
        formPanel.add(fieldsPanel, BorderLayout.SOUTH);
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        JLabel lblTblTitle = new JLabel("My Medical Records");
        lblTblTitle.setFont(UITheme.FONT_BODY_BOLD);
        lblTblTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        tableWrapper.add(lblTblTitle, BorderLayout.NORTH);

        String[] cols = {"Record ID", "Course", "Date", "Description / Reason", "Document", "Status"};
        medModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMedical = new JTable(medModel);
        UITheme.styleTable(tblMedical);
        tableWrapper.add(new JScrollPane(tblMedical), BorderLayout.CENTER);

        JButton btnDeleteMedical = UITheme.createButton(
                "Delete Selected Medical", new Color(185, 28, 28), Color.WHITE);
        btnDeleteMedical.addActionListener(e -> handleDeleteMedical());
        JPanel medicalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        medicalActions.setOpaque(false);
        medicalActions.add(btnDeleteMedical);
        tableWrapper.add(medicalActions, BorderLayout.SOUTH);


        centerPanel.add(tableWrapper, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 4. COURSES & MATERIALS PANEL
    // ==========================================
    private JPanel createCoursesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(2, 1, 0, 15));

        // Upper: Enrolled Courses
        JPanel pnlEnrolled = new JPanel(new BorderLayout(0, 8));
        pnlEnrolled.setOpaque(false);
        JLabel lblEnrolled = new JLabel("My Enrolled Course Modules");
        lblEnrolled.setFont(UITheme.FONT_HEADER);
        pnlEnrolled.add(lblEnrolled, BorderLayout.NORTH);

        String[] cols1 = {"Course Code", "Course Title", "Theory Credits", "Practical Credits", "Total Credits", "Lecturer"};
        coursesModel = new DefaultTableModel(cols1, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCourses = new JTable(coursesModel);
        UITheme.styleTable(tblCourses);
        pnlEnrolled.add(new JScrollPane(tblCourses), BorderLayout.CENTER);
        card.add(pnlEnrolled);

        // Lower: Lecture Materials
        JPanel pnlMaterials = new JPanel(new BorderLayout(0, 8));
        pnlMaterials.setOpaque(false);
        JLabel lblMats = new JLabel("Course Materials & Lecture Notes");
        lblMats.setFont(UITheme.FONT_HEADER);
        pnlMaterials.add(lblMats, BorderLayout.NORTH);

        String[] cols2 = {"Material ID", "Course Code", "Title", "File / Resource Path", "Uploaded Date"};
        materialsModel = new DefaultTableModel(cols2, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMaterials = new JTable(materialsModel);
        UITheme.styleTable(tblMaterials);
        pnlMaterials.add(new JScrollPane(tblMaterials), BorderLayout.CENTER);
        card.add(pnlMaterials);

        return card;
    }

    // ==========================================
    // 5. GRADES & GPA PANEL
    // ==========================================
    private JPanel createGradesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Academic Grades & GPA (UGC Circular 12-2024)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        top.add(lblTitle, BorderLayout.WEST);

        lblSgpa = new JLabel("Credit-weighted GPA: 0.00");
        lblSgpa.setFont(UITheme.FONT_BODY_BOLD);
        lblSgpa.setForeground(UITheme.PRIMARY_BLUE);
        top.add(lblSgpa, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Attempt", "Grade", "Grade Point (GPV)"};
        gradesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGrades = new JTable(gradesModel);
        UITheme.styleTable(tblGrades);
        card.add(new JScrollPane(tblGrades), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 6. TIMETABLE PANEL (Matching WhatsApp Image 2026-10-07 at 12.30.40 (2).jpeg)
    // ==========================================
    private JPanel createTimetablePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("My Timetable");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Day", "Start", "End", "Course", "Venue"};
        timetableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTimetable = new JTable(timetableModel);
        UITheme.styleTable(tblTimetable);
        card.add(new JScrollPane(tblTimetable), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 7. NOTICES PANEL
    // ==========================================
    private JPanel createNoticesPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        JLabel lblTitle = new JLabel("Faculty Notices & Announcements");
        lblTitle.setFont(UITheme.FONT_HEADER);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Notice ID", "Title", "Content", "Date", "Posted By"};
        noticesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNotices = new JTable(noticesModel);
        UITheme.styleTable(tblNotices);
        card.add(new JScrollPane(tblNotices), BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 8. PROFILE PANEL
    // ==========================================
    private JPanel createProfilePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("My Student Profile (FR-AUTH-04)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Registration No:"), gbc);
        lblProfRegNo = new JLabel(student != null ? student.getRegNo() : "");
        lblProfRegNo.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(lblProfRegNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Academic Department:"), gbc);
        lblProfDept = new JLabel(student != null && student.getDepartmentName() != null ? student.getDepartmentName() : "DICT");
        lblProfDept.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(lblProfDept, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Batch Year:"), gbc);
        lblProfBatch = new JLabel(student != null ? String.valueOf(student.getBatchYear()) : "2024");
        lblProfBatch.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 3;
        card.add(lblProfBatch, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("Academic Status:"), gbc);
        lblProfStatus = new JLabel(student != null ? student.getStatus() : "ACTIVE");
        lblProfStatus.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 4;
        card.add(lblProfStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Contact Number:"), gbc);
        txtProfPhone = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 5;
        card.add(txtProfPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        card.add(new JLabel("Email Address:"), gbc);
        txtProfEmail = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 6;
        card.add(txtProfEmail, gbc);

        lblProfilePicture = new JLabel("No profile photo selected");
        gbc.gridx = 0; gbc.gridy = 7;
        card.add(new JLabel("Profile Picture:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        card.add(lblProfilePicture, gbc);

        JButton btnPicture = UITheme.createButton("Choose Profile Picture", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnPicture.addActionListener(e -> handleProfilePicture());
        gbc.gridx = 1; gbc.gridy = 8;
        card.add(btnPicture, gbc);

        JButton btnDeletePicture = UITheme.createButton("Remove Profile Picture", new Color(185, 28, 28), Color.WHITE);
        btnDeletePicture.addActionListener(e -> handleRemoveProfilePicture());
        gbc.gridx = 1; gbc.gridy = 9;
        card.add(btnDeletePicture, gbc);

        JButton btnUpdate = UITheme.createButton("Update Contact Details", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnUpdate.addActionListener(e -> handleUpdateProfile());
        gbc.gridx = 1; gbc.gridy = 10;
        card.add(btnUpdate, gbc);

        return card;
    }

    // ==========================================
    // DATA LOADERS & ACTIONS
    // ==========================================
    private void loadAttendanceData() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Map<String, Object>> list = studentDAO.getAttendanceSummary(stuId);
        attModel.setRowCount(0);

        for (Map<String, Object> m : list) {
            boolean elig = (Boolean) m.get("isEligible");
            attModel.addRow(new Object[]{
                    m.get("courseCode"),
                    m.get("totalSessions"),
                    m.get("presentCount"),
                    m.get("excusedPercentage") + "%",
                    elig ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
        }
    }

    private void loadDashboardOverview() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Course> courses = studentDAO.getEnrolledCourses(stuId);
        List<Map<String, Object>> attList = studentDAO.getAttendanceSummary(stuId);
        List<Medical> meds = studentDAO.getMedicalRecords(stuId);
        List<Result> results = studentDAO.getResultsForStudent(stuId);

        lblCoursesVal.setText(String.valueOf(courses.size()));

        double totalAtt = 0.0;
        for (Map<String, Object> a : attList) {
            totalAtt += (Double) a.get("excusedPercentage");
        }
        double avgAtt = attList.isEmpty() ? 0.0 : totalAtt / attList.size();
        lblAttendanceVal.setText((Math.round(avgAtt * 10.0) / 10.0) + "%");

        int approvedMeds = 0;
        for (Medical m : meds) {
            if ("APPROVED".equalsIgnoreCase(m.getStatus())) approvedMeds++;
        }
        lblMedicalsVal.setText(String.valueOf(approvedMeds));

        double gpa = studentDAO.calculateGpa(stuId);
        lblGpaVal.setText(String.format("%.2f", gpa));
    }

    private void loadMedicalData() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Medical> list = studentDAO.getMedicalRecords(stuId);
        medModel.setRowCount(0);

        for (Medical m : list) {
            medModel.addRow(new Object[]{
                    m.getMedicalId(),
                    m.getCourseCode(),
                    m.getMedicalDate(),
                    m.getReason(),
                    m.getDocumentPath(),
                    m.getStatus()
            });
        }

        // Populate course dropdown for submission
        List<Course> courses = studentDAO.getEnrolledCourses(stuId);
        cbMedCourse.removeAllItems();
        for (Course c : courses) {
            cbMedCourse.addItem(c.getCourseCode());
        }
    }

    // Let the student choose a medical certificate instead of typing a file path.
    private void chooseMedicalCertificate() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Select Medical Certificate");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Medical files (JPG, JPEG, PNG, PDF)", "jpg", "jpeg", "png", "pdf"));

        int result;
        try {
            result = chooser.showOpenDialog(this);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot open file chooser: " + ex.getMessage(),
                    "File Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = chooser.getSelectedFile();

            if (file != null && file.isFile()) {
                selectedMedicalCertificate = file;
                txtMedPath.setText(file.getName());
            }
        }
    }

    // Only the signed-in student's pending requests may be deleted.
    private void handleDeleteMedical() {
        if (student == null) return;
        int row = tblMedical.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table.");
            return;
        }
        int modelRow = tblMedical.convertRowIndexToModel(row);
        Object idValue = medModel.getValueAt(modelRow, 0);
        Object statusValue = medModel.getValueAt(modelRow, 5);
        if (!"PENDING".equalsIgnoreCase(String.valueOf(statusValue))) {
            JOptionPane.showMessageDialog(this,
                    "Only PENDING medical requests can be deleted.",
                    "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int medicalId;
        try {
            medicalId = Integer.parseInt(String.valueOf(idValue));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid medical record ID.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete pending medical request #" + medicalId + "? This cannot be undone.",
                "Confirm Medical Deletion", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        // The DAO verifies student ownership and PENDING status in the DELETE query.
        if (studentDAO.deletePendingMedical(medicalId, student.getStudentId())) {
            // Delete only a certificate stored by this application, never the original upload.
            Object savedPath = medModel.getValueAt(modelRow, 4);
            deleteOwnedMedicalCertificate(savedPath == null ? null : String.valueOf(savedPath));
            loadMedicalData();
            JOptionPane.showMessageDialog(this, "Medical request deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not delete this request. It may have already been reviewed.",
                    "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            loadMedicalData();
        }
    }

    private void deleteOwnedMedicalCertificate(String path) {
        if (path == null || path.isBlank()) return;
        try {
            java.nio.file.Path folder = java.nio.file.Paths.get("medical_certificates")
                    .toAbsolutePath().normalize();
            java.nio.file.Path file = java.nio.file.Paths.get(path)
                    .toAbsolutePath().normalize();
            if (folder.equals(file.getParent()) && file.getFileName().toString()
                    .startsWith("medical_" + student.getStudentId() + "_")) {
                java.nio.file.Files.deleteIfExists(file);
            }
        } catch (java.io.IOException | java.nio.file.InvalidPathException ex) {
            System.err.println("Medical file cleanup failed: " + ex.getMessage());
        }
    }

    private void handleSubmitMedical() {
        String course = (String) cbMedCourse.getSelectedItem();
        String reason = txtMedReason.getText().trim();

        if (course == null || course.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please select a course!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe the medical reason!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedMedicalCertificate == null || !selectedMedicalCertificate.isFile()) {
            JOptionPane.showMessageDialog(this, "Please choose a medical certificate!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date dt;
        try {
            dt = Date.valueOf(txtMedDate.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Please enter date in format YYYY-MM-DD", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.nio.file.Path target = null;
        try {
            java.nio.file.Path folder = java.nio.file.Paths.get("medical_certificates").toAbsolutePath();
            java.nio.file.Files.createDirectories(folder);
            String originalName = selectedMedicalCertificate.getName();
            int dot = originalName.lastIndexOf('.');
            String ext = dot < 0 ? "" : originalName.substring(dot).toLowerCase(java.util.Locale.ROOT);
            if (!java.util.Arrays.asList(".jpg", ".jpeg", ".png", ".pdf").contains(ext)) {
                JOptionPane.showMessageDialog(this, "Please select a JPG, PNG or PDF file.");
                return;
            }
            // Unique name prevents one student's document overwriting another's.
            target = folder.resolve("medical_" + student.getStudentId() + "_"
                    + java.util.UUID.randomUUID() + ext);
            java.nio.file.Files.copy(selectedMedicalCertificate.toPath(), target);

            Medical medical = new Medical(0, student.getStudentId(), course, dt,
                    reason, target.toString(), "PENDING", null);
            if (studentDAO.submitMedical(medical)) {
                JOptionPane.showMessageDialog(this, "Medical request submitted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                txtMedReason.setText("");
                selectedMedicalCertificate = null;
                txtMedPath.setText("No file selected");
                txtMedPath.setToolTipText(null);
                loadMedicalData();
            } else {
                java.nio.file.Files.deleteIfExists(target);
                JOptionPane.showMessageDialog(this, "Medical request could not be saved to the database.",
                        "Submission Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save medical certificate: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCoursesData() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Course> list = studentDAO.getEnrolledCourses(stuId);
        coursesModel.setRowCount(0);

        for (Course c : list) {
            coursesModel.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getCourseTitle(),
                    c.getCreditTheory(),
                    c.getCreditPractical(),
                    c.getTotalCredits(),
                    c.getLecturerName() != null ? c.getLecturerName() : "TBA"
            });
        }

        List<CourseMaterial> mats = studentDAO.getCourseMaterialsForStudent(stuId);
        materialsModel.setRowCount(0);
        for (CourseMaterial m : mats) {
            materialsModel.addRow(new Object[]{
                    m.getMaterialId(),
                    m.getCourseCode(),
                    m.getTitle(),
                    m.getFilePath(),
                    m.getUploadedDate()
            });
        }
    }

    private void loadGradesData() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Result> list = studentDAO.getResultsForStudent(stuId);
        gradesModel.setRowCount(0);

        for (Result r : list) {
            gradesModel.addRow(new Object[]{
                    r.getCourseCode(), r.getAttemptType(),
                    r.getGrade(), r.getGradePoint()
            });
        }
        double gpa = studentDAO.calculateGpa(stuId);
        lblSgpa.setText(String.format("Credit-weighted GPA: %.2f", gpa));
    }

    private void loadTimetableData() {
        int stuId = student != null ? student.getStudentId() : 1;
        List<Timetable> list = studentDAO.getTimetableForStudent(stuId);
        timetableModel.setRowCount(0);

        for (Timetable t : list) {
            timetableModel.addRow(new Object[]{
                    t.getDayOfWeek(),
                    t.getStartTime(),
                    t.getEndTime(),
                    t.getCourseCode(),
                    t.getVenue()
            });
        }
    }

    private void loadNoticesData() {
        List<Notice> list = noticeDAO.getNoticesForRole("UNDERGRADUATE");
        noticesModel.setRowCount(0);

        for (Notice n : list) {
            noticesModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    n.getContent(),
                    n.getCreatedDate(),
                    n.getPostedBy()
            });
        }
    }

    private void loadProfileData() {
        if (student != null) {
            lblProfRegNo.setText(student.getRegNo());
            lblProfDept.setText(student.getDepartmentName() != null ? student.getDepartmentName() : "DICT");
            lblProfBatch.setText(String.valueOf(student.getBatchYear()));
            lblProfStatus.setText(student.getStatus());
            txtProfPhone.setText(student.getContactNo() != null ? student.getContactNo() : student.getPhoneNo());
            txtProfEmail.setText(student.getEmail());
            String path = student.getProfilePic();

            if (path != null && !path.isEmpty()
                    && new java.io.File(path).exists()) {

                ImageIcon icon = new ImageIcon(path);

                Image image = icon.getImage().getScaledInstance(
                        100, 100, Image.SCALE_SMOOTH);

                lblProfilePicture.setText("");
                lblProfilePicture.setIcon(new ImageIcon(image));

            } else {
                lblProfilePicture.setIcon(null);
                lblProfilePicture.setText("No profile photo selected");
            }
        }
    }

    private void handleUpdateProfile() {
        String phone = txtProfPhone.getText().trim();
        String email = txtProfEmail.getText().trim();

        if (studentDAO.updateProfile(student.getUserId(), phone, email)) {
            student.setEmail(email);
            student.setContactNo(phone);
            student.setPhoneNo(phone);
            JOptionPane.showMessageDialog(this, "Contact details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleProfilePicture() {
        if (student == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a profile picture");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        java.io.File chosen = chooser.getSelectedFile();
        if (!chosen.isFile()) return;
        try {
            // Copy to the app's own folder so the saved path survives moving the original image.
            java.nio.file.Path folder = java.nio.file.Paths.get("profile_pictures");
            java.nio.file.Files.createDirectories(folder);
            String filename = chosen.getName().toLowerCase(java.util.Locale.ROOT);
            String ext = filename.substring(filename.lastIndexOf('.'));
            // A unique filename prevents Java's image cache from displaying the old picture.
            java.nio.file.Path target = folder.resolve("student_" + student.getUserId()
                    + "_" + java.util.UUID.randomUUID() + ext).toAbsolutePath();
            java.nio.file.Files.copy(chosen.toPath(), target);
            String saved = target.toString();
            String oldPath = student.getProfilePic();
            if (studentDAO.updateProfilePicture(student.getUserId(), saved)) {
                student.setProfilePic(saved);
                loadProfileData();
                lblAvatar.repaint();
                // Delete only an old file owned by this application, never a user's original.
                deleteOwnedProfilePicture(oldPath);
                JOptionPane.showMessageDialog(this, "Profile picture updated successfully!");
            } else {
                java.nio.file.Files.deleteIfExists(target);
                JOptionPane.showMessageDialog(this, "Could not save profile picture to database.");
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot copy picture: " + ex.getMessage());
        }
    }

    private void handleRemoveProfilePicture() {
        if (student == null) return;
        String oldPath = student.getProfilePic();
        if (oldPath == null || oldPath.isBlank()) {
            JOptionPane.showMessageDialog(this, "No profile picture to remove.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Remove your profile picture and show your name's first letter?",
                "Remove Profile Picture", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        // The existing DAO accepts null and stores SQL NULL in users.profile_pic.
        if (studentDAO.updateProfilePicture(student.getUserId(), null)) {
            student.setProfilePic(null);
            loadProfileData();
            lblAvatar.repaint();
            deleteOwnedProfilePicture(oldPath);
            JOptionPane.showMessageDialog(this, "Profile picture removed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Could not remove profile picture from database.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOwnedProfilePicture(String path) {
        if (path == null || path.isBlank()) return;
        try {
            java.nio.file.Path folder = java.nio.file.Paths.get("profile_pictures")
                    .toAbsolutePath().normalize();
            java.nio.file.Path file = java.nio.file.Paths.get(path).toAbsolutePath().normalize();
            if (file.getParent() != null && file.getParent().equals(folder)
                    && file.getFileName().toString().startsWith("student_" + student.getUserId())) {
                java.nio.file.Files.deleteIfExists(file);
            }
        } catch (java.io.IOException | java.nio.file.InvalidPathException ex) {
            System.err.println("Old profile picture could not be deleted: " + ex.getMessage());
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

