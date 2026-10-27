package gui;

import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * DashboardTemplateDemo - Complete Reference Implementation of BaseDashboardUI.
 * Demonstrates how team members (Admin, Student, Lecturer, Tech Officer) can build
 * their dashboards matching the exact UI specification with minimal code.
 */
public class DashboardTemplateDemo extends BaseDashboardUI {

    public DashboardTemplateDemo() {
        super(
                "Admin Dashboard",
                "LMS",
                "Admin Panel",
                "System",
                "LMS Administration Portal"
        );

        // 1. Dashboard Overview Tab
        addNavItem("overview", "Dashboard Overview", buildOverviewPanel());

        // 2. Manage Students Tab
        addNavItem("students", "Manage Students", buildManageStudentsPanel());

        // 3. Course Management Tab
        addNavItem("courses", "Course Management", buildCourseManagementPanel());

        // 4. Medical Requests Tab
        addNavItem("medical", "Medical Requests", buildMedicalRequestsPanel());

        // 5. Notice Management Tab
        addNavItem("notices", "Notices", buildNoticeManagementPanel());

        // 6. Timetables Tab
        addNavItem("timetables", "Timetables", buildTimetableManagementPanel());
    }

    // =========================================================================
    // 1. DASHBOARD OVERVIEW PANEL (Matches Screenshot 15)
    // =========================================================================
    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Top Stat Cards
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        statsRow.setOpaque(false);
        statsRow.add(new UITheme.StatCard("Total Students", "20", UITheme.PRIMARY_BLUE));
        statsRow.add(new UITheme.StatCard("Active Courses", "8", UITheme.SUCCESS_GREEN));
        statsRow.add(new UITheme.StatCard("Faculty Members", "11", UITheme.WARNING_AMBER));
        panel.add(statsRow, BorderLayout.NORTH);

        // Main Overview Card with Table
        UITheme.RoundedCardPanel card = new UITheme.RoundedCardPanel(16);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Sub-tabs row (Student List, Lecturers, Tech Officers, Courses)
        JPanel tabButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tabButtons.setOpaque(false);
        tabButtons.setBorder(new EmptyBorder(0, 0, 14, 0));

        JButton btnStudentsTab = UITheme.createSecondaryButton("Student List");
        JButton btnLecturersTab = UITheme.createSecondaryButton("Lecturers");
        JButton btnOfficersTab = UITheme.createSecondaryButton("Technical Officers");
        JButton btnCoursesTab = UITheme.createSecondaryButton("Courses");
        tabButtons.add(btnStudentsTab);
        tabButtons.add(btnLecturersTab);
        tabButtons.add(btnOfficersTab);
        tabButtons.add(btnCoursesTab);

