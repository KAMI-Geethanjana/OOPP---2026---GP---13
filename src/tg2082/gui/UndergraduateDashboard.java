
package tg2082.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.util.UITheme;
import common.util.UserSession;
import tg2082.dao.UndergraduateDAO;
import tg2082.model.Undergraduate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;

public class UndergraduateDashboard extends JFrame {

    Undergraduate student;
    UndergraduateDAO studentDAO;
    NoticeDAO noticeDAO;

    JPanel contentCards;
    CardLayout cardLayout;

    // Sidebar buttons
    JButton btnNavProfile;
    JButton btnNavDashboard;
    JButton btnNavCourses;
    JButton btnNavAttendance;
    JButton btnNavMedical;
    JButton btnNavGrades;
    JButton btnNavNotices;
    JButton btnNavTimetable;
    JButton btnNavQuizzes;

    // Top header
    JLabel lblWelcomeName;
    JLabel lblRegNo;
    JLabel lblHeaderAvatar;

    // Dashboard overview
    JLabel lblGpaVal;
    JLabel lblCoursesVal;
    JLabel lblAttendanceVal;
    JLabel lblMedicalsVal;

    // Attendance
    JTable tblAttendance;
    DefaultTableModel attModel;
    JComboBox<String> cbAttendanceCourse;
    JLabel lblAttendanceTotals;

    // Medical
    JTable tblMedical;
    DefaultTableModel medModel;
    JComboBox<String> cbMedCourse;
    JTextField txtMedDate;
    JTextArea txtMedReason;
    JTextField txtMedPath;

    // Courses and materials
    JTable tblCourses;
    DefaultTableModel coursesModel;
    JTable tblMaterials;
    DefaultTableModel materialsModel;

    // Grades
    JTable tblGrades;
    DefaultTableModel gradesModel;
    JLabel lblSgpa;

    // Timetable
    JTable tblTimetable;
    DefaultTableModel timetableModel;

    // Notices
    JTable tblNotices;
    DefaultTableModel noticesModel;

    // Profile
    JTextField txtProfEmail;
    JTextField txtProfPhone;
    JLabel lblProfRegNo;
    JLabel lblProfBatch;
    JLabel lblProfDept;
    JLabel lblProfStatus;
    JLabel lblProfilePicture;

    public UndergraduateDashboard(Undergraduate student) {
        this.student = student;
        this.studentDAO = new UndergraduateDAO();
        this.noticeDAO = new NoticeDAO();

        if (student != null &&
                student.getDepartmentName() == null) {

            Undergraduate fullStu =
                    studentDAO.getUndergraduateByUserId(
                            student.getUserId()
                    );

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
        contentCards.setBorder(
                new EmptyBorder(15, 25, 20, 25)
        );

        contentCards.add(
                createAttendancePanel(), "ATTENDANCE"
        );
        contentCards.add(
                createDashboardOverviewPanel(), "DASHBOARD"
        );
        contentCards.add(
                createProfilePanel(), "PROFILE"
        );
        contentCards.add(
                createCoursesPanel(), "COURSES"
        );
        contentCards.add(
                createMedicalPanel(), "MEDICAL"
        );
        contentCards.add(
                createGradesPanel(), "GRADES"
        );
        contentCards.add(
                createTimetablePanel(), "TIMETABLE"
        );
        contentCards.add(
                createNoticesPanel(), "NOTICES"
        );
        contentCards.add(
                StudentQuizPanel.create(this), "QUIZZES"
        );

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Open My Profile initially
        switchCard("PROFILE", btnNavProfile);
        loadProfileData();
        refreshHeaderAvatar();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(
                new Dimension(240, 700)
        );
        sidebar.setBorder(
                new EmptyBorder(20, 0, 20, 0)
        );

        JLabel lblLogo = new JLabel(
                "  STUDENT PORTAL",
                SwingConstants.LEFT
        );
        lblLogo.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(
                new EmptyBorder(0, 24, 25, 0)
        );
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);

        // All buttons use dynamic highlighting
        btnNavProfile = createDynamicSidebarButton(
                "My Profile", true
        );
        btnNavDashboard = createDynamicSidebarButton(
                "Dashboard", false
        );
        btnNavCourses = createDynamicSidebarButton(
                "Courses", false
        );
        btnNavAttendance = createDynamicSidebarButton(
                "Attendance", false
        );
        btnNavMedical = createDynamicSidebarButton(
                "Medical", false
        );
        btnNavGrades = createDynamicSidebarButton(
                "Grades & GPA", false
        );
        btnNavNotices = createDynamicSidebarButton(
                "Notices", false
        );
        btnNavTimetable = createDynamicSidebarButton(
                "Timetable", false
        );
        btnNavQuizzes = createDynamicSidebarButton(
                "My Quizzes", false
        );

        btnNavProfile.addActionListener(e -> {
            switchCard("PROFILE", btnNavProfile);
            loadProfileData();
        });

        btnNavDashboard.addActionListener(e -> {
            switchCard("DASHBOARD", btnNavDashboard);
            loadDashboardOverview();
        });

        btnNavCourses.addActionListener(e -> {
            switchCard("COURSES", btnNavCourses);
            loadCoursesData();
        });

        btnNavAttendance.addActionListener(e -> {
            switchCard("ATTENDANCE", btnNavAttendance);
            loadAttendanceData();
        });

        btnNavMedical.addActionListener(e -> {
            switchCard("MEDICAL", btnNavMedical);
            loadMedicalData();
        });

        btnNavGrades.addActionListener(e -> {
            switchCard("GRADES", btnNavGrades);
            loadGradesData();
        });

        btnNavNotices.addActionListener(e -> {
            switchCard("NOTICES", btnNavNotices);
            loadNoticesData();
        });

        btnNavTimetable.addActionListener(e -> {
            switchCard("TIMETABLE", btnNavTimetable);
            loadTimetableData();
        });

        btnNavQuizzes.addActionListener(e -> {
            switchCard("QUIZZES", btnNavQuizzes);
            StudentQuizPanel.load(this);
        });

        JButton[] buttons = {
                btnNavProfile,
                btnNavDashboard,
                btnNavCourses,
                btnNavAttendance,
                btnNavMedical,
                btnNavGrades,
                btnNavNotices,
                btnNavTimetable,
                btnNavQuizzes
        };

        for (JButton button : buttons) {
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnSignOut = UITheme.createButton(
                "Sign Out",
                new Color(185, 28, 28),
                Color.WHITE
        );

        btnSignOut.setMaximumSize(
                new Dimension(200, 38)
        );
        btnSignOut.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSignOut.addActionListener(
                e -> handleSignOut()
        );

        JPanel signOutWrapper = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );
        signOutWrapper.setOpaque(false);
        signOutWrapper.add(btnSignOut);
        sidebar.add(signOutWrapper);

        return sidebar;
    }

