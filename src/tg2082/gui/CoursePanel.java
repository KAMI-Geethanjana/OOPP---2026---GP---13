package tg2082.gui;

import common.model.Course;
import common.util.UITheme;
import tg2088.model.CourseMaterial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

final class CoursesPanel {

    private CoursesPanel() {}

    static JPanel createCoursesPanel(UndergraduateDashboard d) {

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(2, 1, 0, 15));

        // Upper: Enrolled Courses
        JPanel pnlEnrolled = new JPanel(new BorderLayout(0, 8));
        pnlEnrolled.setOpaque(false);

        JLabel lblEnrolled = new JLabel("My Enrolled Course Modules");
        lblEnrolled.setFont(UITheme.FONT_HEADER);
        pnlEnrolled.add(lblEnrolled, BorderLayout.NORTH);

        String[] cols1 = {
                "Course Code", "Course Title", "Theory Credits",
                "Practical Credits", "Total Credits", "Lecturer"
        };

        d.coursesModel = new DefaultTableModel(cols1, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        d.tblCourses = new JTable(d.coursesModel);
        UITheme.styleTable(d.tblCourses);

        pnlEnrolled.add(new JScrollPane(d.tblCourses), BorderLayout.CENTER);
        card.add(pnlEnrolled);

        // Lower: Lecture Notes with Countdown Timer at the top
        JPanel pnlMaterials = new JPanel(new BorderLayout(0, 8));
        pnlMaterials.setOpaque(false);

        JLabel lblMats = new JLabel("Course Materials & Lecture Notes");
        lblMats.setFont(UITheme.FONT_HEADER);

        // Countdown Banner placed right at the top of the lecture notes section
        JPanel pnlCountdown = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        pnlCountdown.setBackground(new Color(254, 243, 199)); // soft amber
        pnlCountdown.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblTimerIcon = new JLabel("â³");
        lblTimerIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel lblCountdown = new JLabel("Lecture Material Access Countdown: Select a course to view available notes");
        lblCountdown.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCountdown.setForeground(new Color(146, 64, 14));

        pnlCountdown.add(lblTimerIcon);
        pnlCountdown.add(lblCountdown);

        // Course dropdown and controls
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlControls.setOpaque(false);

        JLabel lblCourse = new JLabel("Select Course Code:");

        JComboBox<String> cbCourse = new JComboBox<>();
        cbCourse.setPreferredSize(new Dimension(180, 30));

        JButton btnOpen = new JButton("Open Lecture Note");

        pnlControls.add(lblCourse);
        pnlControls.add(cbCourse);
        pnlControls.add(btnOpen);

        // Header container holding title, countdown banner, and controls
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setOpaque(false);
        pnlHeader.add(lblMats, BorderLayout.NORTH);
        pnlHeader.add(pnlCountdown, BorderLayout.CENTER);
        pnlHeader.add(pnlControls, BorderLayout.SOUTH);

        pnlMaterials.add(pnlHeader, BorderLayout.NORTH);

        // Materials table with Expiry Date and Time Remaining countdown columns
        String[] cols2 = {
                "Material ID", "Course Code", "Title",
                "File Path", "Uploaded Date", "Access Expiry", "Time Remaining"
        };

        // =================================================================
        // OOP CONCEPT: POLYMORPHISM - RUNTIME (Method Overriding)
        // Anonymous subclass overriding isCellEditable to protect table data
        // =================================================================
        d.materialsModel = new DefaultTableModel(cols2, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        d.tblMaterials = new JTable(d.materialsModel);
        UITheme.styleTable(d.tblMaterials);

        // Hide file path column (model index 3) from student display
        d.tblMaterials.removeColumn(
                d.tblMaterials.getColumnModel().getColumn(3)
        );

        pnlMaterials.add(
                new JScrollPane(d.tblMaterials),
                BorderLayout.CENTER
        );

        card.add(pnlMaterials);

        // Store references for event handling and countdown timer
        d.tblMaterials.putClientProperty("courseDropdown", cbCourse);
        d.tblMaterials.putClientProperty("countdownLabel", lblCountdown);
        d.tblMaterials.putClientProperty("countdownPanel", pnlCountdown);

        // =================================================================
        // MULTITHREADING / GUI CONCURRENCY: javax.swing.Timer (Lesson 08)
        // Non-blocking timer firing ActionEvents on the Event Dispatch Thread (EDT)
        // every 1000ms (1 second) to recalculate countdown without freezing GUI.
        // =================================================================
        Timer liveTimer = new Timer(1000, e -> updateCountdownDisplay(d, lblCountdown, pnlCountdown));
        liveTimer.start();
        d.tblMaterials.putClientProperty("liveTimer", liveTimer);

        // Table selection listener: when a student clicks a specific note, show its countdown
        d.tblMaterials.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCountdownDisplay(d, lblCountdown, pnlCountdown);
            }
        });

        // Dropdown selection filters materials
        cbCourse.addActionListener(e -> {
            Object selected = cbCourse.getSelectedItem();
            if (selected != null) {
                filterMaterialsByCourse(d, selected.toString());
                updateCountdownDisplay(d, lblCountdown, pnlCountdown);
            }
        });

        // Open button
        btnOpen.addActionListener(e -> {
            int selectedRow = d.tblMaterials.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(
                        card,
                        "Please select a lecture note first.",
                        "Select Lecture Note",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            int modelRow = d.tblMaterials.convertRowIndexToModel(selectedRow);
            int matId = (int) d.materialsModel.getValueAt(modelRow, 0);

            // Double check expiry before opening
            Object data = d.tblMaterials.getClientProperty("allCourseMaterials");
            if (data instanceof List<?>) {
                for (Object item : (List<?>) data) {
                    if (item instanceof CourseMaterial && ((CourseMaterial) item).getMaterialId() == matId) {
                        CourseMaterial cm = (CourseMaterial) item;
                        if (cm.isExpired()) {
                            JOptionPane.showMessageDialog(
                                    card,
                                    "Access Expired!\n\nThis lecture note is no longer available to students.\nOnly the lecturer can access this material.",
                                    "Access Restricted",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            filterMaterialsByCourse(d, (String) cbCourse.getSelectedItem());
                            return;
                        }
                        break;
                    }
                }
            }

            String filePath = String.valueOf(
                    d.materialsModel.getValueAt(modelRow, 3)
            );

            openLectureNote(filePath);
        });

        // Double-click also opens the selected note
        d.tblMaterials.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnOpen.doClick();
                }
            }
        });

        return card;
    }

    static void loadCoursesData(UndergraduateDashboard d) {

        int stuId = d.student != null
                ? d.student.getStudentId()
                : 1;

        // Load enrolled courses
        List<Course> courses = d.studentDAO.getEnrolledCourses(stuId);

        d.coursesModel.setRowCount(0);

        JComboBox<String> cbCourse =
                (JComboBox<String>) d.tblMaterials.getClientProperty(
                        "courseDropdown"
                );

        cbCourse.removeAllItems();

        for (Course c : courses) {

            String code = c.getCourseCode();

            d.coursesModel.addRow(new Object[]{
                    code,
                    c.getCourseTitle(),
                    c.getCreditTheory(),
                    c.getCreditPractical(),
                    c.getTotalCredits(),
                    c.getLecturerName() != null
                            ? c.getLecturerName()
                            : "TBA"
            });

            cbCourse.addItem(code);
        }

        // Load lecture materials
        List<CourseMaterial> materials =
                d.studentDAO.getCourseMaterialsForStudent(stuId);

        d.tblMaterials.putClientProperty(
                "allCourseMaterials", materials
        );

        if (cbCourse.getItemCount() > 0) {
            cbCourse.setSelectedIndex(0);
        } else {
            d.materialsModel.setRowCount(0);
        }

        JLabel lblCountdown = (JLabel) d.tblMaterials.getClientProperty("countdownLabel");
        JPanel pnlCountdown = (JPanel) d.tblMaterials.getClientProperty("countdownPanel");
        if (lblCountdown != null && pnlCountdown != null) {
            updateCountdownDisplay(d, lblCountdown, pnlCountdown);
        }
    }

    private static void filterMaterialsByCourse(
            UndergraduateDashboard d,
            String courseCode) {

        d.materialsModel.setRowCount(0);

        Object data = d.tblMaterials.getClientProperty(
                "allCourseMaterials"
        );

        if (!(data instanceof List<?>)) {
            return;
        }

        List<?> materials = (List<?>) data;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Object item : materials) {

            if (item instanceof CourseMaterial) {

                CourseMaterial m = (CourseMaterial) item;

                // Requirement: Expired materials are NOT shown to students!
                if (courseCode.equalsIgnoreCase(m.getCourseCode()) && !m.isExpired()) {

                    String expiryStr = (m.getExpiryDate() != null) ? sdf.format(m.getExpiryDate()) : "Permanent";
                    String remainingStr = m.getRemainingTimeFormatted();

                    d.materialsModel.addRow(new Object[]{
                            m.getMaterialId(),
                            m.getCourseCode(),
                            m.getTitle(),
                            m.getFilePath(),
                            m.getUploadedDate(),
                            expiryStr,
                            remainingStr
                    });
                }
            }
        }
    }

    /**
     * Updates the countdown display label and table countdown values every second
     */
    private static void updateCountdownDisplay(UndergraduateDashboard d, JLabel lblCountdown, JPanel pnlCountdown) {
        JComboBox<String> cbCourse = (JComboBox<String>) d.tblMaterials.getClientProperty("courseDropdown");
        if (cbCourse == null || cbCourse.getSelectedItem() == null) {
            lblCountdown.setText("Lecture Material Access Countdown: Select a course to view available notes");
            return;
        }

        String courseCode = (String) cbCourse.getSelectedItem();
        Object data = d.tblMaterials.getClientProperty("allCourseMaterials");
        if (!(data instanceof List<?>)) return;
        List<?> materials = (List<?>) data;

        boolean hasNewlyExpired = false;
        CourseMaterial activeTarget = null;

        // 1. If user selected a row in the table, target that material
        int selectedRow = d.tblMaterials.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = d.tblMaterials.convertRowIndexToModel(selectedRow);
            int matId = (int) d.materialsModel.getValueAt(modelRow, 0);
            for (Object item : materials) {
                if (item instanceof CourseMaterial && ((CourseMaterial) item).getMaterialId() == matId) {
                    activeTarget = (CourseMaterial) item;
                    break;
                }
            }
        }

        // 2. Otherwise find the nearest expiring active material for this course
        if (activeTarget == null) {
            long minRemaining = Long.MAX_VALUE;
            for (Object item : materials) {
                if (item instanceof CourseMaterial) {
                    CourseMaterial m = (CourseMaterial) item;
                    if (courseCode.equalsIgnoreCase(m.getCourseCode()) && m.getExpiryDate() != null) {
                        if (!m.isExpired() && m.getRemainingMillis() < minRemaining) {
                            minRemaining = m.getRemainingMillis();
                            activeTarget = m;
                        }
                    }
                }
            }
        }

        // Update the table rows' Time Remaining column (column 6 in model)
        int rowCount = d.materialsModel.getRowCount();
        for (int r = rowCount - 1; r >= 0; r--) {
            int matId = (int) d.materialsModel.getValueAt(r, 0);
            for (Object item : materials) {
                if (item instanceof CourseMaterial && ((CourseMaterial) item).getMaterialId() == matId) {
                    CourseMaterial m = (CourseMaterial) item;
                    if (m.isExpired()) {
                        hasNewlyExpired = true;
                    } else {
                        d.materialsModel.setValueAt(m.getRemainingTimeFormatted(), r, 6);
                    }
                    break;
                }
            }
        }

        // If an item expired, refresh the course materials table to immediately hide it
        if (hasNewlyExpired) {
            filterMaterialsByCourse(d, courseCode);
        }

        // Update top countdown banner text and colors
        if (activeTarget != null) {
            if (activeTarget.getExpiryDate() == null) {
                lblCountdown.setText("â™¾ï¸ Material [" + activeTarget.getTitle() + "]: Permanent Access (No Expiry Date)");
                pnlCountdown.setBackground(new Color(240, 249, 255));
                pnlCountdown.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(59, 130, 246), 1, true),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)
                ));
            } else if (activeTarget.isExpired()) {
                lblCountdown.setText("âš ï¸ ACCESS CLOSED: Material [" + activeTarget.getTitle() + "] has expired and is now hidden from students!");
                pnlCountdown.setBackground(new Color(254, 226, 226));
                pnlCountdown.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(239, 68, 68), 1, true),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)
                ));
            } else {
                lblCountdown.setText("â³ LIVE COUNTDOWN: [" + activeTarget.getTitle() + "] Access Closes In: " + activeTarget.getRemainingTimeFormatted());
                pnlCountdown.setBackground(new Color(254, 243, 199));
                pnlCountdown.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(245, 158, 11), 1, true),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)
                ));
            }
        } else {
            lblCountdown.setText("â„¹ï¸ Lecture Materials: All available course notes for " + courseCode + " have permanent access.");
            pnlCountdown.setBackground(new Color(241, 245, 249));
            pnlCountdown.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(148, 163, 184), 1, true),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
        }
    }

    private static void openLectureNote(String filePath) {

        if (filePath == null || filePath.trim().isEmpty()
                || "null".equalsIgnoreCase(filePath.trim())) {

            JOptionPane.showMessageDialog(
                    null,
                    "Lecture note file path is empty.",
                    "File Not Available",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            File file = new File(filePath.trim());

            // Try project working directory for relative paths
            if (!file.isAbsolute()) {
                file = new File(
                        System.getProperty("user.dir"),
                        filePath.trim()
                );
            }

            if (!file.exists()) {
                File alt = new File(System.getProperty("user.dir"), "materials/" + new File(filePath.trim()).getName());
                if (alt.exists()) {
                    file = alt;
                }
            }

            file = file.getCanonicalFile();

            if (!file.exists() || !file.isFile()) {

                JOptionPane.showMessageDialog(
                        null,
                        "Lecture note file was not found.\n\n"
                                + "Saved path:\n" + filePath
                                + "\n\nChecked location:\n"
                                + file.getAbsolutePath(),
                        "File Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (!Desktop.isDesktopSupported()
                    || !Desktop.getDesktop().isSupported(
                    Desktop.Action.OPEN)) {

                JOptionPane.showMessageDialog(
                        null,
                        "Your computer cannot open files automatically.",
                        "Open Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Desktop.getDesktop().open(file);

        } catch (IOException | SecurityException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    "Unable to open lecture note:\n" + ex.getMessage(),
                    "Open Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