        // Table
        String[] columns = {"Student ID", "Full Name", "Degree Program"};
        Object[][] data = {
                {"TG/2023/1753", "Harshana Prabhash", "ICT"},
                {"TG/2023/1754", "Nimal Perera", "ICT"},
                {"TG/2023/1755", "Saman Silva", "ICT"},
                {"TG/2023/1756", "Kasun Weerasinghe", "ICT"},
                {"TG/2023/1757", "Nuwan Dias", "ICT"},
                {"TG/2023/1758", "Tharindu Gunasekara", "ICT"},
                {"TG/2023/1759", "Malith Ekanayake", "ICT"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = UITheme.createTableScrollPane(table);

        JPanel centerBox = new JPanel(new BorderLayout());
        centerBox.setOpaque(false);
        centerBox.add(tabButtons, BorderLayout.NORTH);
        centerBox.add(scrollPane, BorderLayout.CENTER);

        card.add(centerBox, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // 2. MANAGE STUDENTS PANEL (Matches Screenshot 14)
    // =========================================================================
    private JPanel buildManageStudentsPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        // Form fields grid
        JPanel formGrid = new JPanel(new GridLayout(4, 2, 16, 12));
        formGrid.setOpaque(false);

        UITheme.ModernTextField txtId = new UITheme.ModernTextField("e.g. TG/2023/0001");
        UITheme.ModernTextField txtFirst = new UITheme.ModernTextField("e.g. Nimal");
        UITheme.ModernTextField txtLast = new UITheme.ModernTextField("e.g. Fernando");
        UITheme.ModernTextField txtUser = new UITheme.ModernTextField("Unique login username");
        UITheme.ModernTextField txtEmail = new UITheme.ModernTextField("Nimal@university.com");
        UITheme.ModernTextField txtDegree = new UITheme.ModernTextField("e.g. BICT");
        UITheme.ModernTextField txtLevel = new UITheme.ModernTextField("1 - 4");
        UITheme.ModernTextField txtGpa = new UITheme.ModernTextField("e.g. 3.5");

        formGrid.add(createLabeledField("Student ID (Format: TG/XXXX/XXXX)", txtId));
        formGrid.add(createLabeledField("Level", txtLevel));
        formGrid.add(createLabeledField("First Name", txtFirst));
        formGrid.add(createLabeledField("Last Name", txtLast));
        formGrid.add(createLabeledField("Username", txtUser));
        formGrid.add(createLabeledField("Email Address", txtEmail));
        formGrid.add(createLabeledField("Degree Program", txtDegree));
        formGrid.add(createLabeledField("Initial GPA", txtGpa));

        content.add(formGrid, BorderLayout.NORTH);

        // Action Buttons Row (Right aligned)
        JButton btnDelete = UITheme.createDangerButton("Delete");
        JButton btnUpdate = UITheme.createSecondaryButton("Update Info");
        JButton btnRegister = UITheme.createPrimaryButton("Register Student");

        JPanel btnRow = createButtonBar(true, btnDelete, btnUpdate, btnRegister);
        content.add(btnRow, BorderLayout.SOUTH);

        return createContentCard("Manage Undergraduate Student", content);
    }

    // =========================================================================
    // 3. COURSE MANAGEMENT PANEL (Matches Screenshot 6)
    // =========================================================================
    private JPanel buildCourseManagementPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        UITheme.ModernTextField txtCode = new UITheme.ModernTextField("e.g. ICT1202");
        UITheme.ModernTextField txtName = new UITheme.ModernTextField("e.g. Database Systems");
        UITheme.ModernTextField txtCredits = new UITheme.ModernTextField("e.g. 3");

        txtCode.setMaximumSize(new Dimension(340, 38));
        txtName.setMaximumSize(new Dimension(340, 38));
        txtCredits.setMaximumSize(new Dimension(340, 38));

        fields.add(createLabeledField("Course Code (Primary Key)", txtCode));
        fields.add(Box.createVerticalStrut(12));
        fields.add(createLabeledField("Course Name", txtName));
        fields.add(Box.createVerticalStrut(12));
        fields.add(createLabeledField("Credits", txtCredits));

        content.add(fields, BorderLayout.NORTH);

        // Action Buttons
        JButton btnSave = UITheme.createSuccessButton("Save");
        JButton btnUpdate = UITheme.createPrimaryButton("Update");
        JButton btnDelete = UITheme.createDangerButton("Delete");

        JPanel btnRow = createButtonBar(true, btnSave, btnUpdate, btnDelete);
        content.add(btnRow, BorderLayout.SOUTH);

        return createContentCard("Course Management", content);
    }

    // =========================================================================
    // 4. MEDICAL REQUESTS PANEL (Matches Screenshot 7)
    // =========================================================================
    private JPanel buildMedicalRequestsPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);

        // Search Bar
        UITheme.ModernTextField txtSearch = new UITheme.ModernTextField("Search Student ID (e.g. TG/2023/1753)");
        JButton btnSearch = UITheme.createPrimaryButton("Search");
        JPanel searchBar = createSearchBar(txtSearch, btnSearch);
        content.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Student ID", "Valid From", "Valid To", "Status", "Description"};
        Object[][] data = {
                {"TG/2023/1753", "2026-02-10", "2026-02-12", "Approved", "Viral fever with medical certificate"},
                {"TG/2023/1753", "2026-03-05", "2026-03-06", "Pending", "Dental procedure"},
                {"TG/2023/1753", "2026-04-01", "2026-04-02", "Rejected", "Family emergency (documentation incomplete)"},
                {"TG/2023/1754", "2026-03-01", "2026-03-02", "Approved", "Minor injury"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = UITheme.createTableScrollPane(table);
        content.add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar (Count + Action Buttons)
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.add(UITheme.createRecordCountLabel("Found 4 records."), BorderLayout.WEST);

        JButton btnApprove = UITheme.createSuccessButton("Approve");
        JButton btnDeny = UITheme.createDangerButton("Deny");
        bottomBar.add(createButtonBar(true, btnApprove, btnDeny), BorderLayout.EAST);

        content.add(bottomBar, BorderLayout.SOUTH);

        return createContentCard("Medical Submission Management", content);
    }

    // =========================================================================
    // 5. NOTICE MANAGEMENT PANEL (Matches Screenshot 1)
    // =========================================================================
    private JPanel buildNoticeManagementPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);

