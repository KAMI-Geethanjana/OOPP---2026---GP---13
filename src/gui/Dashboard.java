package gui;

import dao.CourseDAO;
import dao.ResultDAO;
import model.Result;
import util.UserSession;
import util.GradeCalc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {

    private JComboBox<String> cbCourse;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnCalculateSave;

    private CourseDAO courseDAO = new CourseDAO();
    private ResultDAO resultDAO = new ResultDAO();

    public Dashboard() {
        setTitle("FTMS - Lecturer Dashboard (" + UserSession.getUsername() + ")");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel - Course Selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course: "));

        cbCourse = new JComboBox<>();
        loadLecturerCourses();
        cbCourse.addActionListener(e -> loadData());
        topPanel.add(cbCourse);

        add(topPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Enrollment ID", "Index No", "Name", "Attempt", "CA Marks", "Final Marks", "Total Marks", "Grade"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel - Calculation & Action Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCalculateSave = new JButton("Calculate & Save Results");
        btnCalculateSave.addActionListener(e -> calculateAndSave());
        bottomPanel.add(btnCalculateSave);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load Initial Data
        loadData();
    }

    private void loadLecturerCourses() {
        List<String> courses = courseDAO.getCoursesByLecturer(UserSession.getUserId());
        for (String c : courses) {
            cbCourse.addItem(c);
        }
    }

    private void loadData() {
        String selectedCourse = (String) cbCourse.getSelectedItem();
        if (selectedCourse == null || selectedCourse.isEmpty()) return;

        List<Result> results = resultDAO.getResultsByCourse(selectedCourse);
        model.setRowCount(0);

        for (Result r : results) {
            model.addRow(new Object[]{
                    r.getEnrollmentId(),
                    r.getStudentIndex(),
                    r.getStudentName(),
                    r.getAttemptType(),
                    r.getCaMarks(),
                    r.getFinalMarks(),
                    r.getTotalMarks(),
                    r.getGrade()
            });
        }
    }

    private void calculateAndSave() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        int rowCount = model.getRowCount();
        if (rowCount == 0) return;

        try {
            for (int i = 0; i < rowCount; i++) {
                int enrollmentId = Integer.parseInt(model.getValueAt(i, 0).toString());
                String attemptType = model.getValueAt(i, 3).toString();

                Object caObj = model.getValueAt(i, 4);
                Object finalObj = model.getValueAt(i, 5);

                double caMarks = (caObj != null && !caObj.toString().isEmpty()) ? Double.parseDouble(caObj.toString()) : 0.0;
                double finalMarks = (finalObj != null && !finalObj.toString().isEmpty()) ? Double.parseDouble(finalObj.toString()) : 0.0;

                double totalMarks = GradeCalc.calcTotalMarks(caMarks, 40, finalMarks, 60);
                boolean caEligible = GradeCalc.isCaEligible(caMarks);

                String grade = GradeCalc.getGrade(totalMarks, caEligible, attemptType);

                model.setValueAt(totalMarks, i, 6);
                model.setValueAt(grade, i, 7);

                resultDAO.saveOrUpdateMarks(enrollmentId, caMarks, finalMarks, totalMarks, grade);
            }

            JOptionPane.showMessageDialog(this, "Marks calculated and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for CA and Final Marks!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}