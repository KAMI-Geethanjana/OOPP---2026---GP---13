package gui;

import dao.AdminCourseDAO;
import dao.AdminNoticeDAO;
import dao.AdminTimetableDAO;
import dao.AdminUserDAO;
import exception.AdminOperationException;
import model.Admin;
import model.Course;
import model.Department;
import model.Notice;
import model.Timetable;
import model.User;
import util.AdminReportUtil;

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
 * Implements FR-ADM-01 through FR-ADM-06 and FR-AUTH-05.
 * - Multithreading (Lesson 08: extends Thread and implements Runnable)
 * - Polymorphism, Casting, and instanceof
 * - File Handling with Character Streams (Report Export)
 * - Checked Exception Handling (AdminOperationException handling)
 */
public class AdminDashboardUI extends JFrame {

    // DAOs
    private final AdminUserDAO userDAO = new AdminUserDAO();
    private final AdminCourseDAO courseDAO = new AdminCourseDAO();
    private final AdminNoticeDAO noticeDAO = new AdminNoticeDAO();
    private final AdminTimetableDAO timetableDAO = new AdminTimetableDAO();

    // User session demonstrating Polymorphism & Upcasting
    private final User currentAdminUser;

    // Multithreading (ICT2122 Lesson 08)
    private JLabel lblLiveClock;
    private JLabel lblNoticeTicker;
    private volatile boolean isClockRunning = true;
    private volatile boolean isTickerRunning = true;
    private Thread clockThread;
    private NoticeTickerThread noticeTickerThread;
    private final Object noticeLock = new Object();
    private List<String> latestNoticeTitles = new ArrayList<>();

    // UI Panels
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Sidebar buttons for active highlighting
    private JButton btnNavDashboard;
    private JButton btnNavUsers;
    private JButton btnNavCourses;
    private JButton btnNavNotices;
    private JButton btnNavTimetable;

    // Tables
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;
    private JTable noticesTable;
    private DefaultTableModel noticesTableModel;
    private JTable timetablesTable;
    private DefaultTableModel timetablesTableModel;

    // Metric Labels
    private JLabel lblTotalUsersVal;
    private JLabel lblStudentsVal;
    private JLabel lblLecturersVal;
    private JLabel lblTechOfficersVal;
    private JLabel lblCoursesVal;
    private JLabel lblNoticesVal;

