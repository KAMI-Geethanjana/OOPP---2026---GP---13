
package tg2082.gui;

import common.model.Course;
import common.model.Medical;
import common.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class MedicalPanel {

    private static int editingMedicalId = -1;
    private static String editingOriginalPath = null;

    // Store checkbox status separately
    private static final Map<String, Boolean> courseChecks =
            new HashMap<>();

    private MedicalPanel() {}

    static JPanel createMedicalPanel(UndergraduateDashboard d) {

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 15));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Medical Requests");
        title.setFont(UITheme.FONT_HEADER);

        JLabel subtitle = new JLabel(
                "Submit medical certificates and track their status."
        );
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);

        card.add(header, BorderLayout.NORTH);

        // Center
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        // Form
        JPanel form = new JPanel(new BorderLayout(10, 8));
        form.setBackground(new Color(248, 250, 252));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel formTitle = new JLabel("Medical Submission");
        formTitle.setFont(UITheme.FONT_BODY_BOLD);
        form.add(formTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        // Course checkbox list
        JPanel courseRow = new JPanel(new BorderLayout(10, 5));
        courseRow.setOpaque(false);

        courseRow.add(
                new JLabel("Select enrolled courses:"),
                BorderLayout.NORTH
        );

        d.medCourseListModel = new DefaultListModel<>();
        d.listMedCourses = new JList<>(d.medCourseListModel);

        d.listMedCourses.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        d.listMedCourses.setCellRenderer(new CheckBoxRenderer());

        d.listMedCourses.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {

                        int index =
                                d.listMedCourses.locationToIndex(e.getPoint());

                        if (index >= 0) {

                            Rectangle bounds =
                                    d.listMedCourses.getCellBounds(index, index);

                            if (bounds != null && bounds.contains(e.getPoint())) {

                                String course =
                                        d.medCourseListModel.getElementAt(index);

                                toggleCourse(course);

                                d.listMedCourses.repaint();
                            }
                        }
                    }
                });

        JScrollPane courseScroll = new JScrollPane(d.listMedCourses);
        courseScroll.setPreferredSize(new Dimension(300, 120));

        courseRow.add(courseScroll, BorderLayout.CENTER);
        courseRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        courseRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(courseRow);

        // Date
        JPanel dateRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5)
        );
        dateRow.setOpaque(false);

        dateRow.add(new JLabel("Medical Date (YYYY-MM-DD):"));

        d.txtMedDate = UITheme.createTextField(12);
        d.txtMedDate.setText(
                new Date(System.currentTimeMillis()).toString()
        );

        dateRow.add(d.txtMedDate);
        dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(dateRow);

        // Reason
        d.txtMedReason = new JTextArea(3, 30);
        d.txtMedReason.setFont(UITheme.FONT_BODY);
        d.txtMedReason.setLineWrap(true);
        d.txtMedReason.setWrapStyleWord(true);
        d.txtMedReason.setBorder(
                BorderFactory.createTitledBorder("Medical Reason")
        );

        JScrollPane reasonScroll = new JScrollPane(d.txtMedReason);
        reasonScroll.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 85)
        );
        reasonScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(reasonScroll);

        // Certificate
        JPanel fileRow = new JPanel(new BorderLayout(8, 0));
        fileRow.setOpaque(false);

        d.txtMedPath = UITheme.createTextField(20);
        d.txtMedPath.setEditable(false);

        JButton chooseFile = UITheme.createButton(
                "Choose File", UITheme.PRIMARY_BLUE, Color.WHITE
        );

        chooseFile.addActionListener(e -> chooseCertificate(d));

        fileRow.add(new JLabel("Certificate:"), BorderLayout.WEST);
        fileRow.add(d.txtMedPath, BorderLayout.CENTER);
        fileRow.add(chooseFile, BorderLayout.EAST);

        fileRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fileRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(fileRow);

        // Form buttons
        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );
        actions.setOpaque(false);

        JButton submitButton = UITheme.createButton(
                "Submit Medical", UITheme.SUCCESS_GREEN, Color.WHITE
        );
        submitButton.addActionListener(e -> handleSubmitMedical(d));

        JButton clearButton = UITheme.createButton(
                "Clear / Cancel Edit", UITheme.PRIMARY_BLUE, Color.WHITE
        );
        clearButton.addActionListener(e -> clearForm(d));

        JButton refreshButton = UITheme.createButton(
                "Refresh List", UITheme.PRIMARY_BLUE, Color.WHITE
        );
        refreshButton.addActionListener(e -> {
            clearForm(d);
            loadMedicalData(d);
        });

        actions.add(submitButton);
        actions.add(clearButton);
        actions.add(refreshButton);

        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        fields.add(actions);

        form.add(fields, BorderLayout.CENTER);
        center.add(form, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        tablePanel.setOpaque(false);

        JLabel tableTitle = new JLabel("My Medical Records");
        tableTitle.setFont(UITheme.FONT_BODY_BOLD);
        tableTitle.setBorder(new EmptyBorder(5, 0, 5, 0));

        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {
                "Record ID", "Course", "Date",
                "Description / Reason", "Document", "Status"
        };

        d.medModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        d.tblMedical = new JTable(d.medModel);
        d.tblMedical.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        UITheme.styleTable(d.tblMedical);

        tablePanel.add(
                new JScrollPane(d.tblMedical),
                BorderLayout.CENTER
        );

        // Table buttons
        JPanel tableActions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 5)
        );
        tableActions.setOpaque(false);

        JButton viewButton = UITheme.createButton(
                "View Certificate", UITheme.PRIMARY_BLUE, Color.WHITE
        );
        viewButton.addActionListener(e -> viewSelectedMedical(d));

        JButton editButton = UITheme.createButton(
                "Edit Pending", UITheme.PRIMARY_BLUE, Color.WHITE
        );
        editButton.addActionListener(e -> editSelectedMedical(d));

        JButton deleteButton = UITheme.createButton(
                "Delete Pending", new Color(185, 48, 48), Color.WHITE
        );
        deleteButton.addActionListener(e -> deleteSelectedMedical(d));

        tableActions.add(viewButton);
        tableActions.add(editButton);
        tableActions.add(deleteButton);

        tablePanel.add(tableActions, BorderLayout.SOUTH);

        center.add(tablePanel, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    // Checkbox renderer
    private static class CheckBoxRenderer extends JCheckBox
            implements ListCellRenderer<String> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list,
                String value,
                int index,
                boolean selected,
                boolean focus) {

            setText(value);
            setSelected(Boolean.TRUE.equals(courseChecks.get(value)));
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            setOpaque(true);

            return this;
        }
    }

    // Toggle checkbox state
    private static void toggleCourse(String course) {

        boolean current =
                Boolean.TRUE.equals(courseChecks.get(course));

        courseChecks.put(course, !current);
    }

    // Get all checked courses
    private static List<String> getCheckedCourses() {

        List<String> selected = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : courseChecks.entrySet()) {

            if (Boolean.TRUE.equals(entry.getValue())) {
                selected.add(entry.getKey());
            }
        }

        return selected;
    }

    // Load records and enrolled courses
    static void loadMedicalData(UndergraduateDashboard d) {

        int studentId = d.student.getStudentId();

        d.medModel.setRowCount(0);

        List<Medical> records =
                d.studentDAO.getMedicalRecords(studentId);

        for (Medical m : records) {
            d.medModel.addRow(new Object[]{
                    m.getMedicalId(),
                    m.getCourseCode(),
                    m.getMedicalDate(),
                    m.getReason(),
                    m.getDocumentPath(),
                    m.getStatus()
            });
        }

        d.medCourseListModel.clear();
        courseChecks.clear();

        List<Course> courses =
                d.studentDAO.getEnrolledCourses(studentId);

        for (Course c : courses) {

            String code = c.getCourseCode();

            d.medCourseListModel.addElement(code);
            courseChecks.put(code, false);
        }

        d.listMedCourses.repaint();
    }

    // Choose certificate
    private static void chooseCertificate(UndergraduateDashboard d) {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Choose Medical Certificate");

        chooser.setFileFilter(new FileNameExtensionFilter(
                "Medical Certificates (PDF, JPG, JPEG, PNG)",
                "pdf", "jpg", "jpeg", "png"
        ));

        if (chooser.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
            d.txtMedPath.setText(
                    chooser.getSelectedFile().getAbsolutePath()
            );
        }
    }

    // Submit or update
    static void handleSubmitMedical(UndergraduateDashboard d) {

        List<String> selectedCourses = getCheckedCourses();

        String reason = d.txtMedReason.getText().trim();
        String selectedPath = d.txtMedPath.getText().trim();

        if (selectedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(
                    d, "Please select at least one course."
            );
            return;
        }

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(
                    d, "Please enter the medical reason."
            );
            return;
        }

        if (selectedPath.isEmpty()
                || !Files.isRegularFile(Paths.get(selectedPath))) {

            JOptionPane.showMessageDialog(
                    d, "Please choose an existing medical certificate."
            );
            return;
        }

        try {

            Date medicalDate = Date.valueOf(
                    d.txtMedDate.getText().trim()
            );

            if (!isMedicalSubmissionAllowed(medicalDate)) {
                JOptionPane.showMessageDialog(
                        d,
                        "Medical certificates must be submitted within 14 days of the absent date. " +
                                "Future dates and submissions after 14 days are not allowed.",
                        "Medical Submission Period Expired",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int studentId = d.student.getStudentId();

            // Update existing pending record
            if (editingMedicalId != -1) {

                if (selectedCourses.size() != 1) {
                    JOptionPane.showMessageDialog(
                            d, "Please select exactly one course when editing."
                    );
                    return;
                }

                String documentPath = editingOriginalPath;
                Path newCopiedFile = null;

                if (!selectedPath.equals(editingOriginalPath)) {

                    newCopiedFile = copyCertificate(
                            selectedPath, studentId
                    );

                    documentPath = newCopiedFile.toString();
                }

                Medical medical = new Medical(
                        editingMedicalId,
                        studentId,
                        selectedCourses.get(0),
                        medicalDate,
                        reason,
                        documentPath,
                        "PENDING",
                        null
                );

                boolean updated =
                        d.studentDAO.updatePendingMedical(medical);

                if (updated) {

                    JOptionPane.showMessageDialog(
                            d, "Medical request updated successfully!"
                    );

                    clearForm(d);
                    loadMedicalData(d);

                } else {

                    if (newCopiedFile != null) {
                        Files.deleteIfExists(newCopiedFile);
                    }

                    JOptionPane.showMessageDialog(
                            d, "Update failed. The request may no longer be pending."
                    );
                }

            } else {

                // New submission
                Path copiedFile = copyCertificate(
                        selectedPath, studentId
                );

                boolean submitted =
                        d.studentDAO.submitMedicalForCourses(
                                studentId,
                                selectedCourses,
                                medicalDate,
                                reason,
                                copiedFile.toString()
                        );

                if (submitted) {

                    JOptionPane.showMessageDialog(
                            d,
                            "Medical request submitted for "
                                    + selectedCourses.size() + " course(s)."
                    );

                    clearForm(d);
                    loadMedicalData(d);

                } else {

                    Files.deleteIfExists(copiedFile);

                    JOptionPane.showMessageDialog(
                            d, "Could not submit medical request."
                    );
                }
            }

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    d,
                    "Please enter date in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    d, "Certificate error: " + ex.getMessage()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    d, "Error: " + ex.getMessage()
            );
        }
    }

    // Allow submission from the absent date up to and including 14 days later.
    private static boolean isMedicalSubmissionAllowed(Date medicalDate) {
        if (medicalDate == null) {
            return false;
        }

        long days = ChronoUnit.DAYS.between(
                medicalDate.toLocalDate(),
                LocalDate.now()
        );

        return days >= 0 && days <= 14;
    }

    // Copy certificate into folder
    private static Path copyCertificate(
            String sourcePath, int studentId
    ) throws IOException {

        Path source = Paths.get(sourcePath);

        String fileName = source.getFileName().toString();
        int dot = fileName.lastIndexOf('.');

        if (dot < 0) {
            throw new IOException("Invalid certificate file.");
        }

        String extension =
                fileName.substring(dot).toLowerCase(Locale.ROOT);

        Path folder = Paths.get("medical_certificates");

        Files.createDirectories(folder);

        Path target = folder.resolve(
                "student_" + studentId + "_" + UUID.randomUUID() + extension
        );

        Files.copy(source, target);

        return target;
    }

    // View certificate
    private static void viewSelectedMedical(UndergraduateDashboard d) {

        int selected = d.tblMedical.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    d, "Please select a medical record first."
            );
            return;
        }

        int row = d.tblMedical.convertRowIndexToModel(selected);

        String path = String.valueOf(
                d.medModel.getValueAt(row, 4)
        );

        File file = new File(path);

        if (!file.isFile()) {
            JOptionPane.showMessageDialog(
                    d, "Certificate file not found:\n" + path
            );
            return;
        }

        try {

            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(
                        d, "Desktop file opening is not supported."
                );
                return;
            }

            Desktop.getDesktop().open(file);

        } catch (IOException | UnsupportedOperationException ex) {

            JOptionPane.showMessageDialog(
                    d, "Could not open certificate: " + ex.getMessage()
            );
        }
    }

    // Edit pending record
    private static void editSelectedMedical(UndergraduateDashboard d) {

        int selected = d.tblMedical.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    d, "Please select a medical record first."
            );
            return;
        }

        int row = d.tblMedical.convertRowIndexToModel(selected);

        String status = String.valueOf(
                d.medModel.getValueAt(row, 5)
        );

        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                    d, "Only PENDING records can be edited."
            );
            return;
        }

        editingMedicalId = ((Number)
                d.medModel.getValueAt(row, 0)).intValue();

        String course = String.valueOf(
                d.medModel.getValueAt(row, 1)
        );

        String date = String.valueOf(
                d.medModel.getValueAt(row, 2)
        );

        String reason = String.valueOf(
                d.medModel.getValueAt(row, 3)
        );

        editingOriginalPath = String.valueOf(
                d.medModel.getValueAt(row, 4)
        );

        // Clear previous checks
        for (String key : courseChecks.keySet()) {
            courseChecks.put(key, false);
        }

        // Tick selected course
        courseChecks.put(course, true);

        d.listMedCourses.repaint();

        d.txtMedDate.setText(date);
        d.txtMedReason.setText(reason);
        d.txtMedPath.setText(editingOriginalPath);

        JOptionPane.showMessageDialog(
                d,
                "Record loaded. Make changes and click Submit Medical."
        );
    }

    // Delete pending record
    private static void deleteSelectedMedical(UndergraduateDashboard d) {

        int selected = d.tblMedical.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    d, "Please select a medical record first."
            );
            return;
        }

        int row = d.tblMedical.convertRowIndexToModel(selected);

        String status = String.valueOf(
                d.medModel.getValueAt(row, 5)
        );

        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                    d, "Only PENDING records can be deleted."
            );
            return;
        }

        int medicalId = ((Number)
                d.medModel.getValueAt(row, 0)).intValue();

        int studentId = d.student.getStudentId();

        int confirm = JOptionPane.showConfirmDialog(
                d,
                "Delete this pending medical request?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (d.studentDAO.deletePendingMedical(medicalId, studentId)) {

            clearForm(d);
            loadMedicalData(d);

            JOptionPane.showMessageDialog(
                    d, "Medical request deleted successfully."
            );

        } else {

            JOptionPane.showMessageDialog(
                    d, "Deletion failed. The record may no longer be pending."
            );
        }
    }

    // Clear form
    private static void clearForm(UndergraduateDashboard d) {

        editingMedicalId = -1;
        editingOriginalPath = null;

        for (String course : courseChecks.keySet()) {
            courseChecks.put(course, false);
        }

        d.listMedCourses.repaint();

        d.txtMedDate.setText(
                new Date(System.currentTimeMillis()).toString()
        );

        d.txtMedReason.setText("");
        d.txtMedPath.setText("");
    }
}
