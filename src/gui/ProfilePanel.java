
package gui;

import dao.TechnicalOfficerDAO;
import model.TechnicalOfficer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProfilePanel extends JPanel {

    private final TechnicalOfficer officer;
    private final TechnicalOfficerDAO dao = new TechnicalOfficerDAO();

    private final JTextField nameField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(25);

    public ProfilePanel(TechnicalOfficer officer) {
        this.officer = officer;

        setLayout(new GridBagLayout());

        nameField.setText(officer.getFullName());
        emailField.setText(officer.getEmail());
        phoneField.setText(officer.getPhoneNo());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addField("Full Name:", nameField, gbc, 0);
        addField("Email:", emailField, gbc, 1);
        addField("Phone Number:", phoneField, gbc, 2);

        JButton saveButton = new JButton("Save Profile");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(saveButton, gbc);

        saveButton.addActionListener(e -> saveProfile());
    }

    private void addField(String label, JTextField field,
                          GridBagConstraints gbc, int row) {

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        add(field, gbc);
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please complete all profile fields.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid email address.");
            return;
        }

        try {
            boolean updated = dao.updateProfile(
                    officer.getUserId(), name, email, phone);

            if (updated) {
                officer.setFullName(name);
                officer.setEmail(email);
                officer.setPhoneNo(phone);

                JOptionPane.showMessageDialog(
                        this, "Profile updated successfully.");
            } else {
                JOptionPane.showMessageDialog(
                        this, "No profile was updated.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update profile: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
