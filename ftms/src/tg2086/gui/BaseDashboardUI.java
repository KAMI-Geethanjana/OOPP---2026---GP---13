package tg2086.gui;

import common.auth.LoginFrame;
import tg2086.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseDashboardUI - Reusable Master Template for FTMS Role Dashboards.
 *
 * Provides the unified visual design:
 * - Deep navy sidebar with branded header, active pill highlight, and Sign Out button.
 * - Top header with personalized welcome greeting and profile avatar.
 * - CardLayout center panel hosting rounded white content cards.
 * - Standardized helper methods for content cards, form rows, button toolbars, and alerts.
 *
 * Authored under: Module TG2086 (K.A.M.I. Geethanjana)
 */
public abstract class BaseDashboardUI extends JFrame {

    // Sidebar & Navigation
    private final JPanel sidebarPanel;
    private final JPanel navButtonsContainer;
    private final Map<String, NavButton> navButtonMap = new HashMap<>();
    private String activeTabKey = null;

    // Header Labels & Accessories
    private final JLabel lblWelcome;
    private final JLabel lblSubtitle;
    private final JPanel headerRightContainer;

    // Content Panel with CardLayout
    private final JPanel centerContainer;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    /**
     * Initializes the Master Dashboard Frame.
     *
     * @param windowTitle  Title of the window (e.g. "Admin Dashboard")
     * @param badgeText    Badge tag text (e.g. "FTMS")
     * @param portalTitle  Portal name in sidebar header (e.g. "Admin Panel")
     * @param welcomeUser  User name for greeting (e.g. "K.A.M.I. Geethanjana")
     * @param userSubtitle Subtitle under greeting (e.g. "System Administrator | TG/2024/2086")
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

        // Top Header Bar
        JPanel topHeader = new JPanel(new BorderLayout(15, 0));
        topHeader.setBackground(Color.WHITE);
        topHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
                new EmptyBorder(18, 32, 18, 32)
        ));

        // Welcome titles
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        lblWelcome = new JLabel("Welcome back, " + (welcomeUser != null ? welcomeUser : "User") + " 👋");
        lblWelcome.setFont(UITheme.FONT_PAGE_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        lblSubtitle = new JLabel(userSubtitle != null ? userSubtitle : "Faculty Management System");
        lblSubtitle.setFont(UITheme.FONT_PAGE_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(lblWelcome);
        titleBox.add(lblSubtitle);
        topHeader.add(titleBox, BorderLayout.WEST);

        // Header Right Container (For Accessory components like Live Clock + Avatar)
        headerRightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        headerRightContainer.setOpaque(false);

        // User Avatar Circle
        JLabel avatar = new JLabel(getInitials(welcomeUser), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        headerRightContainer.add(avatar);
        topHeader.add(headerRightContainer, BorderLayout.EAST);

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

        // Optional Pill Badge (e.g., "FTMS")
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
            badge.setPreferredSize(new Dimension(46, 26));
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

    public void addNavItem(String key, String displayName, JPanel contentPanel) {
        cardPanel.add(contentPanel, key);

        NavButton btn = new NavButton(displayName);
        btn.addActionListener(e -> setActiveTab(key));

        navButtonMap.put(key, btn);
        navButtonsContainer.add(btn);
        navButtonsContainer.add(Box.createVerticalStrut(6));

        if (activeTabKey == null) {
            setActiveTab(key);
        }

        navButtonsContainer.revalidate();
        navButtonsContainer.repaint();
    }

    public void setActiveTab(String key) {
        if (!navButtonMap.containsKey(key)) return;

        if (activeTabKey != null && navButtonMap.containsKey(activeTabKey)) {
            navButtonMap.get(activeTabKey).setActive(false);
        }

        activeTabKey = key;
        navButtonMap.get(key).setActive(true);
        cardLayout.show(cardPanel, key);
        onTabChanged(key);
    }

    protected void onTabChanged(String activeTabKey) {}

    public void setHeaderGreeting(String welcome, String subtitle) {
        if (welcome != null) lblWelcome.setText(welcome);
        if (subtitle != null) lblSubtitle.setText(subtitle);
    }

    public void addHeaderAccessory(JComponent comp) {
        if (headerRightContainer != null && comp != null) {
            headerRightContainer.add(comp, 0);
            headerRightContainer.revalidate();
            headerRightContainer.repaint();
        }
    }

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
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // =========================================================================
    // REUSABLE UI BUILDER HELPERS FOR MODULE IMPLEMENTERS
    // =========================================================================

    public static JPanel createSectionCard(String cardTitle, JComponent content) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 14));

        if (cardTitle != null && !cardTitle.trim().isEmpty()) {
            JLabel lblTitle = new JLabel(cardTitle);
            lblTitle.setFont(UITheme.FONT_CARD_TITLE);
            lblTitle.setForeground(UITheme.TEXT_DARK);
            lblTitle.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
                    new EmptyBorder(0, 0, 10, 0)
            ));
            card.add(lblTitle, BorderLayout.NORTH);
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static JPanel createFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_FORM_LABEL);
        label.setForeground(UITheme.TEXT_DARK);

        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }

    public static JPanel createSearchBar(JTextField searchField, JButton searchButton, JButton resetButton) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        searchField.setPreferredSize(new Dimension(280, 36));
        bar.add(searchField);
        bar.add(searchButton);
        if (resetButton != null) {
            bar.add(resetButton);
        }
        return bar;
    }

    private static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    // =========================================================================
    // SIDEBAR NAV BUTTON COMPONENT
    // =========================================================================

    private static class NavButton extends JButton {
        private boolean isActive = false;
        private boolean isHover = false;

        public NavButton(String text) {
            super(text);
            setFont(UITheme.FONT_SIDEBAR_ITEM);
            setForeground(UITheme.SIDEBAR_TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

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
            setFont(active ? UITheme.FONT_SIDEBAR_ACTIVE : UITheme.FONT_SIDEBAR_ITEM);
            setForeground(active ? UITheme.SIDEBAR_ACTIVE_FG : UITheme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(UITheme.SIDEBAR_ACTIVE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else if (isHover) {
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
