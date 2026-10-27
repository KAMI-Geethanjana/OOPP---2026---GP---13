package gui;

import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseDashboardUI - Reusable Master Template for all FTMS Role Dashboards
 * (Admin, Student, Lecturer, Technical Officer).
 *
 * Provides the unified visual design:
 * - Deep navy sidebar with branded header, active pill highlight, and Sign Out button.
 * - Top header with personalized welcome greeting and profile avatar placeholder.
 * - CardLayout center panel hosting rounded white content cards.
 * - Standardized helper methods for content cards, form rows, button toolbars, and alerts.
 */
public abstract class BaseDashboardUI extends JFrame {

    // Sidebar & Navigation
    private final JPanel sidebarPanel;
    private final JPanel navButtonsContainer;
    private final Map<String, NavButton> navButtonMap = new HashMap<>();
    private String activeTabKey = null;

    // Header Labels
    private final JLabel lblWelcome;
    private final JLabel lblSubtitle;

    // Content Panel with CardLayout
    private final JPanel centerContainer;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    /**
     * Initializes the Master Dashboard Frame.
     *
     * @param windowTitle  Title of the window (e.g. "Admin Dashboard", "Student Portal")
     * @param badgeText    Badge tag text (e.g. "LMS", or null if no badge)
     * @param portalTitle  Portal name in sidebar header (e.g. "Admin Panel", "STUDENT PORTAL")
     * @param welcomeUser  User name for greeting (e.g. "System", "Kasun")
     * @param userSubtitle Subtitle under greeting (e.g. "LMS Administration Portal", "TG/2023/1756")
     */
    public BaseDashboardUI(String windowTitle, String badgeText, String portalTitle,
                           String welcomeUser, String userSubtitle) {
        super(windowTitle);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Overall layout
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_LIGHT);
        setContentPane(rootPanel);

        // ---------------------------------------------------------------------
        // 1. LEFT SIDEBAR PANEL
        // ---------------------------------------------------------------------
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBackground(UITheme.SIDEBAR_BG);

        // Sidebar Header (Brand & Title)
        JPanel sidebarHeader = createSidebarHeader(badgeText, portalTitle);
        sidebarPanel.add(sidebarHeader, BorderLayout.NORTH);

        // Sidebar Navigation Buttons Container (Scrollable if many tabs)
        navButtonsContainer = new JPanel();
        navButtonsContainer.setLayout(new BoxLayout(navButtonsContainer, BoxLayout.Y_AXIS));
        navButtonsContainer.setBackground(UITheme.SIDEBAR_BG);
        navButtonsContainer.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane navScrollPane = new JScrollPane(navButtonsContainer);
        navScrollPane.setBorder(null);
        navScrollPane.setBackground(UITheme.SIDEBAR_BG);
        navScrollPane.getViewport().setBackground(UITheme.SIDEBAR_BG);
        navScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        navScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarPanel.add(navScrollPane, BorderLayout.CENTER);

        // Sidebar Footer (Sign Out)
        JPanel sidebarFooter = createSidebarFooter();
        sidebarPanel.add(sidebarFooter, BorderLayout.SOUTH);

        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // ---------------------------------------------------------------------
        // 2. RIGHT WORKSPACE (Header + Dynamic Content Card)
        // ---------------------------------------------------------------------
        centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(UITheme.BG_LIGHT);

        // Top Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);
        topHeader.setBorder(new EmptyBorder(22, 32, 16, 32));

        JPanel welcomeBox = new JPanel();
        welcomeBox.setOpaque(false);
        welcomeBox.setLayout(new BoxLayout(welcomeBox, BoxLayout.Y_AXIS));

        lblWelcome = new JLabel("Welcome, " + (welcomeUser != null ? welcomeUser : "User"));
        lblWelcome.setFont(UITheme.FONT_TITLE_LARGE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        lblSubtitle = new JLabel(userSubtitle != null ? userSubtitle : "Faculty Technical Management System");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        welcomeBox.add(lblWelcome);
        welcomeBox.add(Box.createVerticalStrut(3));
        welcomeBox.add(lblSubtitle);

        topHeader.add(welcomeBox, BorderLayout.WEST);

        // Circular Profile Avatar placeholder
        topHeader.add(UITheme.createAvatarPlaceholder(40), BorderLayout.EAST);

        centerContainer.add(topHeader, BorderLayout.NORTH);

        // Card Panel holding the different section views
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(0, 32, 24, 32));

