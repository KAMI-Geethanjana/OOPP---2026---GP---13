
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

        // Top bar labels
        JLabel lblWelcomeName;
        JLabel lblRegNo;

        // Dashboard Overview labels
        JLabel lblGpaVal;
        JLabel lblCoursesVal;
        JLabel lblAttendanceVal;
        JLabel lblMedicalsVal;

        // Attendance components
        JTable tblAttendance;
        DefaultTableModel attModel;

        // Medical components
        JTable tblMedical;
        DefaultTableModel medModel;
        JComboBox<String> cbMedCourse;
        JTextField txtMedDate;
        JTextArea txtMedReason;
        JTextField txtMedPath;

        // Courses & Materials components
        JTable tblCourses;
        DefaultTableModel coursesModel;
        JTable tblMaterials;
        DefaultTableModel materialsModel;

        // Grades components
        JTable tblGrades;
        DefaultTableModel gradesModel;
        JLabel lblSgpa;

        // Timetable components
        JTable tblTimetable;
        DefaultTableModel timetableModel;

        // Notices components
        JTable tblNotices;
        DefaultTableModel noticesModel;

        // Profile components
        JTextField txtProfEmail, txtProfPhone;
        JLabel lblProfRegNo, lblProfBatch, lblProfDept, lblProfStatus;
        JLabel lblProfilePicture;

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
            JLabel lblAvatar = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(203, 213, 225));
                    g2.fillOval(0, 0, 40, 40);
                    g2.setColor(new Color(71, 85, 105));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    g2.drawString("S", 15, 25);
                    g2.dispose();
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
        JPanel createAttendancePanel() { return AttendancePanel.createAttendancePanel(this); }

        // ==========================================
        // 2. DASHBOARD OVERVIEW PANEL
        // ==========================================
        JPanel createDashboardOverviewPanel() { return DashboardPanel.createDashboardOverviewPanel(this); }

        JPanel createStatCard(String title, JLabel lblValue, Color accent) { return DashboardPanel.createStatCard(this, title, lblValue, accent); }

        // ==========================================
        // 3. MEDICAL REQUESTS PANEL (Matching WhatsApp Image 2026-10-07 at 12.30.40.jpeg)
        // ==========================================
        JPanel createMedicalPanel() { return MedicalPanel.createMedicalPanel(this); }

        // ==========================================
        // 4. COURSES & MATERIALS PANEL
        // ==========================================
        JPanel createCoursesPanel() { return CoursesPanel.createCoursesPanel(this); }

        // ==========================================
        // 5. GRADES & GPA PANEL
        // ==========================================
        JPanel createGradesPanel() { return GradesPanel.createGradesPanel(this); }

        // ==========================================
        // 6. TIMETABLE PANEL (Matching WhatsApp Image 2026-10-07 at 12.30.40 (2).jpeg)
        // ==========================================
        JPanel createTimetablePanel() { return TimetablePanel.createTimetablePanel(this); }

        // ==========================================
        // 7. NOTICES PANEL
        // ==========================================
        JPanel createNoticesPanel() { return NoticesPanel.createNoticesPanel(this); }

        // ==========================================
        // 8. PROFILE PANEL
        // ==========================================
        JPanel createProfilePanel() { return ProfilePanel.createProfilePanel(this); }

        // ==========================================
        // DATA LOADERS & ACTIONS
        // ==========================================
        void loadAttendanceData() { AttendancePanel.loadAttendanceData(this); }

        void loadDashboardOverview() { DashboardPanel.loadDashboardOverview(this); }

        void loadMedicalData() { MedicalPanel.loadMedicalData(this); }

        void handleSubmitMedical() { MedicalPanel.handleSubmitMedical(this); }

        void loadCoursesData() { CoursesPanel.loadCoursesData(this); }

        void loadGradesData() { GradesPanel.loadGradesData(this); }

        void loadTimetableData() { TimetablePanel.loadTimetableData(this); }

        void loadNoticesData() { NoticesPanel.loadNoticesData(this); }

        void loadProfileData() { ProfilePanel.loadProfileData(this); }

        void handleUpdateProfile() { ProfilePanel.handleUpdateProfile(this); }

        void handleProfilePicture() { ProfilePanel.handleProfilePicture(this); }

        private void handleSignOut() {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserSession.clearSession();
                dispose();
                new LoginFrame().setVisible(true);
            }
        }
    }


