package tg2082.gui;

import common.auth.LoginFrame;
import common.dao.NoticeDAO;
import common.model.Course;
import common.model.Medical;
import common.model.Notice;
import common.model.Result;
import common.model.Timetable;
import common.util.UITheme;
import common.util.UserSession;
import tg2082.dao.UndergraduateDAO;
import tg2082.model.Undergraduate;
import tg2088.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/** ProfilePanel owns the UI and actions for this student portal section. */
final class ProfilePanel {
    private ProfilePanel() {}

    static JPanel createProfilePanel(UndergraduateDashboard d) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("My Student Profile (FR-AUTH-04)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Registration No:"), gbc);
        d.lblProfRegNo = new JLabel(d.student != null ? d.student.getRegNo() : "");
        d.lblProfRegNo.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(d.lblProfRegNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Academic Department:"), gbc);
        d.lblProfDept = new JLabel(d.student != null && d.student.getDepartmentName() != null ? d.student.getDepartmentName() : "DICT");
        d.lblProfDept.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(d.lblProfDept, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Batch Year:"), gbc);
        d.lblProfBatch = new JLabel(d.student != null ? String.valueOf(d.student.getBatchYear()) : "2024");
        d.lblProfBatch.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 3;
        card.add(d.lblProfBatch, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("Academic Status:"), gbc);
        d.lblProfStatus = new JLabel(d.student != null ? d.student.getStatus() : "ACTIVE");
        d.lblProfStatus.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 1; gbc.gridy = 4;
        card.add(d.lblProfStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Contact Number:"), gbc);
        d.txtProfPhone = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 5;
        card.add(d.txtProfPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        card.add(new JLabel("Email Address:"), gbc);
        d.txtProfEmail = UITheme.createTextField(25);
        gbc.gridx = 1; gbc.gridy = 6;
        card.add(d.txtProfEmail, gbc);

        d.lblProfilePicture = new JLabel("No profile photo selected", SwingConstants.CENTER);
        d.lblProfilePicture.setPreferredSize(new Dimension(160, 160));
        d.lblProfilePicture.setMinimumSize(new Dimension(160, 160));
        d.lblProfilePicture.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        gbc.gridx = 0; gbc.gridy = 7;
        card.add(new JLabel("Profile Picture:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        card.add(d.lblProfilePicture, gbc);

        JButton btnPicture = UITheme.createButton("Choose Profile Picture", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnPicture.addActionListener(e -> d.handleProfilePicture());
        gbc.gridx = 1; gbc.gridy = 8;
        card.add(btnPicture, gbc);

        JButton btnRemove = UITheme.createButton("Remove Profile Picture", new Color(185, 48, 48), Color.WHITE);
        btnRemove.addActionListener(e -> removeProfilePicture(d));
        gbc.gridx = 1; gbc.gridy = 9;
        card.add(btnRemove, gbc);

        JButton btnUpdate = UITheme.createButton("Update Contact Details", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnUpdate.addActionListener(e -> d.handleUpdateProfile());
        gbc.gridx = 1; gbc.gridy = 10;
        card.add(btnUpdate, gbc);

        return card;
    }

    static void loadProfileData(UndergraduateDashboard d) {
        if (d.student != null) {
            d.lblProfRegNo.setText(d.student.getRegNo());
            d.lblProfDept.setText(d.student.getDepartmentName() != null ? d.student.getDepartmentName() : "DICT");
            d.lblProfBatch.setText(String.valueOf(d.student.getBatchYear()));
            d.lblProfStatus.setText(d.student.getStatus());
            d.txtProfPhone.setText(d.student.getContactNo() != null ? d.student.getContactNo() : d.student.getPhoneNo());
            d.txtProfEmail.setText(d.student.getEmail());
            showProfilePicture(d);
        }
    }

    private static void showProfilePicture(UndergraduateDashboard d) {
        String path = d.student == null ? null : d.student.getProfilePic();
        if (path == null || path.isBlank()) {
            d.lblProfilePicture.setIcon(null);
            d.lblProfilePicture.setText("No profile photo selected");
            return;
        }
        java.nio.file.Path file = java.nio.file.Paths.get(path);
        if (!java.nio.file.Files.isRegularFile(file)) {
            d.lblProfilePicture.setIcon(null);
            d.lblProfilePicture.setText("Photo file not found");
            d.lblProfilePicture.setToolTipText(path);
            return;
        }
        ImageIcon original = new ImageIcon(file.toAbsolutePath().toString());
        if (original.getIconWidth() <= 0) {
            d.lblProfilePicture.setIcon(null);
            d.lblProfilePicture.setText("Cannot open photo");
            return;
        }
        Image resized = original.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        d.lblProfilePicture.setIcon(new ImageIcon(resized));
        d.lblProfilePicture.setText("");
        d.lblProfilePicture.setToolTipText(file.toString());
    }

    static void handleUpdateProfile(UndergraduateDashboard d) {
        String phone = d.txtProfPhone.getText().trim();
        String email = d.txtProfEmail.getText().trim();

        if (d.studentDAO.updateProfile(d.student.getUserId(), phone, email)) {
            d.student.setEmail(email);
            d.student.setContactNo(phone);
            d.student.setPhoneNo(phone);
            JOptionPane.showMessageDialog(d, "Contact details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static void handleProfilePicture(UndergraduateDashboard d) {
        if (d.student == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a profile picture");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(d) != JFileChooser.APPROVE_OPTION) return;
        java.io.File chosen = chooser.getSelectedFile();
        if (!chosen.isFile()) return;
        try {
            // Copy to the app's own folder so the saved path survives moving the original image.
            java.nio.file.Path folder = java.nio.file.Paths.get("profile_pictures");
            java.nio.file.Files.createDirectories(folder);
            String filename = chosen.getName().toLowerCase(java.util.Locale.ROOT);
            String ext = filename.substring(filename.lastIndexOf('.'));
            java.nio.file.Path target = folder.resolve("student_" + d.student.getUserId() + ext);
            java.nio.file.Files.copy(chosen.toPath(), target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            String saved = target.toString();
            if (d.studentDAO.updateProfilePicture(d.student.getUserId(), saved)) {
                d.student.setProfilePic(saved);
                showProfilePicture(d);
                JOptionPane.showMessageDialog(d, "Profile picture updated successfully!");
            } else {
                JOptionPane.showMessageDialog(d, "Could not save profile picture to database.");
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(d, "Cannot copy picture: " + ex.getMessage());
        }
    }
    private static void removeProfilePicture(UndergraduateDashboard d) {
        if (d.student == null) return;
        String oldPath = d.student.getProfilePic();
        if (oldPath == null || oldPath.isBlank()) {
            JOptionPane.showMessageDialog(d, "There is no profile picture to remove.");
            return;
        }
        if (JOptionPane.showConfirmDialog(d, "Remove your profile picture?", "Confirm removal",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        if (!d.studentDAO.updateProfilePicture(d.student.getUserId(), null)) {
            JOptionPane.showMessageDialog(d, "Could not remove profile picture from database.");
            return;
        }
        d.student.setProfilePic(null);
        showProfilePicture(d);
        // Delete only an image managed by this application, not an arbitrary user file.
        try {
            java.nio.file.Path managedFolder = java.nio.file.Paths.get("profile_pictures").toAbsolutePath().normalize();
            java.nio.file.Path oldFile = java.nio.file.Paths.get(oldPath).toAbsolutePath().normalize();
            if (oldFile.getParent() != null && oldFile.getParent().equals(managedFolder)
                    && oldFile.getFileName().toString().startsWith("student_" + d.student.getUserId() + ".")) {
                java.nio.file.Files.deleteIfExists(oldFile);
            }
        } catch (java.io.IOException ex) {
            // Database removal succeeded; stale managed image can be cleaned up later.
        }
        JOptionPane.showMessageDialog(d, "Profile picture removed successfully.");
    }

}