        centerContainer.add(cardPanel, BorderLayout.CENTER);
        rootPanel.add(centerContainer, BorderLayout.CENTER);
    }

    // =========================================================================
    // SIDEBAR BUILDERS
    // =========================================================================

    private JPanel createSidebarHeader(String badgeText, String portalTitle) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(26, 20, 20, 20));

        // Optional Pill Badge (e.g., "LMS")
        if (badgeText != null && !badgeText.trim().isEmpty()) {
            JLabel badge = new JLabel(badgeText.trim(), SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.SIDEBAR_BADGE_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setForeground(Color.WHITE);
            badge.setPreferredSize(new Dimension(36, 26));
            badge.setOpaque(false);
            header.add(badge);
        }

        // Title Label
        JLabel title = new JLabel(portalTitle != null ? portalTitle : "FTMS Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        header.add(title);

        return header;
    }

    private JPanel createSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 16, 20, 16));

        JButton btnSignOut = new JButton("Sign Out") {
            private boolean hover = false;
            {
                setFont(UITheme.FONT_BUTTON);
                setForeground(UITheme.SIGNOUT_FG);
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(10, 0, 10, 0));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(hover ? new Color(60, 24, 38) : UITheme.SIGNOUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(UITheme.SIGNOUT_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnSignOut.addActionListener(e -> onSignOut());
        footer.add(btnSignOut, BorderLayout.CENTER);
        return footer;
    }

    // =========================================================================
    // NAVIGATION TAB MANAGEMENT
    // =========================================================================

    /**
     * Registers a new navigation tab and its corresponding view panel.
     * Automatically sets up the modern active pill highlighting.
     *
     * @param key          Unique key identifier (e.g. "notices", "timetables", "students")
     * @param displayName  Label displayed on the sidebar menu button
     * @param contentPanel Panel component displayed in the main card area
     */
    public void addNavItem(String key, String displayName, JPanel contentPanel) {
        cardPanel.add(contentPanel, key);

        NavButton btn = new NavButton(displayName);
        btn.addActionListener(e -> setActiveTab(key));

        navButtonMap.put(key, btn);
        navButtonsContainer.add(btn);
        navButtonsContainer.add(Box.createVerticalStrut(6));

        // If this is the very first tab added, set it as active by default
        if (activeTabKey == null) {
            setActiveTab(key);
        }

        navButtonsContainer.revalidate();
        navButtonsContainer.repaint();
    }

    /**
     * Programmatically switches the active navigation tab.
     *
     * @param key The key of the tab to activate
     */
    public void setActiveTab(String key) {
        if (!navButtonMap.containsKey(key)) return;

        // Reset previous active tab
        if (activeTabKey != null && navButtonMap.containsKey(activeTabKey)) {
            navButtonMap.get(activeTabKey).setActive(false);
        }

        // Set new active tab
        activeTabKey = key;
        navButtonMap.get(key).setActive(true);
        cardLayout.show(cardPanel, key);

        // Notify subclass hook
        onTabSelected(key);
    }

    /**
     * Hook method invoked whenever the active tab changes.
     * Subclasses can override this to refresh table data on demand.
     *
     * @param tabKey The active tab key
     */
    protected void onTabSelected(String tabKey) {
        // Optional override in subclass
    }

    /**
     * Updates the welcome user label and subtitle.
     */
    public void setWelcome(String username, String subtitle) {
        if (username != null) lblWelcome.setText("Welcome, " + username);
        if (subtitle != null) lblSubtitle.setText(subtitle);
    }

    /**
     * Default sign-out handler with confirmation dialog.
     * Closes the dashboard and reopens the login window.
     */
    protected void onSignOut() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out from the system?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        }
    }

    // =========================================================================
    // CONTENT CARD & UI BUILDER HELPERS
    // =========================================================================

    /**
     * Creates a standardized white rounded content card container
     * matching the layout of the reference screenshots.
     *
     * @param title      Section/Panel title (e.g. "Notice Management")
     * @param bodyPanel  The main content inside the card (e.g. table, forms)
     * @return Formatted RoundedCardPanel ready to be registered via addNavItem
     */
    public static UITheme.RoundedCardPanel createContentCard(String title, JComponent bodyPanel) {
        UITheme.RoundedCardPanel card = new UITheme.RoundedCardPanel(16);
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        if (title != null && !title.trim().isEmpty()) {
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(UITheme.FONT_CARD_HEADER);
            lblTitle.setForeground(UITheme.TEXT_DARK);
            lblTitle.setBorder(new EmptyBorder(0, 0, 16, 0));
            card.add(lblTitle, BorderLayout.NORTH);
        }

        if (bodyPanel != null) {
            card.add(bodyPanel, BorderLayout.CENTER);
        }

        return card;
    }

    /**
     * Creates a standardized action buttons bar (right-aligned or left-aligned).
     */
    public static JPanel createButtonBar(boolean rightAligned, JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(rightAligned ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(12, 0, 0, 0));
        for (JButton b : buttons) {
            if (b != null) bar.add(b);
        }
        return bar;
    }

    /**
     * Creates a standardized search row with search input and blue search button.
     */
    public static JPanel createSearchBar(UITheme.ModernTextField txtSearch, JButton btnSearch) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));
        txtSearch.setPreferredSize(new Dimension(280, 36));
        panel.add(txtSearch);
        panel.add(btnSearch);
        return panel;
    }

    // =========================================================================
    // ALERT DIALOG HELPERS
    // =========================================================================

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean showConfirm(String message, String title) {
        int res = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    // =========================================================================
    // CUSTOM SIDEBAR NAVIGATION BUTTON
    // =========================================================================

    private static class NavButton extends JButton {
        private boolean isActive = false;
        private boolean isHover = false;

        public NavButton(String text) {
            super(text);
            setFont(UITheme.FONT_NAV);
            setForeground(UITheme.SIDEBAR_TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setFont(active ? UITheme.FONT_NAV_ACTIVE : UITheme.FONT_NAV);
            setForeground(active ? UITheme.SIDEBAR_ACTIVE_FG : UITheme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                // Vibrant Purple/Indigo Pill Highlight (#6366F1)
                g2.setColor(UITheme.SIDEBAR_ACTIVE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else if (isHover) {
                // Subtle dark slate hover highlight
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