        // Notices Table
        String[] columns = {"ID", "Title", "Posted By", "Posted At"};
        Object[][] data = {
                {"4", "Course Registration", "Academic Office", "2026-10-07 09:18:21.0"},
                {"3", "Medical Submission", "Technical Division", "2026-10-07 09:18:21.0"},
                {"2", "System Maintenance", "System Admin", "2026-10-07 09:18:21.0"},
                {"1", "Mid Exam Schedule", "Admin Office", "2026-10-07 09:18:21.0"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = UITheme.createTableScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 220));
        content.add(scrollPane, BorderLayout.NORTH);

        // Form Fields (Title, Posted By, Notice message textarea)
        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row1.setOpaque(false);
        UITheme.ModernTextField txtTitle = new UITheme.ModernTextField("Title");
        UITheme.ModernTextField txtPostedBy = new UITheme.ModernTextField("Posted By");
        txtTitle.setPreferredSize(new Dimension(240, 36));
        txtPostedBy.setPreferredSize(new Dimension(200, 36));
        row1.add(txtTitle);
        row1.add(txtPostedBy);

        JTextArea txtMsg = new JTextArea(4, 20);
        txtMsg.setFont(UITheme.FONT_REGULAR);
        txtMsg.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane msgScroll = new JScrollPane(txtMsg);
        msgScroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));

        form.add(row1, BorderLayout.NORTH);
        form.add(msgScroll, BorderLayout.CENTER);

        // Buttons
        JButton btnAdd = UITheme.createSuccessButton("Add");
        JButton btnUpdate = UITheme.createPrimaryButton("Update");
        JButton btnDelete = UITheme.createDangerButton("Delete");
        JPanel btnBar = createButtonBar(false, btnAdd, btnUpdate, btnDelete);
        form.add(btnBar, BorderLayout.SOUTH);

        content.add(form, BorderLayout.CENTER);

        return createContentCard("Notice Management", content);
    }

    // =========================================================================
    // 6. TIMETABLE MANAGEMENT PANEL (Matches Screenshot 2)
    // =========================================================================
    private JPanel buildTimetableManagementPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);

        String[] columns = {"ID", "Department", "Level", "Day", "Start", "End", "Course", "Venue"};
        Object[][] data = {
                {"5", "ICT", "2", "Friday", "10:30:00", "12:30:00", "ICT2253", "Hall C"},
                {"1", "ICT", "2", "Monday", "08:30:00", "10:30:00", "ICT2213", "Lab 1"},
                {"6", "ICT", "2", "Monday", "13:30:00", "15:30:00", "ICT2262", "Seminar Room 1"},
                {"4", "ICT", "2", "Thursday", "08:30:00", "10:30:00", "ICT2242", "Lab 2"},
                {"2", "ICT", "2", "Tuesday", "10:30:00", "12:30:00", "ICT2223", "Hall A"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = UITheme.createTableScrollPane(table);
        content.add(scrollPane, BorderLayout.CENTER);

        // Inputs row + Buttons
        JPanel bottomBox = new JPanel(new BorderLayout(0, 10));
        bottomBox.setOpaque(false);

        JPanel inputsRow = new JPanel(new GridLayout(2, 4, 10, 8));
        inputsRow.setOpaque(false);
        inputsRow.add(new UITheme.ModernTextField("Department"));
        inputsRow.add(new UITheme.ModernTextField("Level"));
        inputsRow.add(new UITheme.ModernTextField("Day (e.g. Monday)"));
        inputsRow.add(new UITheme.ModernTextField("Course Code"));
        inputsRow.add(new UITheme.ModernTextField("Start (HH:MM:SS)"));
        inputsRow.add(new UITheme.ModernTextField("End (HH:MM:SS)"));
        inputsRow.add(new UITheme.ModernTextField("Venue"));

        JButton btnAdd = UITheme.createSuccessButton("Add");
        JButton btnUpdate = UITheme.createPrimaryButton("Update");
        JButton btnDelete = UITheme.createDangerButton("Delete");
        JPanel btnBar = createButtonBar(false, btnAdd, btnUpdate, btnDelete);

        bottomBox.add(inputsRow, BorderLayout.NORTH);
        bottomBox.add(btnBar, BorderLayout.SOUTH);
        content.add(bottomBox, BorderLayout.SOUTH);

        return createContentCard("Timetable Management", content);
    }

    // Helper to wrap label + input
    private static JPanel createLabeledField(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    /**
     * Standalone main launcher for previewing the template.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DashboardTemplateDemo().setVisible(true);
        });
    }
}
