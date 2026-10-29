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

        d.lblProfilePicture = new JLabel("No profile photo selected");
        gbc.gridx = 0; gbc.gridy = 7;
        card.add(new JLabel("Profile Picture:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        card.add(d.lblProfilePicture, gbc);

        JButton btnPicture = UITheme.createButton("Choose Profile Picture", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnPicture.addActionListener(e -> d.handleProfilePicture());
        gbc.gridx = 1; gbc.gridy = 8;
        card.add(btnPicture, gbc);

        JButton btnUpdate = UITheme.createButton("Update Contact Details", UITheme.PRIMARY_BLUE, Color.WHITE);
        btnUpdate.addActionListener(e -> d.handleUpdateProfile());
        gbc.gridx = 1; gbc.gridy = 9;
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
            d.lblProfilePicture.setText(d.student.getProfilePic() == null ? "No profile photo selected" : d.student.getProfilePic());
        }
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
                d.lblProfilePicture.setText(saved);
                JOptionPane.showMessageDialog(d, "Profile picture updated successfully!");
            } else {
                JOptionPane.showMessageDialog(d, "Could not save profile picture to database.");
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(d, "Cannot copy picture: " + ex.getMessage());
        }
    }
}