    // Create sidebar buttons whose highlight can change
    private JButton createDynamicSidebarButton(
            String text,
            boolean initiallyActive
    ) {
        JButton button = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();

                try {
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    boolean active =
                            Boolean.TRUE.equals(
                                    getClientProperty("navActive")
                            );

                    if (active || getModel().isRollover()) {
                        g2.setColor(
                                active
                                        ? UITheme.SIDEBAR_ACTIVE
                                        : UITheme.SIDEBAR_HOVER
                        );

                        g2.fillRoundRect(
                                8,
                                2,
                                Math.max(0, getWidth() - 16),
                                Math.max(0, getHeight() - 4),
                                12,
                                12
                        );
                    }
                } finally {
                    g2.dispose();
                }

                super.paintComponent(g);
            }
        };

        button.putClientProperty(
                "navActive", initiallyActive
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        initiallyActive
                                ? Font.BOLD
                                : Font.PLAIN,
                        14
                )
        );

        button.setForeground(
                initiallyActive
                        ? UITheme.SIDEBAR_TEXT_ACTIVE
                        : UITheme.SIDEBAR_TEXT
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );
        button.setBorder(
                new EmptyBorder(10, 28, 10, 20)
        );
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        button.setMaximumSize(
                new Dimension(240, 44)
        );

        return button;
    }

    // Fix: Only the selected button stays highlighted
    private void switchCard(
            String name,
            JButton activeBtn
    ) {
        cardLayout.show(contentCards, name);

        JButton[] buttons = {
                btnNavProfile,
                btnNavDashboard,
                btnNavCourses,
                btnNavAttendance,
                btnNavMedical,
                btnNavGrades,
                btnNavNotices,
                btnNavTimetable,
                btnNavQuizzes
        };

        for (JButton button : buttons) {
            boolean active = button == activeBtn;

            button.putClientProperty(
                    "navActive", active
            );

            button.setFont(
                    new Font(
                            "Segoe UI",
                            active ? Font.BOLD : Font.PLAIN,
                            14
                    )
            );

            button.setForeground(
                    active
                            ? UITheme.SIDEBAR_TEXT_ACTIVE
                            : UITheme.SIDEBAR_TEXT
            );

            button.repaint();
        }
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(UITheme.CONTENT_BG);
        header.setBorder(
                new EmptyBorder(18, 25, 0, 25)
        );

        JPanel textPanel = new JPanel();
        textPanel.setLayout(
                new BoxLayout(textPanel, BoxLayout.Y_AXIS)
        );
        textPanel.setOpaque(false);

        String sName = student != null
                ? student.getFullName()
                : "Student";

        lblWelcomeName = new JLabel(
                "Welcome, " + sName
        );
        lblWelcomeName.setFont(UITheme.FONT_TITLE);
        lblWelcomeName.setForeground(
                UITheme.TEXT_DARK
        );

        String reg = student != null
                ? student.getRegNo()
                : "";

        lblRegNo = new JLabel(reg);
        lblRegNo.setFont(UITheme.FONT_SUBTITLE);
        lblRegNo.setForeground(
                UITheme.TEXT_MUTED
        );

        textPanel.add(lblWelcomeName);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblRegNo);

        header.add(
                textPanel, BorderLayout.WEST
        );

        JPanel avatarPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        avatarPanel.setOpaque(false);

        lblHeaderAvatar = new JLabel();
        lblHeaderAvatar.setPreferredSize(
                new Dimension(42, 42)
        );
        lblHeaderAvatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        avatarPanel.add(lblHeaderAvatar);
        header.add(
                avatarPanel, BorderLayout.EAST
        );

        return header;
    }

    // Show profile photo or student's first initial
    void refreshHeaderAvatar() {
        if (lblHeaderAvatar == null) {
            return;
        }

        String name = student == null
                ? "S"
                : student.getFullName();

        String initial =
                (name == null || name.trim().isEmpty())
                        ? "S"
                        : name.trim()
                        .substring(0, 1)
                        .toUpperCase(Locale.ROOT);

        String photo = student == null
                ? null
                : student.getProfilePic();

        BufferedImage picture = null;

        if (photo != null && !photo.trim().isEmpty()) {
            try {
                picture = ImageIO.read(new File(photo));
            } catch (IOException ignored) {
                // Display initial if photo is unavailable
            }
        }

        BufferedImage avatar = new BufferedImage(
                42,
                42,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = avatar.createGraphics();

        try {
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setColor(new Color(203, 213, 225));
            g.fillOval(1, 1, 40, 40);

            if (picture != null) {
                g.setClip(
                        new Ellipse2D.Double(
                                1, 1, 40, 40
                        )
                );

                double scale = Math.max(
                        40.0 / picture.getWidth(),
                        40.0 / picture.getHeight()
                );

                int w = (int) Math.ceil(
                        picture.getWidth() * scale
                );
                int h = (int) Math.ceil(
                        picture.getHeight() * scale
                );

                g.drawImage(
                        picture,
                        (42 - w) / 2,
                        (42 - h) / 2,
                        w,
                        h,
                        null
                );

                g.setClip(null);

            } else {
                g.setColor(new Color(71, 85, 105));
                g.setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                17
                        )
                );

                FontMetrics fm = g.getFontMetrics();

                g.drawString(
                        initial,
                        (42 - fm.stringWidth(initial)) / 2,
                        (42 - fm.getHeight()) / 2
                                + fm.getAscent()
                );
            }
        } finally {
            g.dispose();
        }

        lblHeaderAvatar.setIcon(
                new ImageIcon(avatar)
        );
        lblHeaderAvatar.setText("");
    }

    // Panel connections

    JPanel createAttendancePanel() {
        return AttendancePanel.createAttendancePanel(this);
    }

    JPanel createDashboardOverviewPanel() {
        return DashboardPanel
                .createDashboardOverviewPanel(this);
    }

    JPanel createStatCard(
            String title,
            JLabel lblValue,
            Color accent
    ) {
        return DashboardPanel.createStatCard(
                this, title, lblValue, accent
        );
    }

    JPanel createMedicalPanel() {
        return MedicalPanel.createMedicalPanel(this);
    }

    JPanel createCoursesPanel() {
        return CoursesPanel.createCoursesPanel(this);
    }

    JPanel createGradesPanel() {
        return GradesPanel.createGradesPanel(this);
    }

    JPanel createTimetablePanel() {
        return TimetablePanel.createTimetablePanel(this);
    }

    JPanel createNoticesPanel() {
        return NoticesPanel.createNoticesPanel(this);
    }

    JPanel createProfilePanel() {
        return ProfilePanel.createProfilePanel(this);
    }

    // Data loading

    void loadAttendanceData() {
        AttendancePanel.loadAttendanceData(this);
    }

    void loadDashboardOverview() {
        DashboardPanel.loadDashboardOverview(this);
    }

    void loadMedicalData() {
        MedicalPanel.loadMedicalData(this);
    }

    void handleSubmitMedical() {
        MedicalPanel.handleSubmitMedical(this);
    }

    void loadCoursesData() {
        CoursesPanel.loadCoursesData(this);
    }

    void loadGradesData() {
        GradesPanel.loadGradesData(this);
    }

    void loadTimetableData() {
        TimetablePanel.loadTimetableData(this);
    }

    void loadNoticesData() {
        NoticesPanel.loadNoticesData(this);
    }

    void loadProfileData() {
        ProfilePanel.loadProfileData(this);
        refreshHeaderAvatar();
    }

    void handleUpdateProfile() {
        ProfilePanel.handleUpdateProfile(this);
    }

    void handleProfilePicture() {
        ProfilePanel.handleProfilePicture(this);
        refreshHeaderAvatar();
    }

    private void handleSignOut() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
