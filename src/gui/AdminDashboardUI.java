package gui;

import dao.AdminCourseDAO;
import dao.AdminNoticeDAO;
import dao.AdminTimetableDAO;
import dao.AdminUserDAO;
import model.Notice;
import model.Timetable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardUI extends JFrame {
    private JPanel contentPanel;

    public AdminDashboardUI() {
        setTitle("Admin Dashboard - FTMS");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JButton btnManageUsers = createSidebarButton("Manage Users");
        JButton btnManageCourses = createSidebarButton("Manage Courses");
        JButton btnManageNotices = createSidebarButton("Notices");
        JButton btnManageTimetable = createSidebarButton("Timetable");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(btnManageUsers);
        sidebar.add(btnManageCourses);
        sidebar.add(btnManageNotices);
        sidebar.add(btnManageTimetable);
        sidebar.add(btnLogout);

        // Main Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        showWelcomeMessage();

        // Add to Frame
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Action Listeners
        btnManageUsers.addActionListener(e -> showUsers());
        btnManageCourses.addActionListener(e -> showCourses());
        btnManageNotices.addActionListener(e -> showNotices());
        btnManageTimetable.addActionListener(e -> showTimetable());

        btnLogout.addActionListener(e -> {
            this.dispose();
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            System.exit(0);
        });
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    private void showWelcomeMessage() {
        contentPanel.removeAll();
        JLabel lblWelcome = new JLabel("Welcome, System Admin", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUsers() {
        contentPanel.removeAll();
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        AdminUserDAO dao = new AdminUserDAO();
        List<String[]> users = dao.getAllUsers();
        for (String[] u : users) {
            model.addRow(u);
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCourses() {
        contentPanel.removeAll();
        String[] columns = {"Course Code", "Title", "Theory Credits", "Practical Credits", "Dept ID", "Lecturer ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        AdminCourseDAO dao = new AdminCourseDAO();
        List<String[]> courses = dao.getAllCourses();
        for (String[] c : courses) {
            model.addRow(c);
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotices() {
        contentPanel.removeAll();
        String[] columns = {"Notice ID", "Title", "Content", "Audience", "Posted Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        AdminNoticeDAO dao = new AdminNoticeDAO();
        List<Notice> notices = dao.getAllNotices();
        for (Notice n : notices) {
            model.addRow(new Object[]{n.getNoticeId(), n.getTitle(), n.getContent(), n.getTargetAudience(), n.getPostedDate()});
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTimetable() {
        contentPanel.removeAll();
        String[] columns = {"Timetable ID", "Course", "Dept", "Type", "Day", "Start", "End", "Venue"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        AdminTimetableDAO dao = new AdminTimetableDAO();
        List<Timetable> timetables = dao.getAllTimetables();
        for (Timetable t : timetables) {
            model.addRow(new Object[]{
                    t.getTimetableId(), t.getCourseCode(), t.getDepartmentId(), t.getSessionType(),
                    t.getDayOfWeek(), t.getStartTime(), t.getEndTime(), t.getVenue()
            });
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
