package gui;

import config.DBConn;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * LoginUI - Faculty Management System (FTMS)
 * Faculty of Technology - University of Ruhuna
 *
 * Implements modern card-based Login UI matching system specifications.
 */
public class LoginUI extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginUI() {
        setTitle("Faculty Management System - Login");
        setSize(460, 560);
        setMinimumSize(new Dimension(420, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        // Main Background Panel
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle modern gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 252),
                        getWidth(), getHeight(), new Color(238, 242, 250));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        backgroundPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(backgroundPanel);

        // Center White Card
        RoundedCardPanel card = new RoundedCardPanel(24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 32, 32, 32));
        card.setPreferredSize(new Dimension(360, 460));

        // 1. Logo
        JLabel lblLogo = createLogoLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Titles
        JLabel lblTitle = new JLabel("Faculty Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 43, 73)); // Dark navy
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Faculty of Technology - University of Ruhuna");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(115, 128, 145)); // Slate gray
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Username Field
        txtUsername = new PlaceholderTextField("Username");
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Password Field
        txtPassword = new PlaceholderPasswordField("Password");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. Login Button
        btnLogin = new RoundedButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(33, 102, 234)); // Vibrant royal blue
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 6. Status / Error Message Label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(220, 38, 38));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Listeners
        btnLogin.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> performLogin());

        // Assembly
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(14));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(26));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(14));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);

        backgroundPanel.add(card);
    }

    private JLabel createLogoLabel() {
        int logoSize = 82;
        BufferedImage logoImg = null;

        // Try loading from project resources
        String[] possiblePaths = {
                "src/resources/logo.jpg",
                "OOPP---2026---GP---13/src/resources/logo.jpg",
                "resources/logo.jpg",
                "src/resources/logo.png"
        };

        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                try {
                    logoImg = ImageIO.read(f);
                    break;
                } catch (Exception ignored) {}
            }
        }

        if (logoImg != null) {
            Image scaled = logoImg.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }

        // Elegant Dynamic Vector Emblem Fallback
        return new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = x + logoSize / 2;
                int cy = y + logoSize / 2;
                int r = logoSize / 2 - 4;

                // Cogwheel gear outer teeth
                g2.setColor(new Color(139, 21, 27)); // Maroon Ruhuna red
                for (int i = 0; i < 16; i++) {
                    double angle = Math.toRadians(i * (360.0 / 16));
                    int gx = cx + (int) ((r - 2) * Math.cos(angle));
                    int gy = cy + (int) ((r - 2) * Math.sin(angle));
                    g2.fillOval(gx - 4, gy - 4, 8, 8);
                }
                g2.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);

                // Inner white badge
                g2.setColor(Color.WHITE);
                g2.fillOval(cx - r + 7, cy - r + 7, (r - 7) * 2, (r - 7) * 2);

                // Blue inner ring
                g2.setColor(new Color(14, 116, 144));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - r + 10, cy - r + 10, (r - 10) * 2, (r - 10) * 2);

                // Radio Transmission Tower & Waves
                g2.setColor(new Color(15, 76, 129));
                g2.setStroke(new BasicStroke(2f));
                // Tower legs
                g2.drawLine(cx, cy - 14, cx - 9, cy + 12);
                g2.drawLine(cx, cy - 14, cx + 9, cy + 12);
                g2.drawLine(cx - 5, cy - 1, cx + 5, cy - 1);
                g2.drawLine(cx - 7, cy + 6, cx + 7, cy + 6);
                g2.fillOval(cx - 3, cy - 17, 6, 6);

                // Signal waves
                g2.drawArc(cx - 15, cy - 16, 30, 20, 45, 90);
                g2.drawArc(cx - 19, cy - 20, 38, 28, 45, 90);

                // Green Laurel leaves
                g2.setColor(new Color(34, 139, 34));
                g2.fillOval(cx - 14, cy + 12, 10, 5);
                g2.fillOval(cx + 4, cy + 12, 10, 5);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return logoSize; }
            @Override
            public int getIconHeight() { return logoSize; }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        lblStatus.setText(" ");

        if (username.isEmpty() || username.equals("Username")) {
            showError("Please enter your username.");
            txtUsername.requestFocusInWindow();
            return;
        }

        if (password.isEmpty() || password.equals("Password")) {
            showError("Please enter your password.");
            txtPassword.requestFocusInWindow();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("LOGGING IN...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String role = "";
            String errorMsg = "";

            @Override
            protected Boolean doInBackground() {
                try (Connection conn = DBConn.getConn()) {
                    String sql = "SELECT user_id, username, password_hash, role, is_active, failed_logins " +
                                 "FROM users WHERE username = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                boolean isActive = rs.getBoolean("is_active");
                                int failedLogins = rs.getInt("failed_logins");
                                String dbPass = rs.getString("password_hash");
                                role = rs.getString("role");

                                if (!isActive || failedLogins >= 3) {
                                    errorMsg = "Account is locked or deactivated. Contact Admin.";
                                    return false;
                                }

                                if (dbPass != null && dbPass.equals(password)) {
                                    // Reset failed logins on success
                                    String resetSql = "UPDATE users SET failed_logins = 0 WHERE username = ?";
                                    try (PreparedStatement psReset = conn.prepareStatement(resetSql)) {
                                        psReset.setString(1, username);
                                        psReset.executeUpdate();
                                    }
                                    return true;
                                } else {
                                    // Increment failed logins
                                    String incSql = "UPDATE users SET failed_logins = failed_logins + 1 WHERE username = ?";
                                    try (PreparedStatement psInc = conn.prepareStatement(incSql)) {
                                        psInc.setString(1, username);
                                        psInc.executeUpdate();
                                    }
                                    errorMsg = "Invalid username or password.";
                                    return false;
                                }
                            } else {
                                errorMsg = "User account not found.";
                                return false;
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Fallback for development if DB is not currently connected
                    if ("admin".equalsIgnoreCase(username) || "TG2086".equalsIgnoreCase(username)) {
                        role = "ADMIN";
                        return true;
                    }
                    errorMsg = "Database Connection Error: " + ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
                try {
                    boolean success = get();
                    if (success) {
                        dispose(); // Close login window
                        // Open Admin Dashboard
                        SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
                    } else {
                        showError(errorMsg.isEmpty() ? "Authentication failed." : errorMsg);
                    }
                } catch (Exception ex) {
                    showError("Login error: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
    }

    // ==========================================
    // Custom Modern UI Components
    // ==========================================

    private static class RoundedCardPanel extends JPanel {
        private final int radius;

        public RoundedCardPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Soft Drop Shadow
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 3, width - 4, height - 3, radius, radius);
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(1, 1, width - 2, height - 2, radius, radius);

            // Card Body
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, width - 2, height - 2, radius, radius);

            // Thin border
            g2.setColor(new Color(228, 233, 242));
            g2.drawRoundRect(0, 0, width - 2, height - 2, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PlaceholderTextField extends JTextField implements FocusListener {
        private final String placeholder;
        private boolean isShowingPlaceholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            this.isShowingPlaceholder = true;
            setText(placeholder);
            setForeground(new Color(156, 163, 175)); // Placeholder gray
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isShowingPlaceholder) {
                setText("");
                setForeground(new Color(30, 41, 59));
                isShowingPlaceholder = false;
            }
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (getText().trim().isEmpty()) {
                setText(placeholder);
                setForeground(new Color(156, 163, 175));
                isShowingPlaceholder = true;
            }
            repaint();
        }

        @Override
        public String getText() {
            return isShowingPlaceholder ? "" : super.getText();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

            // Border
            if (hasFocus()) {
                g2.setColor(new Color(37, 99, 235)); // Focused blue
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(new Color(203, 213, 225)); // Border gray
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PlaceholderPasswordField extends JPasswordField implements FocusListener {
        private final String placeholder;
        private boolean isShowingPlaceholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            this.isShowingPlaceholder = true;
            setEchoChar((char) 0);
            setText(placeholder);
            setForeground(new Color(156, 163, 175));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isShowingPlaceholder) {
                setText("");
                setEchoChar('•');
                setForeground(new Color(30, 41, 59));
                isShowingPlaceholder = false;
            }
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (new String(getPassword()).trim().isEmpty()) {
                setEchoChar((char) 0);
                setText(placeholder);
                setForeground(new Color(156, 163, 175));
                isShowingPlaceholder = true;
            }
            repaint();
        }

        @Override
        public char[] getPassword() {
            return isShowingPlaceholder ? new char[0] : super.getPassword();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

            // Border
            if (hasFocus()) {
                g2.setColor(new Color(37, 99, 235));
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private boolean hover = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);

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

            Color baseColor = getBackground();
            if (hover) {
                baseColor = new Color(26, 86, 209); // Darker blue on hover
            }
            if (getModel().isPressed()) {
                baseColor = new Color(20, 68, 168);
            }

            g2.setColor(baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
