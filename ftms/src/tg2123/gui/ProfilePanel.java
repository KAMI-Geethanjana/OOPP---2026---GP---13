
package tg2123.gui;

import tg2123.dao.TechnicalOfficerDAO;
import tg2123.model.TechnicalOfficer;
import common.util.UITheme;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class ProfilePanel extends JPanel {

    private final TechnicalOfficer officer;
    private final TechnicalOfficerDAO dao = new TechnicalOfficerDAO();

    private final JTextField nameField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(25);

    private final JLabel photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
    private File selectedPhoto;

    public ProfilePanel(TechnicalOfficer officer) {
        this.officer = officer;

        setLayout(new GridBagLayout());
        setBackground(UITheme.CONTENT_BG);

        nameField.setText(officer.getFullName());
        emailField.setText(officer.getEmail());
        phoneField.setText(officer.getPhoneNo());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Profile photo
        photoLabel.setPreferredSize(new Dimension(130, 130));
        photoLabel.setBorder(new LineBorder(Color.GRAY));
        loadCurrentPhoto();

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(photoLabel, gbc);

        JButton choosePhotoButton = new JButton("Choose Photo");
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(choosePhotoButton, gbc);

        choosePhotoButton.addActionListener(e -> choosePhoto());

        // Profile details
        addField("Full Name:", nameField, gbc, 2);
        addField("Email:", emailField, gbc, 3);
        addField("Phone Number:", phoneField, gbc, 4);

        JButton saveButton = new JButton("Save Profile");
        gbc.gridx = 1;
        gbc.gridy = 5;
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

    private void choosePhoto() {

        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter =
                new FileNameExtensionFilter(
                        "Image Files (JPG, JPEG, PNG)",
                        "jpg", "jpeg", "png");

        chooser.setFileFilter(filter);

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            try {
                BufferedImage image = ImageIO.read(file);

                if (image == null) {
                    JOptionPane.showMessageDialog(
                            this, "Please select a valid image.");
                    return;
                }

                selectedPhoto = file;
                displayPhoto(image);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this, "Could not read the selected image.");
            }
        }
    }

    private void displayPhoto(BufferedImage image) {

        Image scaledImage = image.getScaledInstance(
                120, 120, Image.SCALE_SMOOTH);

        photoLabel.setText("");
        photoLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void loadCurrentPhoto() {

        String path = officer.getProfilePic();

        if (path == null || path.trim().isEmpty()) {
            return;
        }

        File file = new File(path);

        if (!file.isFile()) {
            return;
        }

        try {
            BufferedImage image = ImageIO.read(file);

            if (image != null) {
                displayPhoto(image);
            }

        } catch (IOException ex) {
            System.err.println("Could not load profile photo: "
                    + ex.getMessage());
        }
    }

    private String saveSelectedPhoto() throws IOException {

        if (selectedPhoto == null) {
            return null;
        }

        String fileName = selectedPhoto.getName();
        String extension = fileName.substring(
                fileName.lastIndexOf('.') + 1).toLowerCase();

        if (!extension.equals("jpg")
                && !extension.equals("jpeg")
                && !extension.equals("png")) {
            throw new IOException("Unsupported image format.");
        }

        File folder = new File("resources/profile_photos");

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create photo folder.");
        }

        String newFileName = "to_" + officer.getUserId()
                + "_" + System.currentTimeMillis()
                + "." + extension;

        File destination = new File(folder, newFileName);

        Files.copy(
                selectedPhoto.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return "resources/profile_photos/" + newFileName;
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

            // Save the selected photo if there is one.
            if (selectedPhoto != null) {

                String photoPath = saveSelectedPhoto();

                boolean photoUpdated = dao.updateProfilePhoto(
                        officer.getUserId(), photoPath);

                if (!photoUpdated) {
                    JOptionPane.showMessageDialog(
                            this, "Could not update profile photo.");
                    return;
                }

                officer.setProfilePic(photoPath);
                selectedPhoto = null;
            }

            // Save other profile details.
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
                        this, "No profile details were updated.");
            }

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not save photo: " + ex.getMessage(),
                    "Photo Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update profile: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
