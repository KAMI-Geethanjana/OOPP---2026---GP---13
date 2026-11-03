
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

        // Lower: Lecture Notes
        JPanel pnlMaterials = new JPanel(new BorderLayout(0, 8));
        pnlMaterials.setOpaque(false);

        JLabel lblMats = new JLabel("Course Materials & Lecture Notes");
        lblMats.setFont(UITheme.FONT_HEADER);

        // Course dropdown
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlControls.setOpaque(false);

        JLabel lblCourse = new JLabel("Select Course Code:");

        JComboBox<String> cbCourse = new JComboBox<>();
        cbCourse.setPreferredSize(new Dimension(180, 30));

        JButton btnOpen = new JButton("Open Lecture Note");

        pnlControls.add(lblCourse);
        pnlControls.add(cbCourse);
        pnlControls.add(btnOpen);

        JPanel pnlHeader = new JPanel(new BorderLayout(0, 5));
        pnlHeader.setOpaque(false);
        pnlHeader.add(lblMats, BorderLayout.NORTH);
        pnlHeader.add(pnlControls, BorderLayout.CENTER);

        pnlMaterials.add(pnlHeader, BorderLayout.NORTH);

        // Materials table
        String[] cols2 = {
                "Material ID", "Course Code", "Title",
                "File Path", "Uploaded Date"
        };

        d.materialsModel = new DefaultTableModel(cols2, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        d.tblMaterials = new JTable(d.materialsModel);
        UITheme.styleTable(d.tblMaterials);

        // Hide file path column from the student
        d.tblMaterials.removeColumn(
                d.tblMaterials.getColumnModel().getColumn(3)
        );

        pnlMaterials.add(
                new JScrollPane(d.tblMaterials),
                BorderLayout.CENTER
        );

        card.add(pnlMaterials);

        // Store dropdown reference for loading data
        d.tblMaterials.putClientProperty("courseDropdown", cbCourse);

        // Dropdown selection filters materials
        cbCourse.addActionListener(e -> {

            Object selected = cbCourse.getSelectedItem();

            if (selected != null) {
                filterMaterialsByCourse(d, selected.toString());
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

        for (Object item : materials) {

            if (item instanceof CourseMaterial) {

                CourseMaterial m = (CourseMaterial) item;

                if (courseCode.equalsIgnoreCase(m.getCourseCode())) {

                    d.materialsModel.addRow(new Object[]{
                            m.getMaterialId(),
                            m.getCourseCode(),
                            m.getTitle(),
                            m.getFilePath(),
                            m.getUploadedDate()
                    });
                }
            }
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
