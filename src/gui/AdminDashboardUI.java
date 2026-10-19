package gui;

import dao.AdminCourseDAO;
import dao.AdminNoticeDAO;
import dao.AdminTimetableDAO;
import dao.AdminUserDAO;
import model.Course;
import model.Department;
import model.Notice;
import model.Timetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.util.List;
import java.util.Map;

/**
 * AdminDashboardUI - Complete Graphical Desktop Management Interface.
 * Implements FR-ADM-01 through FR-ADM-06 and FR-AUTH-05.
 */
public class AdminDashboardUI extends JFrame {

    // DAOs
    private final AdminUserDAO userDAO = new AdminUserDAO();
    private final AdminCourseDAO courseDAO = new AdminCourseDAO();
    private final AdminNoticeDAO noticeDAO = new AdminNoticeDAO();
    private final AdminTimetableDAO timetableDAO = new AdminTimetableDAO();

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
        setTitle("Faculty of Technology Management System (FTMS) - Administrator Portal [TG/2024/2086]");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
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

        JLabel lblSub = new JLabel("System Administrator: K.A.M.I. Geethanjana (TG/2024/2086)");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

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
                dispose();
                new LoginUI().setVisible(true);
            }
        });

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

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

        actionButtons.add(btnAddUser);
        actionButtons.add(btnEditUser);
        actionButtons.add(btnDeleteUser);
        actionButtons.add(btnUnlock);

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

        return panel;
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
        for (Department d : userDAO.getAllDepartments()) {
            cmbDepartment.addItem(d);
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

            boolean success = userDAO.createUser(username, password, fullName, email, phone, role, extraId, deptId, batch);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshUsersTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to create user. Ensure username/ID is unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = userDAO.updateUser(userId, txtName.getText().trim(), txtEmail.getText().trim(), txtPhone.getText().trim(), chkActive.isSelected());
            if (success) {
                JOptionPane.showMessageDialog(dialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update user details.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUsersTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Database Error", JOptionPane.ERROR_MESSAGE);
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

        boolean success = userDAO.unlockUserAccount(userId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account for '" + username + "' unlocked successfully! (FR-AUTH-05)", "Account Unlocked", JOptionPane.INFORMATION_MESSAGE);
            refreshUsersTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to unlock account.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
        for (Department d : userDAO.getAllDepartments()) {
            cmbDepartment.addItem(d);
        }

        JComboBox<String> cmbLecturer = new JComboBox<>();
        cmbLecturer.addItem("0 - Unassigned");
        List<Map<String, Object>> lecturers = userDAO.getAllLecturers();
        for (Map<String, Object> lec : lecturers) {
            cmbLecturer.addItem(lec.get("lecturerId") + " - " + lec.get("fullName"));
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
            boolean success = courseDAO.addCourse(course);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCoursesTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add course. Make sure course code is unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
        for (Department d : userDAO.getAllDepartments()) {
            cmbDepartment.addItem(d);
        }

        JComboBox<String> cmbLecturer = new JComboBox<>();
        cmbLecturer.addItem("0 - Unassigned");
        List<Map<String, Object>> lecturers = userDAO.getAllLecturers();
        for (Map<String, Object> lec : lecturers) {
            cmbLecturer.addItem(lec.get("lecturerId") + " - " + lec.get("fullName"));
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
            boolean success = courseDAO.updateCourse(course);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCoursesTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update course.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = courseDAO.deleteCourse(courseCode);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCoursesTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = noticeDAO.addNotice(notice);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshNoticesTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to publish notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = noticeDAO.updateNotice(notice);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshNoticesTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            boolean success = noticeDAO.deleteNotice(noticeId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Notice deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshNoticesTable();
                refreshDashboardStats();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete notice.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
        for (Course c : courseDAO.getAllCourses()) {
            cmbCourses.addItem(c.getCourseCode());
        }

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        for (Department d : userDAO.getAllDepartments()) {
            cmbDepartment.addItem(d);
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
        for (Course c : courseDAO.getAllCourses()) {
            cmbCourses.addItem(c.getCourseCode());
        }
        cmbCourses.setSelectedItem(currentCourse);

        JComboBox<Department> cmbDepartment = new JComboBox<>();
        for (Department d : userDAO.getAllDepartments()) {
            cmbDepartment.addItem(d);
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
            boolean success = timetableDAO.deleteTimetableEntry(slotId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Timetable slot deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTimetablesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete slot.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // DATA REFRESH HELPERS
    // =========================================================================
    private void refreshAllData() {
        refreshDashboardStats();
        refreshUsersTable();
        refreshCoursesTable();
        refreshNoticesTable();
        refreshTimetablesTable();
    }

    private void refreshDashboardStats() {
        Map<String, Integer> stats = userDAO.getSystemStats();
        lblTotalUsersVal.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
        lblStudentsVal.setText(String.valueOf(stats.getOrDefault("students", 0)));
        lblLecturersVal.setText(String.valueOf(stats.getOrDefault("lecturers", 0)));
        lblTechOfficersVal.setText(String.valueOf(stats.getOrDefault("techOfficers", 0)));
        lblCoursesVal.setText(String.valueOf(stats.getOrDefault("courses", 0)));
        lblNoticesVal.setText(String.valueOf(stats.getOrDefault("notices", 0)));
    }

    private void refreshUsersTable() {
        usersTableModel.setRowCount(0);
        List<Map<String, Object>> users = userDAO.getAllUsers();
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
    }

    private void refreshCoursesTable() {
        coursesTableModel.setRowCount(0);
        List<Course> courses = courseDAO.getAllCourses();
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
    }

    private void refreshNoticesTable() {
        noticesTableModel.setRowCount(0);
        List<Notice> notices = noticeDAO.getAllNotices();
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
    }

    private void refreshTimetablesTable() {
        timetablesTableModel.setRowCount(0);
        List<Timetable> list = timetableDAO.getAllTimetables();
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