    public AdminDashboardUI() {
        // Upcasting: Subclass object assigned to base class variable
        this.currentAdminUser = new Admin(1, "TG2086", "", "K.A.M.I. Geethanjana", "ishanminula@gmail.com", "0771234567", 2086, "FACULTY_ADMIN");

        setTitle("Faculty of Technology Management System (FTMS) - Administrator Portal");
        setSize(1220, 760);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        startBackgroundThreads(); // Spawns both background threads (Lesson 08)
        refreshAllData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Deep slate
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("Faculty of Technology Management System (FTMS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        // Demonstrating instanceof operator and Downcasting
        String roleTitle;
        if (currentAdminUser instanceof Admin) {
            Admin adminObj = (Admin) currentAdminUser; // Downcasting
            roleTitle = adminObj.getRoleDisplayName(); // Dynamic Polymorphism
        } else {
            roleTitle = currentAdminUser.getRole();
        }

        JLabel lblSub = new JLabel("Logged in as: " + currentAdminUser.getFullName() + " | " + roleTitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        // Live Clock
        lblLiveClock = new JLabel("🕒 Initializing clock...", SwingConstants.RIGHT);
        lblLiveClock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLiveClock.setForeground(new Color(56, 189, 248)); // Sky cyan

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(225, 29, 72)); // Rose red
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stopBackgroundThreads();
                dispose();
                new LoginUI().setVisible(true);
            }
        });

        JPanel rightHeaderBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderBlock.setOpaque(false);
        rightHeaderBlock.add(lblLiveClock);
        rightHeaderBlock.add(btnLogout);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(rightHeaderBlock, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Stop background threads gracefully on window close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopBackgroundThreads();
            }
        });

        // 2. Left Navigation Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(30, 41, 59));
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblNavHeading = new JLabel("ADMIN NAVIGATION");
        lblNavHeading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNavHeading.setForeground(new Color(148, 163, 184));
        lblNavHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(lblNavHeading);
        sidebarPanel.add(Box.createVerticalStrut(15));

        btnNavDashboard = createNavButton("📊  Dashboard Overview");
        btnNavUsers = createNavButton("👥  User Management");
        btnNavCourses = createNavButton("📚  Course Management");
        btnNavNotices = createNavButton("📢  Notice Management");
        btnNavTimetable = createNavButton("📅  Timetable Management");

        sidebarPanel.add(btnNavDashboard);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnNavUsers);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnNavCourses);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnNavNotices);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnNavTimetable);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. Central Content Cards
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(248, 250, 252));

        mainContentPanel.add(buildDashboardOverviewPanel(), "CARD_DASHBOARD");
        mainContentPanel.add(buildUsersPanel(), "CARD_USERS");
        mainContentPanel.add(buildCoursesPanel(), "CARD_COURSES");
        mainContentPanel.add(buildNoticesPanel(), "CARD_NOTICES");
        mainContentPanel.add(buildTimetablePanel(), "CARD_TIMETABLE");

        add(mainContentPanel, BorderLayout.CENTER);

        // 4. Bottom Footer Status Bar (Live Notice Ticker & Multithreading Status)
        JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
        footerPanel.setBackground(new Color(15, 23, 42)); // Match deep slate header
        footerPanel.setBorder(new EmptyBorder(8, 20, 8, 20));

        lblNoticeTicker = new JLabel("📢 Faculty Announcements: Initializing notice ticker...");
        lblNoticeTicker.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNoticeTicker.setForeground(new Color(226, 232, 240));

        JLabel lblThreadStatus = new JLabel("⚡ Threads Active: Live Clock (Runnable) | Notice Ticker (extends Thread)");
        lblThreadStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblThreadStatus.setForeground(new Color(52, 211, 153)); // Emerald green

        footerPanel.add(lblNoticeTicker, BorderLayout.WEST);
        footerPanel.add(lblThreadStatus, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        // Navigation Actions
        btnNavDashboard.addActionListener(e -> switchCard("CARD_DASHBOARD", btnNavDashboard));
        btnNavUsers.addActionListener(e -> switchCard("CARD_USERS", btnNavUsers));
        btnNavCourses.addActionListener(e -> switchCard("CARD_COURSES", btnNavCourses));
        btnNavNotices.addActionListener(e -> switchCard("CARD_NOTICES", btnNavNotices));
        btnNavTimetable.addActionListener(e -> switchCard("CARD_TIMETABLE", btnNavTimetable));

        setActiveNavButton(btnNavDashboard);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setMaximumSize(new Dimension(210, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void switchCard(String cardName, JButton activeBtn) {
        cardLayout.show(mainContentPanel, cardName);
        setActiveNavButton(activeBtn);
        refreshAllData();
    }

    private void setActiveNavButton(JButton activeBtn) {
        JButton[] buttons = {btnNavDashboard, btnNavUsers, btnNavCourses, btnNavNotices, btnNavTimetable};
        for (JButton b : buttons) {
            if (b == activeBtn) {
                b.setBackground(new Color(37, 99, 235)); // Vibrant Blue
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(51, 65, 85));
                b.setForeground(new Color(226, 232, 240));
            }
        }
    }

    // =========================================================================
    // MULTITHREADING IMPLEMENTATION (ICT2122 Lesson 08)
    // =========================================================================

    /**
     * Lesson 08 - Method 1: Creating a Thread by Extending java.lang.Thread class (Slide 24-27).
     *
     * A dedicated thread class overriding the run() method.
     * Periodically cycles through recent faculty notices in the footer banner,
     * maintaining high GUI responsiveness without freezing the EDT (Slide 7).
     */
    private class NoticeTickerThread extends Thread {
        private final String[] defaultAnnouncements = {
            "Faculty of Technology Management System (FTMS) operational.",
            "ICT2122 Object Oriented Programming: Mini Project Evaluations scheduled.",
            "Mid-Semester Examination Timetables and Room Allocations published.",
            "Technical Officers: Department equipment calibrations finalized.",
            "Faculty Notice: Semester timetables and lecture schedules available."
        };
        private int noticeIndex = 0;

        public NoticeTickerThread() {
            super("Admin-NoticeTicker-Thread"); // Thread(String name) constructor - Lesson 08 Slide 34
            setDaemon(true); // Daemon thread terminates when main application closes
        }

        @Override
        public void run() {
            // Lesson 08 Slide 24: Overrides the run() method to define thread business logic
            while (isTickerRunning) {
                String announcementText;
                synchronized (noticeLock) {
                    if (latestNoticeTitles != null && !latestNoticeTitles.isEmpty()) {
                        announcementText = latestNoticeTitles.get(noticeIndex % latestNoticeTitles.size());
                        noticeIndex = (noticeIndex + 1) % latestNoticeTitles.size();
                    } else {
                        announcementText = defaultAnnouncements[noticeIndex % defaultAnnouncements.length];
                        noticeIndex = (noticeIndex + 1) % defaultAnnouncements.length;
                    }
                }

                final String fullMessage = "📢 Notice: " + announcementText;
                SwingUtilities.invokeLater(() -> {
                    if (lblNoticeTicker != null) {
                        lblNoticeTicker.setText(fullMessage);
                    }
                });

                try {
                    // Lesson 08 Slide 21: Causes the currently executing thread to sleep (temporarily cease execution)
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    break; // Graceful thread termination on interrupt
                }
            }
        }
    }

    /**
     * Lesson 08 - Method 2: Creating a Thread by Implementing java.lang.Runnable interface (Slide 28-31).
     *
     * Since AdminDashboardUI extends JFrame, it cannot extend Thread class due to Java's single inheritance (Slide 32).
     * Implementing Runnable provides better OOP design and consistency while avoiding single inheritance limitations.
     */
    private class LiveClockRunnable implements Runnable {
        @Override
        public void run() {
            // Lesson 08 Slide 28: Overrides the run() method provided by Runnable interface
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            while (isClockRunning) {
                final String timeText = "🕒 " + sdf.format(new Date());
                SwingUtilities.invokeLater(() -> {
                    if (lblLiveClock != null) {
                        lblLiveClock.setText(timeText);
                    }
                });
                try {
                    // Lesson 08 Slide 21: Sleep for 1000 milliseconds (1 second)
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break; // Graceful thread termination on interrupt
                }
            }
        }
    }

    /**
     * Starts both background threads using Thread.start() (Lesson 08 Slide 35).
     * Note: Calling start() creates a new thread on which the task is run,
     * whereas calling run() would execute synchronously on the caller thread.
     */
    private void startBackgroundThreads() {
        // Method 2 (Slide 29): Thread(Runnable target, String name) constructor
        clockThread = new Thread(new LiveClockRunnable(), "Admin-Clock-Thread");
        clockThread.setDaemon(true);
        clockThread.start();

        // Method 1 (Slide 25): Spawning thread extending Thread class
        noticeTickerThread = new NoticeTickerThread();
        noticeTickerThread.start();
    }

    /**
     * Gracefully stops and interrupts all active background threads.
     */
    private void stopBackgroundThreads() {
        isClockRunning = false;
        isTickerRunning = false;
        if (clockThread != null && clockThread.isAlive()) {
            clockThread.interrupt();
        }
        if (noticeTickerThread != null && noticeTickerThread.isAlive()) {
            noticeTickerThread.interrupt();
        }
    }

    // =========================================================================
    // 1. DASHBOARD OVERVIEW PANEL (FR-ADM-05)
    // =========================================================================
    private JPanel buildDashboardOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Welcome Header
        JPanel topBlock = new JPanel(new GridLayout(2, 1, 0, 5));
        topBlock.setOpaque(false);
        JLabel lblWelcome = new JLabel("Administrator Control Overview");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel("Consolidated system summary statistics across all faculty entities (FR-ADM-05).");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));
        topBlock.add(lblWelcome);
        topBlock.add(lblDesc);

        panel.add(topBlock, BorderLayout.NORTH);

        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        statsGrid.setOpaque(false);

        lblTotalUsersVal = new JLabel("0", SwingConstants.CENTER);
        lblStudentsVal = new JLabel("0", SwingConstants.CENTER);
        lblLecturersVal = new JLabel("0", SwingConstants.CENTER);
        lblTechOfficersVal = new JLabel("0", SwingConstants.CENTER);
        lblCoursesVal = new JLabel("0", SwingConstants.CENTER);
        lblNoticesVal = new JLabel("0", SwingConstants.CENTER);

        statsGrid.add(createMetricCard("Total System Users", lblTotalUsersVal, new Color(59, 130, 246)));
        statsGrid.add(createMetricCard("Registered Undergraduates", lblStudentsVal, new Color(16, 185, 129)));
        statsGrid.add(createMetricCard("Faculty Lecturers", lblLecturersVal, new Color(139, 92, 246)));
        statsGrid.add(createMetricCard("Technical Officers", lblTechOfficersVal, new Color(245, 158, 11)));
        statsGrid.add(createMetricCard("Course Modules", lblCoursesVal, new Color(14, 165, 233)));
        statsGrid.add(createMetricCard("Published Notices", lblNoticesVal, new Color(236, 72, 153)));

        panel.add(statsGrid, BorderLayout.CENTER);

        // Quick Shortcuts bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setOpaque(false);
        JButton btnRefreshStats = new JButton("🔄 Refresh Statistics");
        btnRefreshStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefreshStats.setBackground(Color.WHITE);
        btnRefreshStats.setFocusPainted(false);
        btnRefreshStats.addActionListener(e -> refreshDashboardStats());
        bottomBar.add(btnRefreshStats);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(5, 0));
        accentBar.setBackground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(accentBar, BorderLayout.WEST);
        return card;
    }

    // =========================================================================
    // 2. USER MANAGEMENT PANEL (FR-ADM-01 & FR-AUTH-05)
    // =========================================================================
    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top Header and Actions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("User Profile Management (FR-ADM-01)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAddUser = createPrimaryButton("➕ Add User");
        JButton btnEditUser = createSecondaryButton("✏️ Edit User");
        JButton btnDeleteUser = createDangerButton("🗑️ Delete User");
        JButton btnUnlock = createWarningButton("🔓 Unlock Account (FR-AUTH-05)");
        JButton btnExport = createSecondaryButton("📥 Export Audit Report");

        actionButtons.add(btnAddUser);
        actionButtons.add(btnEditUser);
        actionButtons.add(btnDeleteUser);
        actionButtons.add(btnUnlock);
        actionButtons.add(btnExport);

        topPanel.add(lblHeader, BorderLayout.WEST);
        topPanel.add(actionButtons, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Users Table
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Phone", "Role", "Identifier", "Department", "Status", "Failed Logins"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        styleTable(usersTable);

        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // Button Listeners
        btnAddUser.addActionListener(e -> showAddUserDialog());
        btnEditUser.addActionListener(e -> showEditUserDialog());
        btnDeleteUser.addActionListener(e -> deleteSelectedUser());
        btnUnlock.addActionListener(e -> unlockSelectedUser());
        btnExport.addActionListener(e -> exportUserAuditReport());

        return panel;
    }

    /**
     * Exports all user records into a formatted audit report file using Character Streams.
     * (FileWriter -> BufferedWriter -> PrintWriter, flush(), close()).
     */
    private void exportUserAuditReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save User Audit Report");
        chooser.setSelectedFile(new File("FTMS_User_Audit_Report.txt"));
        int selection = chooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File targetFile = chooser.getSelectedFile();
            try {
                List<Map<String, Object>> users = userDAO.getAllUsers();
                AdminReportUtil.exportUserReport(users, targetFile);
                JOptionPane.showMessageDialog(this,
                        "User audit report successfully generated and saved to:\n" + targetFile.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New System User", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 25, 20, 25));

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField("1234");
        JTextField txtFullName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();

        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"UNDERGRADUATE", "LECTURER", "TECH_OFFICER", "ADMIN"});
        JTextField txtExtraId = new JTextField(); // Student Reg No or Employee ID
        JComboBox<Department> cmbDepartment = new JComboBox<>();
        try {
            for (Department d : userDAO.getAllDepartments()) {
                cmbDepartment.addItem(d);
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }
        JTextField txtBatch = new JTextField("2023/2024");

        form.add(new JLabel("Username:"));
        form.add(txtUsername);
        form.add(new JLabel("Password:"));
        form.add(txtPassword);
        form.add(new JLabel("Full Name:"));
        form.add(txtFullName);
        form.add(new JLabel("Email Address:"));
        form.add(txtEmail);
        form.add(new JLabel("Phone No:"));
        form.add(txtPhone);
        form.add(new JLabel("User Role:"));
        form.add(cmbRole);
        form.add(new JLabel("Reg / Employee ID:"));
        form.add(txtExtraId);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Batch (Students only):"));
        form.add(txtBatch);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = createPrimaryButton("Save User");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String role = (String) cmbRole.getSelectedItem();
            String extraId = txtExtraId.getText().trim();
            Department dept = (Department) cmbDepartment.getSelectedItem();
            int deptId = dept != null ? dept.getDepartmentId() : 1;
            String batch = txtBatch.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all mandatory fields (Username, Password, Full Name).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success = userDAO.createUser(username, password, fullName, email, phone, role, extraId, deptId, batch);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshUsersTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create user. Ensure username/ID is unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating user: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTableModel.getValueAt(selectedRow, 0).toString());
        String currentName = usersTableModel.getValueAt(selectedRow, 2).toString();
        String currentEmail = usersTableModel.getValueAt(selectedRow, 3).toString();
        String currentPhone = usersTableModel.getValueAt(selectedRow, 4).toString();
        boolean currentActive = "Active".equalsIgnoreCase(usersTableModel.getValueAt(selectedRow, 8).toString());

        JDialog dialog = new JDialog(this, "Edit User (ID: " + userId + ")", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JTextField txtName = new JTextField(currentName);
        JTextField txtEmail = new JTextField(currentEmail);
        JTextField txtPhone = new JTextField(currentPhone);
        JCheckBox chkActive = new JCheckBox("Account Active", currentActive);

        form.add(new JLabel("Full Name:"));
        form.add(txtName);
        form.add(new JLabel("Email Address:"));
        form.add(txtEmail);
        form.add(new JLabel("Phone No:"));
        form.add(txtPhone);
        form.add(new JLabel("Active Status:"));
        form.add(chkActive);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnUpdate = createPrimaryButton("Update Profile");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnUpdate.addActionListener(e -> {
            try {
                boolean success = userDAO.updateUser(userId, txtName.getText().trim(), txtEmail.getText().trim(), txtPhone.getText().trim(), chkActive.isSelected());
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshUsersTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update user details.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating user: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTableModel.getValueAt(selectedRow, 0).toString());
        String username = usersTableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user '" + username + "' (ID: " + userId + ")?\nThis action will also delete related role records.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshUsersTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void unlockSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a locked user from the table to unlock.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTableModel.getValueAt(selectedRow, 0).toString());
        String username = usersTableModel.getValueAt(selectedRow, 1).toString();

        try {
            boolean success = userDAO.unlockUserAccount(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account for '" + username + "' unlocked successfully! (FR-AUTH-05)", "Account Unlocked", JOptionPane.INFORMATION_MESSAGE);
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unlock account.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (AdminOperationException ex) {
            JOptionPane.showMessageDialog(this, "Error unlocking user: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 3. COURSE MANAGEMENT PANEL (FR-ADM-02 & FR-ADM-06)
    // =========================================================================
    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Academic Course Management (FR-ADM-02 / FR-ADM-06)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAddCourse = createPrimaryButton("➕ Add Course");
        JButton btnEditCourse = createSecondaryButton("✏️ Edit Course");
        JButton btnDeleteCourse = createDangerButton("🗑️ Delete Course");

        actionButtons.add(btnAddCourse);
        actionButtons.add(btnEditCourse);
        actionButtons.add(btnDeleteCourse);

        topPanel.add(lblHeader, BorderLayout.WEST);
        topPanel.add(actionButtons, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Title", "Theory Credits", "Practical Credits", "Total Credits", "Department", "Assigned Lecturer"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        coursesTable = new JTable(coursesTableModel);
        styleTable(coursesTable);

        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        btnAddCourse.addActionListener(e -> showAddCourseDialog());
        btnEditCourse.addActionListener(e -> showEditCourseDialog());
        btnDeleteCourse.addActionListener(e -> deleteSelectedCourse());

        return panel;
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(this, "Add New Academic Course", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 15));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JTextField txtCode = new JTextField();
        JTextField txtTitle = new JTextField();
        JSpinner spinTheory = new JSpinner(new SpinnerNumberModel(2, 0, 4, 1));
        JSpinner spinPractical = new JSpinner(new SpinnerNumberModel(0, 0, 4, 1));

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        try {
            for (Department d : userDAO.getAllDepartments()) {
                cmbDepartment.addItem(d);
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        JComboBox<String> cmbLecturer = new JComboBox<>();
        cmbLecturer.addItem("0 - Unassigned");
        try {
            List<Map<String, Object>> lecturers = userDAO.getAllLecturers();
            for (Map<String, Object> lec : lecturers) {
                cmbLecturer.addItem(lec.get("lecturerId") + " - " + lec.get("fullName"));
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        form.add(new JLabel("Course Code:"));
        form.add(txtCode);
        form.add(new JLabel("Course Title:"));
        form.add(txtTitle);
        form.add(new JLabel("Theory Credits:"));
        form.add(spinTheory);
        form.add(new JLabel("Practical Credits:"));
        form.add(spinPractical);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Assign Lecturer:"));
        form.add(cmbLecturer);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = createPrimaryButton("Save Course");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String code = txtCode.getText().trim();
            String title = txtTitle.getText().trim();
            int theory = (Integer) spinTheory.getValue();
            int practical = (Integer) spinPractical.getValue();
            Department dept = (Department) cmbDepartment.getSelectedItem();
            int deptId = dept != null ? dept.getDepartmentId() : 1;

            Integer lecturerId = null;
            String selectedLec = (String) cmbLecturer.getSelectedItem();
            if (selectedLec != null && !selectedLec.startsWith("0")) {
                lecturerId = Integer.parseInt(selectedLec.split(" - ")[0]);
            }

            if (code.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Course Code and Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course course = new Course(code, title, theory, practical, deptId, "", lecturerId, "");
            try {
                boolean success = courseDAO.addCourse(course);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshCoursesTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add course. Make sure course code is unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding course: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditCourseDialog() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = coursesTableModel.getValueAt(selectedRow, 0).toString();
        String currentTitle = coursesTableModel.getValueAt(selectedRow, 1).toString();
        int currentTheory = Integer.parseInt(coursesTableModel.getValueAt(selectedRow, 2).toString());
        int currentPractical = Integer.parseInt(coursesTableModel.getValueAt(selectedRow, 3).toString());

        JDialog dialog = new JDialog(this, "Edit Course (" + courseCode + ")", true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JTextField txtTitle = new JTextField(currentTitle);
        JSpinner spinTheory = new JSpinner(new SpinnerNumberModel(currentTheory, 0, 4, 1));
        JSpinner spinPractical = new JSpinner(new SpinnerNumberModel(currentPractical, 0, 4, 1));

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        try {
            for (Department d : userDAO.getAllDepartments()) {
                cmbDepartment.addItem(d);
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        JComboBox<String> cmbLecturer = new JComboBox<>();
        cmbLecturer.addItem("0 - Unassigned");
        try {
            List<Map<String, Object>> lecturers = userDAO.getAllLecturers();
            for (Map<String, Object> lec : lecturers) {
                cmbLecturer.addItem(lec.get("lecturerId") + " - " + lec.get("fullName"));
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        form.add(new JLabel("Course Title:"));
        form.add(txtTitle);
        form.add(new JLabel("Theory Credits:"));
        form.add(spinTheory);
        form.add(new JLabel("Practical Credits:"));
        form.add(spinPractical);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Assign Lecturer:"));
        form.add(cmbLecturer);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnUpdate = createPrimaryButton("Update Course");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnUpdate.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            int theory = (Integer) spinTheory.getValue();
            int practical = (Integer) spinPractical.getValue();
            Department dept = (Department) cmbDepartment.getSelectedItem();
            int deptId = dept != null ? dept.getDepartmentId() : 1;

            Integer lecturerId = null;
            String selectedLec = (String) cmbLecturer.getSelectedItem();
            if (selectedLec != null && !selectedLec.startsWith("0")) {
                lecturerId = Integer.parseInt(selectedLec.split(" - ")[0]);
            }

            Course course = new Course(courseCode, title, theory, practical, deptId, "", lecturerId, "");
            try {
                boolean success = courseDAO.updateCourse(course);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshCoursesTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update course.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating course: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = coursesTableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete course '" + courseCode + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = courseDAO.deleteCourse(courseCode);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCoursesTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete course.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 4. NOTICE MANAGEMENT PANEL (FR-ADM-03)
    // =========================================================================
    private JPanel buildNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Notice Publishing & Management (FR-ADM-03)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAddNotice = createPrimaryButton("📢 Publish Notice");
        JButton btnEditNotice = createSecondaryButton("✏️ Edit Notice");
        JButton btnDeleteNotice = createDangerButton("🗑️ Delete Notice");

        actionButtons.add(btnAddNotice);
        actionButtons.add(btnEditNotice);
        actionButtons.add(btnDeleteNotice);

        topPanel.add(lblHeader, BorderLayout.WEST);
        topPanel.add(actionButtons, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Notice ID", "Title", "Target Audience", "Posted By", "Posted Date", "Content"};
        noticesTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        noticesTable = new JTable(noticesTableModel);
        styleTable(noticesTable);

        panel.add(new JScrollPane(noticesTable), BorderLayout.CENTER);

        btnAddNotice.addActionListener(e -> showAddNoticeDialog());
        btnEditNotice.addActionListener(e -> showEditNoticeDialog());
        btnDeleteNotice.addActionListener(e -> deleteSelectedNotice());

        return panel;
    }

    private void showAddNoticeDialog() {
        JDialog dialog = new JDialog(this, "Publish Faculty Notice", true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new BorderLayout(10, 15));
        form.setBorder(new EmptyBorder(20, 25, 15, 25));

        JPanel headerFields = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField txtTitle = new JTextField();
        JComboBox<String> cmbAudience = new JComboBox<>(new String[]{"ALL", "LECTURER", "TECH_OFFICER", "UNDERGRADUATE"});

        headerFields.add(new JLabel("Notice Title:"));
        headerFields.add(txtTitle);
        headerFields.add(new JLabel("Target Audience:"));
        headerFields.add(cmbAudience);

        form.add(headerFields, BorderLayout.NORTH);

        JPanel contentBlock = new JPanel(new BorderLayout(5, 5));
        contentBlock.add(new JLabel("Notice Content:"), BorderLayout.NORTH);
        JTextArea txtContent = new JTextArea(8, 30);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        contentBlock.add(new JScrollPane(txtContent), BorderLayout.CENTER);

        form.add(contentBlock, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnPublish = createPrimaryButton("Publish Now");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPublish.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String audience = (String) cmbAudience.getSelectedItem();
            String content = txtContent.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Notice Title and Content cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Notice notice = new Notice(0, title, content, audience, 1, null);
            try {
                boolean success = noticeDAO.addNotice(notice);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshNoticesTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to publish notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error publishing notice: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnPublish);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditNoticeDialog() {
        int selectedRow = noticesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noticeId = Integer.parseInt(noticesTableModel.getValueAt(selectedRow, 0).toString());
        String currentTitle = noticesTableModel.getValueAt(selectedRow, 1).toString();
        String currentAudience = noticesTableModel.getValueAt(selectedRow, 2).toString();
        String currentContent = noticesTableModel.getValueAt(selectedRow, 5).toString();

        JDialog dialog = new JDialog(this, "Edit Notice (ID: " + noticeId + ")", true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new BorderLayout(10, 15));
        form.setBorder(new EmptyBorder(20, 25, 15, 25));

        JPanel headerFields = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField txtTitle = new JTextField(currentTitle);
        JComboBox<String> cmbAudience = new JComboBox<>(new String[]{"ALL", "LECTURER", "TECH_OFFICER", "UNDERGRADUATE"});
        cmbAudience.setSelectedItem(currentAudience);

        headerFields.add(new JLabel("Notice Title:"));
        headerFields.add(txtTitle);
        headerFields.add(new JLabel("Target Audience:"));
        headerFields.add(cmbAudience);

        form.add(headerFields, BorderLayout.NORTH);

        JPanel contentBlock = new JPanel(new BorderLayout(5, 5));
        contentBlock.add(new JLabel("Notice Content:"), BorderLayout.NORTH);
        JTextArea txtContent = new JTextArea(currentContent, 8, 30);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        contentBlock.add(new JScrollPane(txtContent), BorderLayout.CENTER);

        form.add(contentBlock, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnUpdate = createPrimaryButton("Update Notice");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnUpdate.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String audience = (String) cmbAudience.getSelectedItem();
            String content = txtContent.getText().trim();

            Notice notice = new Notice(noticeId, title, content, audience, 1, null);
            try {
                boolean success = noticeDAO.updateNotice(notice);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshNoticesTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating notice: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedNotice() {
        int selectedRow = noticesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noticeId = Integer.parseInt(noticesTableModel.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete notice #" + noticeId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = noticeDAO.deleteNotice(noticeId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Notice deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshNoticesTable();
                    refreshDashboardStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting notice: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 5. TIMETABLE MANAGEMENT PANEL (FR-ADM-04)
    // =========================================================================
    private JPanel buildTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Academic Timetable Management (FR-ADM-04)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAddSlot = createPrimaryButton("➕ Add Slot");
        JButton btnEditSlot = createSecondaryButton("✏️ Edit Slot");
        JButton btnDeleteSlot = createDangerButton("🗑️ Delete Slot");

        actionButtons.add(btnAddSlot);
        actionButtons.add(btnEditSlot);
        actionButtons.add(btnDeleteSlot);

        topPanel.add(lblHeader, BorderLayout.WEST);
        topPanel.add(actionButtons, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Timetable ID", "Course Code", "Course Title", "Department", "Session Type", "Day of Week", "Start Time", "End Time", "Venue"};
        timetablesTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        timetablesTable = new JTable(timetablesTableModel);
        styleTable(timetablesTable);

        panel.add(new JScrollPane(timetablesTable), BorderLayout.CENTER);

        btnAddSlot.addActionListener(e -> showAddTimetableDialog());
        btnEditSlot.addActionListener(e -> showEditTimetableDialog());
        btnDeleteSlot.addActionListener(e -> deleteSelectedTimetableSlot());

        return panel;
    }

    private void showAddTimetableDialog() {
        JDialog dialog = new JDialog(this, "Add Timetable Slot", true);
        dialog.setSize(480, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JComboBox<String> cmbCourses = new JComboBox<>();
        try {
            for (Course c : courseDAO.getAllCourses()) {
                cmbCourses.addItem(c.getCourseCode());
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        try {
            for (Department d : userDAO.getAllDepartments()) {
                cmbDepartment.addItem(d);
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        JComboBox<String> cmbType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL", "EXAM"});
        JComboBox<String> cmbDay = new JComboBox<>(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"});
        JTextField txtStart = new JTextField("08:00:00");
        JTextField txtEnd = new JTextField("10:00:00");
        JTextField txtVenue = new JTextField("Lecture Hall 01");

        form.add(new JLabel("Course Code:"));
        form.add(cmbCourses);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Session Type:"));
        form.add(cmbType);
        form.add(new JLabel("Day of Week:"));
        form.add(cmbDay);
        form.add(new JLabel("Start Time (HH:mm:ss):"));
        form.add(txtStart);
        form.add(new JLabel("End Time (HH:mm:ss):"));
        form.add(txtEnd);
        form.add(new JLabel("Venue:"));
        form.add(txtVenue);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = createPrimaryButton("Save Slot");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String course = (String) cmbCourses.getSelectedItem();
            Department dept = (Department) cmbDepartment.getSelectedItem();
            int deptId = dept != null ? dept.getDepartmentId() : 1;
            String type = (String) cmbType.getSelectedItem();
            String day = (String) cmbDay.getSelectedItem();
            String startStr = txtStart.getText().trim();
            String endStr = txtEnd.getText().trim();
            String venue = txtVenue.getText().trim();

            try {
                Time startTime = Time.valueOf(startStr);
                Time endTime = Time.valueOf(endStr);

                Timetable t = new Timetable(0, course, deptId, type, day, startTime, endTime, venue);
                boolean success = timetableDAO.addTimetableEntry(t);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Timetable slot added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshTimetablesTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add timetable slot.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid time format. Please use HH:mm:ss (e.g. 09:00:00).", "Input Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding timetable slot: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditTimetableDialog() {
        int selectedRow = timetablesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int slotId = Integer.parseInt(timetablesTableModel.getValueAt(selectedRow, 0).toString());
        String currentCourse = timetablesTableModel.getValueAt(selectedRow, 1).toString();
        String currentType = timetablesTableModel.getValueAt(selectedRow, 4).toString();
        String currentDay = timetablesTableModel.getValueAt(selectedRow, 5).toString();
        String currentStart = timetablesTableModel.getValueAt(selectedRow, 6).toString();
        String currentEnd = timetablesTableModel.getValueAt(selectedRow, 7).toString();
        String currentVenue = timetablesTableModel.getValueAt(selectedRow, 8).toString();

        JDialog dialog = new JDialog(this, "Edit Timetable Slot (#" + slotId + ")", true);
        dialog.setSize(480, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JComboBox<String> cmbCourses = new JComboBox<>();
        try {
            for (Course c : courseDAO.getAllCourses()) {
                cmbCourses.addItem(c.getCourseCode());
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }
        cmbCourses.setSelectedItem(currentCourse);

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        try {
            for (Department d : userDAO.getAllDepartments()) {
                cmbDepartment.addItem(d);
            }
        } catch (AdminOperationException e) {
            e.printStackTrace();
        }

        JComboBox<String> cmbType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL", "EXAM"});
        cmbType.setSelectedItem(currentType);

        JComboBox<String> cmbDay = new JComboBox<>(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"});
        cmbDay.setSelectedItem(currentDay);

        JTextField txtStart = new JTextField(currentStart);
        JTextField txtEnd = new JTextField(currentEnd);
        JTextField txtVenue = new JTextField(currentVenue);

        form.add(new JLabel("Course Code:"));
        form.add(cmbCourses);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Session Type:"));
        form.add(cmbType);
        form.add(new JLabel("Day of Week:"));
        form.add(cmbDay);
        form.add(new JLabel("Start Time (HH:mm:ss):"));
        form.add(txtStart);
        form.add(new JLabel("End Time (HH:mm:ss):"));
        form.add(txtEnd);
        form.add(new JLabel("Venue:"));
        form.add(txtVenue);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnUpdate = createPrimaryButton("Update Slot");
        JButton btnCancel = createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnUpdate.addActionListener(e -> {
            String course = (String) cmbCourses.getSelectedItem();
            Department dept = (Department) cmbDepartment.getSelectedItem();
            int deptId = dept != null ? dept.getDepartmentId() : 1;
            String type = (String) cmbType.getSelectedItem();
            String day = (String) cmbDay.getSelectedItem();
            String startStr = txtStart.getText().trim();
            String endStr = txtEnd.getText().trim();
            String venue = txtVenue.getText().trim();

            try {
                Time startTime = Time.valueOf(startStr);
                Time endTime = Time.valueOf(endStr);

                Timetable t = new Timetable(slotId, course, deptId, type, day, startTime, endTime, venue);
                boolean success = timetableDAO.updateTimetableEntry(t);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Timetable slot updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshTimetablesTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update timetable slot.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid time format. Please use HH:mm:ss (e.g. 09:00:00).", "Input Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating timetable slot: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedTimetableSlot() {
        int selectedRow = timetablesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int slotId = Integer.parseInt(timetablesTableModel.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete timetable slot #" + slotId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = timetableDAO.deleteTimetableEntry(slotId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Timetable slot deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTimetablesTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete slot.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AdminOperationException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting timetable slot: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // DATA REFRESH HELPERS (Multithreaded Background Loading)
    // =========================================================================
    private void refreshAllData() {
        // Run data fetching in a background thread to keep UI completely responsive
        new Thread(() -> {
            refreshDashboardStats();
            refreshUsersTable();
            refreshCoursesTable();
            refreshNoticesTable();
            refreshTimetablesTable();
        }, "Admin-DataRefresh-Thread").start();
    }

    private void refreshDashboardStats() {
        try {
            Map<String, Integer> stats = userDAO.getSystemStats();
            SwingUtilities.invokeLater(() -> {
                lblTotalUsersVal.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
                lblStudentsVal.setText(String.valueOf(stats.getOrDefault("students", 0)));
                lblLecturersVal.setText(String.valueOf(stats.getOrDefault("lecturers", 0)));
                lblTechOfficersVal.setText(String.valueOf(stats.getOrDefault("techOfficers", 0)));
                lblCoursesVal.setText(String.valueOf(stats.getOrDefault("courses", 0)));
                lblNoticesVal.setText(String.valueOf(stats.getOrDefault("notices", 0)));
            });
        } catch (AdminOperationException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }

    private void refreshUsersTable() {
        try {
            List<Map<String, Object>> users = userDAO.getAllUsers();
            SwingUtilities.invokeLater(() -> {
                usersTableModel.setRowCount(0);
                for (Map<String, Object> u : users) {
                    usersTableModel.addRow(new Object[]{
                            u.get("userId"),
                            u.get("username"),
                            u.get("fullName"),
                            u.get("email"),
                            u.get("phoneNo"),
                            u.get("role"),
                            u.get("refId"),
                            u.get("departmentName"),
                            Boolean.TRUE.equals(u.get("isActive")) ? "Active" : "Locked / Inactive",
                            u.get("failedLogins")
                    });
                }
            });
        } catch (AdminOperationException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void refreshCoursesTable() {
        try {
            List<Course> courses = courseDAO.getAllCourses();
            SwingUtilities.invokeLater(() -> {
                coursesTableModel.setRowCount(0);
                for (Course c : courses) {
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
            });
        } catch (AdminOperationException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private void refreshNoticesTable() {
        try {
            List<Notice> notices = noticeDAO.getAllNotices();

            // Synchronized block (Lesson 08 Slide 17): Safely update notice titles for NoticeTickerThread
            synchronized (noticeLock) {
                latestNoticeTitles.clear();
                for (Notice n : notices) {
                    if (n.getTitle() != null && !n.getTitle().trim().isEmpty()) {
                        latestNoticeTitles.add(n.getTitle().trim() + " (" + n.getTargetAudience() + ")");
                    }
                }
            }

            SwingUtilities.invokeLater(() -> {
                noticesTableModel.setRowCount(0);
                for (Notice n : notices) {
                    noticesTableModel.addRow(new Object[]{
                            n.getNoticeId(),
                            n.getTitle(),
                            n.getTargetAudience(),
                            n.getPostedByName() != null ? n.getPostedByName() : "Administrator",
                            n.getPostedDate() != null ? n.getPostedDate().toString() : "Recent",
                            n.getContent()
                    });
                }
            });
        } catch (AdminOperationException e) {
            System.err.println("Error loading notices: " + e.getMessage());
        }
    }

    private void refreshTimetablesTable() {
        try {
            List<Timetable> list = timetableDAO.getAllTimetables();
            SwingUtilities.invokeLater(() -> {
                timetablesTableModel.setRowCount(0);
                for (Timetable t : list) {
                    timetablesTableModel.addRow(new Object[]{
                            t.getTimetableId(),
                            t.getCourseCode(),
                            t.getCourseTitle() != null ? t.getCourseTitle() : "-",
                            t.getDepartmentName() != null ? t.getDepartmentName() : "-",
                            t.getSessionType(),
                            t.getDayOfWeek(),
                            t.getStartTime() != null ? t.getStartTime().toString() : "-",
                            t.getEndTime() != null ? t.getEndTime().toString() : "-",
                            t.getVenue()
                    });
                }
            });
        } catch (AdminOperationException e) {
            System.err.println("Error loading timetables: " + e.getMessage());
        }
    }

    // =========================================================================
    // UI STYLING UTILITIES
    // =========================================================================
    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(219, 234, 254)); // Soft blue
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(51, 65, 85));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(37, 99, 235)); // Vibrant Blue
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(51, 65, 85));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(239, 68, 68)); // Red
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createWarningButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(217, 119, 6)); // Amber
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
