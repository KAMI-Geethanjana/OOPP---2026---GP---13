package tg2086.gui;

import common.model.Course;
import common.model.Department;
import common.model.Notice;
import common.model.Timetable;
import common.model.User;
import tg2086.dao.AdminCourseDAO;
import tg2086.dao.AdminDAO;
import tg2086.dao.AdminNoticeDAO;
import tg2086.dao.AdminTimetableDAO;
import tg2086.dao.AdminUserDAO;
import tg2086.exception.AdminOperationException;
import tg2086.model.Admin;
import tg2086.util.AdminReportUtil;
import tg2086.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AdminDashboardUI - Complete Graphical Desktop Management Interface.
 * Implements Module TG2086 (Administrator & Notices Module).
 * Demonstrates:
 * - Multithreading (Lesson 08 Method 1: extends Thread & Method 2: implements Runnable)
 * - Thread Synchronization (synchronized block protecting shared notice ticker)
 * - Pure OOP Polymorphism, Casting, and instanceof
 * - Custom Exception Handling (AdminOperationException)
 * - Character Streams file export (AdminReportUtil)
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public class AdminDashboardUI extends BaseDashboardUI {

    // DAOs
    private final AdminDAO adminDAO = new AdminDAO();
    private final AdminUserDAO userDAO = new AdminUserDAO();
    private final AdminCourseDAO courseDAO = new AdminCourseDAO();
    private final AdminNoticeDAO noticeDAO = new AdminNoticeDAO();
    private final AdminTimetableDAO timetableDAO = new AdminTimetableDAO();

    // Logged in Admin session
    private final User currentAdminUser;

    // Multithreading (Lesson 08)
    private JLabel lblLiveClock;
    private JLabel lblNoticeTicker;
    private volatile boolean isClockRunning = true;
    private volatile boolean isTickerRunning = true;
    private Thread clockThread;
    private NoticeTickerThread noticeTickerThread;
    private final Object noticeLock = new Object();
    private List<String> latestNoticeTitles = new ArrayList<>();

    // Tables & Models
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;
    private JTable noticesTable;
    private DefaultTableModel noticesTableModel;
    private JTable timetablesTable;
    private DefaultTableModel timetablesTableModel;

    // KPI Metric Labels
    private JLabel lblTotalUsersVal;
    private JLabel lblStudentsVal;
    private JLabel lblLecturersVal;
    private JLabel lblTechOfficersVal;
    private JLabel lblCoursesVal;
    private JLabel lblNoticesVal;

    // Form inputs
    private JTextField txtUserUsername, txtUserFullName, txtUserEmail, txtUserPhone, txtUserExtraId, txtUserBatch;
    private JPasswordField txtUserPassword;
    private JComboBox<String> cmbUserRole;
    private JComboBox<DepartmentItem> cmbUserDepartment;
    private JCheckBox chkUserActive;
    private int selectedUserId = -1;

    private JTextField txtCourseCode, txtCourseTitle, txtTheoryCredits, txtPracticalCredits, txtLecturerId;
    private JComboBox<DepartmentItem> cmbCourseDepartment;

    private JTextField txtNoticeTitle;
    private JTextArea txtNoticeContent;
    private JComboBox<String> cmbNoticeAudience;
    private int selectedNoticeId = -1;

    private JTextField txtTtCourseCode, txtTtStartTime, txtTtEndTime, txtTtVenue;
    private JComboBox<DepartmentItem> cmbTtDepartment;
    private JComboBox<String> cmbTtSessionType;
    private JComboBox<String> cmbTtDayOfWeek;
    private int selectedTimetableId = -1;

    public AdminDashboardUI() {
        this(new Admin(1, "admin", "1234", "K.A.M.I. Geethanjana", "admin@fot.ruh.ac.lk", "0771234567", 1, "SUPER_ADMIN"));
    }

    public AdminDashboardUI(Admin adminUser) {
        super(
                "Faculty Technical Management System (FTMS) - Administrator Portal",
                "FTMS",
                "Admin Panel",
                adminUser != null ? adminUser.getFullName() : "Administrator",
                "System Administrator | TG/2024/2086"
        );

        this.currentAdminUser = (adminUser != null) ? adminUser : new Admin();

        initAdminUI();
        startBackgroundThreads();
        refreshAllData();
    }

    private void initAdminUI() {
        // 1. Live Digital Clock in Top Header (Method 2: Runnable)
        lblLiveClock = new JLabel("🕒 Initializing clock...", SwingConstants.RIGHT);
        lblLiveClock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLiveClock.setForeground(UITheme.PRIMARY_BLUE);
        lblLiveClock.setToolTipText("Multithreaded Real-Time System Clock (Runnable)");
        addHeaderAccessory(lblLiveClock);

        // 2. Navigation Tabs
        addNavItem("dashboard", "Dashboard Overview", buildDashboardOverviewPanel());
        addNavItem("users", "User Management", buildUsersPanel());
        addNavItem("courses", "Course Management", buildCoursesPanel());
        addNavItem("notices", "Notices", buildNoticesPanel());
        addNavItem("timetables", "Timetables", buildTimetablePanel());

        // 3. Footer Marquee Ticker (Method 1: extends Thread)
        JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
        footerPanel.setBackground(UITheme.SIDEBAR_BG);
        footerPanel.setBorder(new EmptyBorder(8, 20, 8, 20));

        lblNoticeTicker = new JLabel("📢 Announcements: Initializing notice ticker...");
        lblNoticeTicker.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNoticeTicker.setForeground(new Color(226, 232, 240));

        JLabel lblThreadStatus = new JLabel("⚡ System Online | Background Services Active");
        lblThreadStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblThreadStatus.setForeground(UITheme.SUCCESS_GREEN);

        footerPanel.add(lblNoticeTicker, BorderLayout.WEST);
        footerPanel.add(lblThreadStatus, BorderLayout.EAST);
        getContentPane().add(footerPanel, BorderLayout.SOUTH);
    }

    // =========================================================================
    // MULTITHREADING (Lesson 08 Implementation)
    // =========================================================================

    private void startBackgroundThreads() {
        // Method 1: Notice Marquee Ticker (extends Thread)
        noticeTickerThread = new NoticeTickerThread();
        noticeTickerThread.setName("Thread-NoticeTicker-TG2086");
        noticeTickerThread.setDaemon(true);
        noticeTickerThread.start();

        // Method 2: Live Digital Clock (implements Runnable)
        LiveClockRunnable clockRunnable = new LiveClockRunnable();
        clockThread = new Thread(clockRunnable, "Thread-DigitalClock-TG2086");
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private class NoticeTickerThread extends Thread {
        @Override
        public void run() {
            int noticeIndex = 0;
            while (isTickerRunning) {
                try {
                    String displayMessage;
                    synchronized (noticeLock) {
                        if (latestNoticeTitles.isEmpty()) {
                            displayMessage = "📢 Faculty Announcements: No active notices published yet.";
                        } else {
                            if (noticeIndex >= latestNoticeTitles.size()) {
                                noticeIndex = 0;
                            }
                            displayMessage = "📢 Faculty Announcements [" + (noticeIndex + 1) + "/" +
                                    latestNoticeTitles.size() + "]: " + latestNoticeTitles.get(noticeIndex);
                            noticeIndex++;
                        }
                    }

                    final String textToSet = displayMessage;
                    SwingUtilities.invokeLater(() -> {
                        if (lblNoticeTicker != null) {
                            lblNoticeTicker.setText(textToSet);
                        }
                    });

                    Thread.sleep(4000); // 4-second cycling
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class LiveClockRunnable implements Runnable {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy | hh:mm:ss a");

        @Override
        public void run() {
            while (isClockRunning) {
                try {
                    final String timeStr = "🕒 " + dateFormat.format(new Date());
                    SwingUtilities.invokeLater(() -> {
                        if (lblLiveClock != null) {
                            lblLiveClock.setText(timeStr);
                        }
                    });

                    Thread.sleep(1000); // 1-second update
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private void updateNoticeTickerCache(List<Notice> notices) {
        synchronized (noticeLock) {
            latestNoticeTitles.clear();
            if (notices != null) {
                for (Notice n : notices) {
                    if (n.getTitle() != null && !n.getTitle().trim().isEmpty()) {
                        latestNoticeTitles.add(n.getTitle().trim() + " (" + n.getTargetAudience() + ")");
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        isClockRunning = false;
        isTickerRunning = false;
        if (clockThread != null && clockThread.isAlive()) {
            clockThread.interrupt();
        }
        if (noticeTickerThread != null && noticeTickerThread.isAlive()) {
            noticeTickerThread.interrupt();
        }
        super.dispose();
    }

    // =========================================================================
    // PANEL BUILDERS
    // =========================================================================

    private JPanel buildDashboardOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Top KPI Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        metricsGrid.setOpaque(false);

        lblTotalUsersVal = new JLabel("0");
        lblStudentsVal = new JLabel("0");
        lblLecturersVal = new JLabel("0");
        lblTechOfficersVal = new JLabel("0");
        lblCoursesVal = new JLabel("0");
        lblNoticesVal = new JLabel("0");

        metricsGrid.add(UITheme.createMetricCard("TOTAL REGISTERED USERS", lblTotalUsersVal, UITheme.ACCENT_BLUE));
        metricsGrid.add(UITheme.createMetricCard("UNDERGRADUATE STUDENTS", lblStudentsVal, UITheme.ACCENT_PURPLE));
        metricsGrid.add(UITheme.createMetricCard("LECTURERS & ACADEMIC STAFF", lblLecturersVal, UITheme.ACCENT_EMERALD));
        metricsGrid.add(UITheme.createMetricCard("TECHNICAL OFFICERS", lblTechOfficersVal, UITheme.ACCENT_AMBER));
        metricsGrid.add(UITheme.createMetricCard("ACTIVE COURSES & MODULES", lblCoursesVal, UITheme.ACCENT_BLUE));
        metricsGrid.add(UITheme.createMetricCard("PUBLISHED ANNOUNCEMENTS", lblNoticesVal, UITheme.ACCENT_PURPLE));

        panel.add(metricsGrid, BorderLayout.NORTH);

        // System Overview Info Card
        JPanel infoContent = new JPanel(new GridLayout(5, 1, 0, 8));
        infoContent.setOpaque(false);
        infoContent.add(new JLabel("• System Architecture: MVC with Singleton Connection & Shared common base"));
        infoContent.add(new JLabel("• Active Session: " + currentAdminUser.getFullName() + " (" + currentAdminUser.getRoleDisplayName() + ")"));
        infoContent.add(new JLabel("• Multithreading Services: Notice Ticker (Thread) and Live Clock (Runnable) running smoothly"));
        infoContent.add(new JLabel("• Security Policy: Automatic lock after 5 consecutive failed login attempts"));
        infoContent.add(new JLabel("• Database: ftms_db running on MySQL localhost"));

        JPanel card = createSectionCard("System Status & Architecture Summary", infoContent);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Search and Filter Bar
        JTextField txtSearch = UITheme.createTextField(18);
        JComboBox<String> cmbFilterRole = UITheme.createComboBox(new String[]{"ALL", "UNDERGRADUATE", "LECTURER", "TECH_OFFICER", "ADMIN"});
        JButton btnSearch = UITheme.createButton("Filter", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnUnlock = UITheme.createButton("Unlock Account", UITheme.WARNING_AMBER, Color.WHITE);
        JButton btnExport = UITheme.createButton("Export CSV", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnSearch.addActionListener(e -> loadUsersData((String) cmbFilterRole.getSelectedItem()));
        btnUnlock.addActionListener(e -> handleUnlockUser());
        btnExport.addActionListener(e -> handleExportCSV(usersTable, "Users_Report.csv", "System Users Directory"));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(new JLabel("Filter Role:"));
        topBar.add(cmbFilterRole);
        topBar.add(btnSearch);
        topBar.add(btnUnlock);
        topBar.add(btnExport);

        panel.add(topBar, BorderLayout.NORTH);

        // Users Table
        String[] cols = {"User ID", "Username", "Full Name", "Email", "Phone", "Role", "Status", "Failed Logins", "Reg / Emp No", "Department"};
        usersTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersTable.getSelectedRow() != -1) {
                populateUserForm(usersTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = createTableScrollPane(usersTable);

        // User Management Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 10));

        JPanel formGrid = new JPanel(new GridLayout(4, 3, 12, 10));
        formGrid.setOpaque(false);

        txtUserUsername = UITheme.createTextField(10);
        txtUserPassword = UITheme.createPasswordField(10);
        txtUserFullName = UITheme.createTextField(15);
        txtUserEmail = UITheme.createTextField(15);
        txtUserPhone = UITheme.createTextField(10);
        txtUserExtraId = UITheme.createTextField(10);
        txtUserBatch = UITheme.createTextField(10);
        cmbUserRole = UITheme.createComboBox(new String[]{"UNDERGRADUATE", "LECTURER", "TECH_OFFICER", "ADMIN"});
        cmbUserDepartment = new JComboBox<>();
        chkUserActive = new JCheckBox("Active Account", true);
        chkUserActive.setOpaque(false);

        formGrid.add(createFormRow("Username", txtUserUsername));
        formGrid.add(createFormRow("Password (default 1234)", txtUserPassword));
        formGrid.add(createFormRow("Full Name", txtUserFullName));
        formGrid.add(createFormRow("Email Address", txtUserEmail));
        formGrid.add(createFormRow("Phone Number", txtUserPhone));
        formGrid.add(createFormRow("Role", cmbUserRole));
        formGrid.add(createFormRow("Reg No / Employee ID", txtUserExtraId));
        formGrid.add(createFormRow("Department", cmbUserDepartment));
        formGrid.add(createFormRow("Batch (Students)", txtUserBatch));
        formGrid.add(chkUserActive);

        // Form Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnAdd = UITheme.createButton("Register User", UITheme.SUCCESS_GREEN, Color.WHITE);
        JButton btnUpdate = UITheme.createButton("Update User", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnDelete = UITheme.createButton("Delete User", UITheme.DANGER_RED, Color.WHITE);
        JButton btnClear = UITheme.createButton("Clear Form", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> handleAddUser());
        btnUpdate.addActionListener(e -> handleUpdateUser());
        btnDelete.addActionListener(e -> handleDeleteUser());
        btnClear.addActionListener(e -> clearUserForm());

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        formCard.add(formGrid, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formCard);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Search Bar
        JTextField txtSearch = UITheme.createTextField(20);
        JButton btnSearch = UITheme.createButton("Search Courses", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnReset = UITheme.createButton("Reset", UITheme.NEUTRAL_GRAY, Color.WHITE);
        JButton btnExport = UITheme.createButton("Export CSV", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnSearch.addActionListener(e -> searchCoursesData(txtSearch.getText().trim()));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadCoursesData();
        });
        btnExport.addActionListener(e -> handleExportCSV(coursesTable, "Courses_Report.csv", "Curriculum Courses Directory"));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(txtSearch);
        topBar.add(btnSearch);
        topBar.add(btnReset);
        topBar.add(btnExport);
        panel.add(topBar, BorderLayout.NORTH);

        // Courses Table
        String[] cols = {"Course Code", "Course Title", "Theory Credits", "Practical Credits", "Total Credits", "Department", "Assigned Lecturer"};
        coursesTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        coursesTable = new JTable(coursesTableModel);
        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && coursesTable.getSelectedRow() != -1) {
                populateCourseForm(coursesTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = createTableScrollPane(coursesTable);

        // Course Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 10));

        JPanel formGrid = new JPanel(new GridLayout(2, 3, 12, 10));
        formGrid.setOpaque(false);

        txtCourseCode = UITheme.createTextField(10);
        txtCourseTitle = UITheme.createTextField(15);
        txtTheoryCredits = UITheme.createTextField(5);
        txtPracticalCredits = UITheme.createTextField(5);
        txtLecturerId = UITheme.createTextField(5);
        cmbCourseDepartment = new JComboBox<>();

        formGrid.add(createFormRow("Course Code (e.g. ICT1122)", txtCourseCode));
        formGrid.add(createFormRow("Course Title", txtCourseTitle));
        formGrid.add(createFormRow("Theory Credits", txtTheoryCredits));
        formGrid.add(createFormRow("Practical Credits", txtPracticalCredits));
        formGrid.add(createFormRow("Department", cmbCourseDepartment));
        formGrid.add(createFormRow("Lecturer ID (Optional)", txtLecturerId));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnAdd = UITheme.createButton("Add Course", UITheme.SUCCESS_GREEN, Color.WHITE);
        JButton btnUpdate = UITheme.createButton("Update Course", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnDelete = UITheme.createButton("Delete Course", UITheme.DANGER_RED, Color.WHITE);
        JButton btnClear = UITheme.createButton("Clear", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> handleAddCourse());
        btnUpdate.addActionListener(e -> handleUpdateCourse());
        btnDelete.addActionListener(e -> handleDeleteCourse());
        btnClear.addActionListener(e -> clearCourseForm());

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        formCard.add(formGrid, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Filter Bar
        JComboBox<String> cmbFilter = UITheme.createComboBox(new String[]{"ALL", "UNDERGRADUATE", "LECTURER", "TECH_OFFICER"});
        JButton btnFilter = UITheme.createButton("Filter Notices", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnExport = UITheme.createButton("Export CSV", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnFilter.addActionListener(e -> loadNoticesData((String) cmbFilter.getSelectedItem()));
        btnExport.addActionListener(e -> handleExportCSV(noticesTable, "Notices_Report.csv", "Published Notices"));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(new JLabel("Target Audience:"));
        topBar.add(cmbFilter);
        topBar.add(btnFilter);
        topBar.add(btnExport);
        panel.add(topBar, BorderLayout.NORTH);

        // Notices Table
        String[] cols = {"Notice ID", "Title", "Target Audience", "Posted By", "Posted Date", "Content"};
        noticesTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        noticesTable = new JTable(noticesTableModel);
        noticesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && noticesTable.getSelectedRow() != -1) {
                populateNoticeForm(noticesTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = createTableScrollPane(noticesTable);

        // Notice Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 10));

        JPanel formTop = new JPanel(new GridLayout(1, 2, 12, 0));
        formTop.setOpaque(false);

        txtNoticeTitle = UITheme.createTextField(20);
        cmbNoticeAudience = UITheme.createComboBox(new String[]{"ALL", "UNDERGRADUATE", "LECTURER", "TECH_OFFICER"});

        formTop.add(createFormRow("Notice Title", txtNoticeTitle));
        formTop.add(createFormRow("Target Audience", cmbNoticeAudience));

        txtNoticeContent = new JTextArea(4, 30);
        txtNoticeContent.setFont(UITheme.FONT_FORM_INPUT);
        txtNoticeContent.setLineWrap(true);
        txtNoticeContent.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(txtNoticeContent);

        JPanel contentWrap = createFormRow("Announcement Content", contentScroll);

        JPanel formCenter = new JPanel(new BorderLayout(0, 8));
        formCenter.setOpaque(false);
        formCenter.add(formTop, BorderLayout.NORTH);
        formCenter.add(contentWrap, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnPublish = UITheme.createButton("Publish Notice", UITheme.SUCCESS_GREEN, Color.WHITE);
        JButton btnUpdate = UITheme.createButton("Update Notice", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnDelete = UITheme.createButton("Delete Notice", UITheme.DANGER_RED, Color.WHITE);
        JButton btnClear = UITheme.createButton("Clear", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnPublish.addActionListener(e -> handlePublishNotice());
        btnUpdate.addActionListener(e -> handleUpdateNotice());
        btnDelete.addActionListener(e -> handleDeleteNotice());
        btnClear.addActionListener(e -> clearNoticeForm());

        btnRow.add(btnPublish);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        formCard.add(formCenter, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formCard);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Filter Bar
        JComboBox<DepartmentItem> cmbFilterDept = new JComboBox<>();
        JButton btnFilter = UITheme.createButton("Filter Department", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnExport = UITheme.createButton("Export CSV", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnFilter.addActionListener(e -> {
            DepartmentItem item = (DepartmentItem) cmbFilterDept.getSelectedItem();
            loadTimetableData(item != null ? item.id : -1);
        });
        btnExport.addActionListener(e -> handleExportCSV(timetablesTable, "Timetable_Report.csv", "Academic Schedule"));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(new JLabel("Department:"));
        topBar.add(cmbFilterDept);
        topBar.add(btnFilter);
        topBar.add(btnExport);
        panel.add(topBar, BorderLayout.NORTH);

        // Timetable Table
        String[] cols = {"Slot ID", "Course Code", "Course Title", "Department", "Session Type", "Day", "Start Time", "End Time", "Venue"};
        timetablesTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        timetablesTable = new JTable(timetablesTableModel);
        timetablesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && timetablesTable.getSelectedRow() != -1) {
                populateTimetableForm(timetablesTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = createTableScrollPane(timetablesTable);

        // Timetable Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 10));

        JPanel formGrid = new JPanel(new GridLayout(2, 4, 12, 10));
        formGrid.setOpaque(false);

        txtTtCourseCode = UITheme.createTextField(10);
        cmbTtDepartment = new JComboBox<>();
        cmbTtSessionType = UITheme.createComboBox(new String[]{"THEORY", "PRACTICAL", "EXAM"});
        cmbTtDayOfWeek = UITheme.createComboBox(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"});
        txtTtStartTime = UITheme.createTextField(8);
        txtTtEndTime = UITheme.createTextField(8);
        txtTtVenue = UITheme.createTextField(15);

        formGrid.add(createFormRow("Course Code", txtTtCourseCode));
        formGrid.add(createFormRow("Department", cmbTtDepartment));
        formGrid.add(createFormRow("Session Type", cmbTtSessionType));
        formGrid.add(createFormRow("Day of Week", cmbTtDayOfWeek));
        formGrid.add(createFormRow("Start Time (HH:MM:SS)", txtTtStartTime));
        formGrid.add(createFormRow("End Time (HH:MM:SS)", txtTtEndTime));
        formGrid.add(createFormRow("Venue / Hall", txtTtVenue));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnAdd = UITheme.createButton("Add Schedule", UITheme.SUCCESS_GREEN, Color.WHITE);
        JButton btnUpdate = UITheme.createButton("Update Schedule", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnDelete = UITheme.createButton("Delete Schedule", UITheme.DANGER_RED, Color.WHITE);
        JButton btnClear = UITheme.createButton("Clear", UITheme.NEUTRAL_GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> handleAddTimetable());
        btnUpdate.addActionListener(e -> handleUpdateTimetable());
        btnDelete.addActionListener(e -> handleDeleteTimetable());
        btnClear.addActionListener(e -> clearTimetableForm());

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        formCard.add(formGrid, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        // Load departments into filter combo
        SwingUtilities.invokeLater(() -> {
            try {
                List<Department> depts = userDAO.getAllDepartments();
                cmbFilterDept.addItem(new DepartmentItem(-1, "ALL Departments"));
                for (Department d : depts) {
                    cmbFilterDept.addItem(new DepartmentItem(d.getDepartmentId(), d.getDepartmentName()));
                }
            } catch (Exception ignored) {}
        });

        return panel;
    }

    // =========================================================================
    // DATA LOADING & REFRESH LOGIC
    // =========================================================================

    public void refreshAllData() {
        loadMetrics();
        loadDepartments();
        loadUsersData("ALL");
        loadCoursesData();
        loadNoticesData("ALL");
        loadTimetableData(-1);
    }

    private void loadMetrics() {
        Map<String, Integer> metrics = adminDAO.getDashboardMetrics();
        lblTotalUsersVal.setText(String.valueOf(metrics.getOrDefault("totalUsers", 0)));
        lblStudentsVal.setText(String.valueOf(metrics.getOrDefault("students", 0)));
        lblLecturersVal.setText(String.valueOf(metrics.getOrDefault("lecturers", 0)));
        lblTechOfficersVal.setText(String.valueOf(metrics.getOrDefault("techOfficers", 0)));
        lblCoursesVal.setText(String.valueOf(metrics.getOrDefault("courses", 0)));
        lblNoticesVal.setText(String.valueOf(metrics.getOrDefault("notices", 0)));
    }

    private void loadDepartments() {
        try {
            List<Department> depts = userDAO.getAllDepartments();
            cmbUserDepartment.removeAllItems();
            cmbCourseDepartment.removeAllItems();
            cmbTtDepartment.removeAllItems();

            for (Department d : depts) {
                DepartmentItem item = new DepartmentItem(d.getDepartmentId(), d.getDepartmentName());
                cmbUserDepartment.addItem(item);
                cmbCourseDepartment.addItem(item);
                cmbTtDepartment.addItem(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadUsersData(String roleFilter) {
        usersTableModel.setRowCount(0);
        try {
            List<Map<String, Object>> users = userDAO.getAllUsers(roleFilter);
            for (Map<String, Object> u : users) {
                usersTableModel.addRow(new Object[]{
                        u.get("userId"),
                        u.get("username"),
                        u.get("fullName"),
                        u.get("email"),
                        u.get("phoneNo"),
                        u.get("role"),
                        Boolean.TRUE.equals(u.get("isActive")) ? "ACTIVE" : "LOCKED",
                        u.get("failedLogins"),
                        u.get("extraId"),
                        u.get("departmentName")
                });
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Loading Users", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCoursesData() {
        coursesTableModel.setRowCount(0);
        try {
            List<Course> list = courseDAO.getAllCourses();
            for (Course c : list) {
                coursesTableModel.addRow(new Object[]{
                        c.getCourseCode(),
                        c.getCourseTitle(),
                        c.getCreditTheory(),
                        c.getCreditPractical(),
                        c.getTotalCredits(),
                        c.getDepartmentName(),
                        c.getLecturerName()
                });
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Loading Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCoursesData(String keyword) {
        coursesTableModel.setRowCount(0);
        try {
            List<Course> list = courseDAO.searchCourses(keyword);
            for (Course c : list) {
                coursesTableModel.addRow(new Object[]{
                        c.getCourseCode(),
                        c.getCourseTitle(),
                        c.getCreditTheory(),
                        c.getCreditPractical(),
                        c.getTotalCredits(),
                        c.getDepartmentName(),
                        c.getLecturerName()
                });
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNoticesData(String audience) {
        noticesTableModel.setRowCount(0);
        try {
            List<Notice> list = noticeDAO.getAllAdminNotices(audience);
            updateNoticeTickerCache(list);
            for (Notice n : list) {
                noticesTableModel.addRow(new Object[]{
                        n.getNoticeId(),
                        n.getTitle(),
                        n.getTargetAudience(),
                        n.getPostedByName(),
                        n.getPostedDate() != null ? n.getPostedDate().toString() : "N/A",
                        n.getContent()
                });
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Loading Notices", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTimetableData(int deptId) {
        timetablesTableModel.setRowCount(0);
        try {
            List<Timetable> list = timetableDAO.getAllTimetables(deptId);
            for (Timetable t : list) {
                timetablesTableModel.addRow(new Object[]{
                        t.getTimetableId(),
                        t.getCourseCode(),
                        t.getCourseTitle(),
                        t.getDepartmentName(),
                        t.getSessionType(),
                        t.getDayOfWeek(),
                        t.getStartTime(),
                        t.getEndTime(),
                        t.getVenue()
                });
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Loading Timetables", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // USER FORM ACTIONS
    // =========================================================================

    private void populateUserForm(int row) {
        selectedUserId = (int) usersTableModel.getValueAt(row, 0);
        txtUserUsername.setText((String) usersTableModel.getValueAt(row, 1));
        txtUserFullName.setText((String) usersTableModel.getValueAt(row, 2));
        txtUserEmail.setText((String) usersTableModel.getValueAt(row, 3));
        txtUserPhone.setText((String) usersTableModel.getValueAt(row, 4));
        cmbUserRole.setSelectedItem(usersTableModel.getValueAt(row, 5));
        chkUserActive.setSelected("ACTIVE".equals(usersTableModel.getValueAt(row, 6)));
        txtUserExtraId.setText((String) usersTableModel.getValueAt(row, 8));
    }

    private void handleAddUser() {
        String username = txtUserUsername.getText().trim();
        String password = new String(txtUserPassword.getPassword()).trim();
        String fullName = txtUserFullName.getText().trim();
        String email = txtUserEmail.getText().trim();
        String phone = txtUserPhone.getText().trim();
        String role = (String) cmbUserRole.getSelectedItem();
        String extraId = txtUserExtraId.getText().trim();
        String batch = txtUserBatch.getText().trim();
        DepartmentItem dept = (DepartmentItem) cmbUserDepartment.getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Full Name are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = userDAO.createUser(username, password, fullName, email, phone, role, extraId, dept != null ? dept.id : 1, batch);
            if (success) {
                JOptionPane.showMessageDialog(this, "User [" + username + "] registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearUserForm();
                loadUsersData("ALL");
                loadMetrics();
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fullName = txtUserFullName.getText().trim();
        String email = txtUserEmail.getText().trim();
        String phone = txtUserPhone.getText().trim();
        String role = (String) cmbUserRole.getSelectedItem();
        String extraId = txtUserExtraId.getText().trim();
        String batch = txtUserBatch.getText().trim();
        boolean isActive = chkUserActive.isSelected();
        DepartmentItem dept = (DepartmentItem) cmbUserDepartment.getSelectedItem();

        try {
            boolean success = userDAO.updateUser(selectedUserId, fullName, email, phone, role, isActive, extraId, dept != null ? dept.id : 1, batch);
            if (success) {
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearUserForm();
                loadUsersData("ALL");
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(selectedUserId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearUserForm();
                    loadUsersData("ALL");
                    loadMetrics();
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUnlockUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to unlock.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) usersTableModel.getValueAt(row, 0);
        try {
            boolean success = userDAO.unlockAccount(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account unlocked successfully! Failed attempts reset to 0.", "Account Unlocked", JOptionPane.INFORMATION_MESSAGE);
                loadUsersData("ALL");
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearUserForm() {
        selectedUserId = -1;
        txtUserUsername.setText("");
        txtUserPassword.setText("");
        txtUserFullName.setText("");
        txtUserEmail.setText("");
        txtUserPhone.setText("");
        txtUserExtraId.setText("");
        txtUserBatch.setText("");
        chkUserActive.setSelected(true);
        usersTable.clearSelection();
    }

    // =========================================================================
    // COURSE FORM ACTIONS
    // =========================================================================

    private void populateCourseForm(int row) {
        txtCourseCode.setText((String) coursesTableModel.getValueAt(row, 0));
        txtCourseTitle.setText((String) coursesTableModel.getValueAt(row, 1));
        txtTheoryCredits.setText(String.valueOf(coursesTableModel.getValueAt(row, 2)));
        txtPracticalCredits.setText(String.valueOf(coursesTableModel.getValueAt(row, 3)));
    }

    private void handleAddCourse() {
        String code = txtCourseCode.getText().trim();
        String title = txtCourseTitle.getText().trim();
        DepartmentItem dept = (DepartmentItem) cmbCourseDepartment.getSelectedItem();

        if (code.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course code and title are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tc = Integer.parseInt(txtTheoryCredits.getText().trim());
            int pc = Integer.parseInt(txtPracticalCredits.getText().trim());
            Integer lecId = null;
            if (!txtLecturerId.getText().trim().isEmpty()) {
                lecId = Integer.parseInt(txtLecturerId.getText().trim());
            }

            boolean success = courseDAO.addCourse(code, title, tc, pc, dept != null ? dept.id : 1, lecId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCourseForm();
                loadCoursesData();
                loadMetrics();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Credits and Lecturer ID must be valid integers.", "Number Format Error", JOptionPane.WARNING_MESSAGE);
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateCourse() {
        String code = txtCourseCode.getText().trim();
        String title = txtCourseTitle.getText().trim();
        DepartmentItem dept = (DepartmentItem) cmbCourseDepartment.getSelectedItem();

        if (code.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a course to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tc = Integer.parseInt(txtTheoryCredits.getText().trim());
            int pc = Integer.parseInt(txtPracticalCredits.getText().trim());
            Integer lecId = null;
            if (!txtLecturerId.getText().trim().isEmpty()) {
                lecId = Integer.parseInt(txtLecturerId.getText().trim());
            }

            boolean success = courseDAO.updateCourse(code, title, tc, pc, dept != null ? dept.id : 1, lecId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCourseForm();
                loadCoursesData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteCourse() {
        String code = txtCourseCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete course " + code + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = courseDAO.deleteCourse(code);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearCourseForm();
                    loadCoursesData();
                    loadMetrics();
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearCourseForm() {
        txtCourseCode.setText("");
        txtCourseTitle.setText("");
        txtTheoryCredits.setText("");
        txtPracticalCredits.setText("");
        txtLecturerId.setText("");
        coursesTable.clearSelection();
    }

    // =========================================================================
    // NOTICE FORM ACTIONS
    // =========================================================================

    private void populateNoticeForm(int row) {
        selectedNoticeId = (int) noticesTableModel.getValueAt(row, 0);
        txtNoticeTitle.setText((String) noticesTableModel.getValueAt(row, 1));
        cmbNoticeAudience.setSelectedItem(noticesTableModel.getValueAt(row, 2));
        txtNoticeContent.setText((String) noticesTableModel.getValueAt(row, 5));
    }

    private void handlePublishNotice() {
        String title = txtNoticeTitle.getText().trim();
        String content = txtNoticeContent.getText().trim();
        String audience = (String) cmbNoticeAudience.getSelectedItem();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Notice title and content cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = noticeDAO.publishNotice(title, content, audience, currentAdminUser.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearNoticeForm();
                loadNoticesData("ALL");
                loadMetrics();
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateNotice() {
        if (selectedNoticeId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = txtNoticeTitle.getText().trim();
        String content = txtNoticeContent.getText().trim();
        String audience = (String) cmbNoticeAudience.getSelectedItem();

        try {
            boolean success = noticeDAO.updateNotice(selectedNoticeId, title, content, audience);
            if (success) {
                JOptionPane.showMessageDialog(this, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearNoticeForm();
                loadNoticesData("ALL");
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteNotice() {
        if (selectedNoticeId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this notice?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = noticeDAO.deleteNoticeById(selectedNoticeId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Notice deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearNoticeForm();
                    loadNoticesData("ALL");
                    loadMetrics();
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearNoticeForm() {
        selectedNoticeId = -1;
        txtNoticeTitle.setText("");
        txtNoticeContent.setText("");
        noticesTable.clearSelection();
    }

    // =========================================================================
    // TIMETABLE FORM ACTIONS
    // =========================================================================

    private void populateTimetableForm(int row) {
        selectedTimetableId = (int) timetablesTableModel.getValueAt(row, 0);
        txtTtCourseCode.setText((String) timetablesTableModel.getValueAt(row, 1));
        cmbTtSessionType.setSelectedItem(timetablesTableModel.getValueAt(row, 4));
        cmbTtDayOfWeek.setSelectedItem(timetablesTableModel.getValueAt(row, 5));
        txtTtStartTime.setText(String.valueOf(timetablesTableModel.getValueAt(row, 6)));
        txtTtEndTime.setText(String.valueOf(timetablesTableModel.getValueAt(row, 7)));
        txtTtVenue.setText((String) timetablesTableModel.getValueAt(row, 8));
    }

    private void handleAddTimetable() {
        String code = txtTtCourseCode.getText().trim();
        DepartmentItem dept = (DepartmentItem) cmbTtDepartment.getSelectedItem();
        String sessionType = (String) cmbTtSessionType.getSelectedItem();
        String day = (String) cmbTtDayOfWeek.getSelectedItem();
        String venue = txtTtVenue.getText().trim();

        if (code.isEmpty() || venue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course code and venue are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Time st = Time.valueOf(txtTtStartTime.getText().trim());
            Time et = Time.valueOf(txtTtEndTime.getText().trim());

            boolean success = timetableDAO.addTimetableEntry(code, dept != null ? dept.id : 1, sessionType, day, st, et, venue);
            if (success) {
                JOptionPane.showMessageDialog(this, "Timetable schedule slot added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearTimetableForm();
                loadTimetableData(-1);
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, "Time must be in format HH:MM:SS (e.g. 08:30:00).", "Time Format Error", JOptionPane.WARNING_MESSAGE);
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateTimetable() {
        if (selectedTimetableId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = txtTtCourseCode.getText().trim();
        DepartmentItem dept = (DepartmentItem) cmbTtDepartment.getSelectedItem();
        String sessionType = (String) cmbTtSessionType.getSelectedItem();
        String day = (String) cmbTtDayOfWeek.getSelectedItem();
        String venue = txtTtVenue.getText().trim();

        try {
            Time st = Time.valueOf(txtTtStartTime.getText().trim());
            Time et = Time.valueOf(txtTtEndTime.getText().trim());

            boolean success = timetableDAO.updateTimetableEntry(selectedTimetableId, code, dept != null ? dept.id : 1, sessionType, day, st, et, venue);
            if (success) {
                JOptionPane.showMessageDialog(this, "Timetable schedule slot updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearTimetableForm();
                loadTimetableData(-1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteTimetable() {
        if (selectedTimetableId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this schedule slot?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = timetableDAO.deleteTimetableEntry(selectedTimetableId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Timetable schedule slot deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearTimetableForm();
                    loadTimetableData(-1);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearTimetableForm() {
        selectedTimetableId = -1;
        txtTtCourseCode.setText("");
        txtTtStartTime.setText("");
        txtTtEndTime.setText("");
        txtTtVenue.setText("");
        timetablesTable.clearSelection();
    }

    // =========================================================================
    // REPORT EXPORT (Character Streams)
    // =========================================================================

    private void handleExportCSV(JTable table, String defaultFileName, String reportTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultFileName));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            boolean success = AdminReportUtil.exportTableToCSV(table.getModel(), reportTitle, fileToSave);
            if (success) {
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + fileToSave.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export report. Please check file permissions.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // HELPER COMBOBOX ITEM CLASS
    // =========================================================================

    private static class DepartmentItem {
        final int id;
        final String name;

        public DepartmentItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
