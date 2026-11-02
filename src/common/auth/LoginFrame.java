package common.auth;

import common.exception.AccountLockedException;
import common.model.User;
import common.util.UITheme;
import common.util.UserSession;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * LoginFrame - Central Authentication Portal for FTMS.
 * Faithfully mirrors the design in the provided reference screenshot:
 * - Centered card on a clean, soft modern background
 * - Faculty of Technology crest / emblem
 * - Username & Password inputs with rounded borders
 * - Blue action login button
 * - Polymorphically launches the appropriate dashboard for the user role
 * - Enforces account lock after 5 consecutive failed attempts (FR-AUTH-05)
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthDAO authDAO;

    public LoginFrame() {
        authDAO = new AuthDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Faculty Management System - Login");
        setSize(960, 620);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel with subtle gentle curves/gradient
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft pastel background
                GradientPaint gp = new GradientPaint(0, 0, new Color(248, 250, 255), getWidth(), getHeight(), new Color(238, 242, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle background decorative circles
                g2.setColor(new Color(224, 231, 255, 120));
                g2.fillOval(-80, -80, 320, 320);
                g2.setColor(new Color(237, 233, 254, 100));
                g2.fillOval(getWidth() - 250, getHeight() - 250, 350, 350);

                g2.dispose();
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        // Center Login Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        card.add(createLogoLabel());

        card.add(Box.createVerticalStrut(12));

        // Title
        JLabel lblTitle = new JLabel("Faculty Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UITheme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);

        card.add(Box.createVerticalStrut(4));

        JLabel lblSub = new JLabel("Faculty of Technology - University of Ruhuna", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);

        card.add(Box.createVerticalStrut(25));

        // Username Field
        JPanel userWrap = new JPanel(new BorderLayout(5, 4));
        userWrap.setOpaque(false);
        userWrap.setMaximumSize(new Dimension(340, 60));
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);
        txtUsername = UITheme.createTextField(20);
        userWrap.add(lblUser, BorderLayout.NORTH);
        userWrap.add(txtUsername, BorderLayout.CENTER);
        card.add(userWrap);

        card.add(Box.createVerticalStrut(14));

        // Password Field
        JPanel passWrap = new JPanel(new BorderLayout(5, 4));
        passWrap.setOpaque(false);
        passWrap.setMaximumSize(new Dimension(340, 60));
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        txtPassword = UITheme.createPasswordField(20);
        passWrap.add(lblPass, BorderLayout.NORTH);
        passWrap.add(txtPassword, BorderLayout.CENTER);
        card.add(passWrap);

        card.add(Box.createVerticalStrut(22));

        // Login Button
        btnLogin = UITheme.createButton("LOGIN", new Color(37, 99, 235), Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(340, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());
        card.add(btnLogin);

        card.add(Box.createVerticalStrut(15));

        // Quick Demo Accounts Hint
        JLabel lblDemo = new JLabel("<html><center><font color='#94A3B8' size='2'>Demo Accounts (PW: 1234)<br>" +
                "Admin: <b>admin</b> | Lecturer: <b>wosada</b><br>" +
                "TO: <b>to_weera</b> | Student: <b>tg2088</b> / <b>tg2082</b></font></center></html>", SwingConstants.CENTER);
        lblDemo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblDemo);

        bgPanel.add(card);
        add(bgPanel);

        // Bind Enter key to login
        getRootPane().setDefaultButton(btnLogin);
    }

    private JLabel createLogoLabel() {
        BufferedImage logo = null;
        URL logoResource = getClass().getResource("/logo.jpg");
        if (logoResource == null) {
            logoResource = getClass().getResource("/resources/logo.jpg");
        }

        if (logoResource != null) {
            try {
                logo = ImageIO.read(logoResource);
            } catch (IOException ex) {
                System.err.println("Could not read login logo from " + logoResource + ": " + ex.getMessage());
            }
        }

        Path logoPath = Path.of("resources", "logo.jpg");
        if (logo == null && Files.isRegularFile(logoPath)) {
            try {
                logo = ImageIO.read(logoPath.toFile());
            } catch (IOException ex) {
                System.err.println("Could not read login logo from " + logoPath.toAbsolutePath() + ": " + ex.getMessage());
            }
        }

        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(80, 80));
        label.setMaximumSize(new Dimension(80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (logo != null) {
            Image scaledLogo = logo.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledLogo));
        } else {
            System.err.println("Login logo not found. Expected /logo.jpg or " + logoPath.toAbsolutePath());
            label.setText("FoT");
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setBackground(new Color(30, 58, 138));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        return label;
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = authDAO.authenticate(username, password);

            if (user != null) {
                UserSession.createSession(user);

                // Pure Runtime Polymorphism: user.openDashboard(this) automatically opens the correct dashboard!
                user.openDashboard(this);

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (AccountLockedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Account Locked", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
