package gui;

import dao.UserDAO;
import model.User;
import util.UserSession;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        setTitle("FTMS - Lecturer Login");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Lecturer System Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(lblTitle);

        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.add(new JLabel("Username: "), BorderLayout.WEST);
        txtUsername = new JTextField();
        userPanel.add(txtUsername, BorderLayout.CENTER);
        mainPanel.add(userPanel);

        JPanel passPanel = new JPanel(new BorderLayout(5, 5));
        passPanel.add(new JLabel("Password: "), BorderLayout.WEST);
        txtPassword = new JPasswordField();
        passPanel.add(txtPassword, BorderLayout.CENTER);
        mainPanel.add(passPanel);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(btnLogin);

        add(mainPanel);

        btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            // UserSession එක නිර්මාණය කිරීම
            UserSession.createSession(user);

            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);

            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}