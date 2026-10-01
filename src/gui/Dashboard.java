package gui;

import dao.ResultDAO;
import model.Result;
import util.GradeCalc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {

    private JComboBox<String> cbCourse;
    private JTable table;
    private DefaultTableModel model;
    private ResultDAO resultDAO;

    public Dashboard() {
        resultDAO = new ResultDAO();

        setTitle("Faculty Marks Management System (FTMS) - Lecturer Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel: Course Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course:"));
        cbCourse = new JComboBox<>(new String[]{"ICT1113", "ICT1122", "ICT1133"});
        topPanel.add(cbCourse);

        JButton btnLoad = new JButton("Load Marks");
        topPanel.add(btnLoad);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Results Table
        String[] columns = {"Enrollment ID", "Index No", "Name", "CA Marks", "Final Marks", "Total Marks", "Grade"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCalculate = new JButton("Calculate & Save Marks");
        bottomPanel.add(btnCalculate);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnLoad.addActionListener(e -> loadData());
        btnCalculate.addActionListener(e -> calculateAndSave());
    }

    private void loadData() {
        String selectedCourse = (String) cbCourse.getSelectedItem();
        List<Result> results = resultDAO.getResultsByCourse(selectedCourse);
        model.setRowCount(0); // Table එක clear කිරීම

        for (Result r : results) {
            model.addRow(new Object[]{
                    r.getEnrollmentId(),
                    r.getStudentIndex(),
                    r.getStudentName(),
                    r.getCaMarks(),
                    r.getFinalMarks(),
                    r.getTotalMarks(),
                    r.getGrade()
            });
        }
    }

    private void calculateAndSave() {
        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No data to calculate!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < rowCount; i++) {
            int enrollmentId = (int) model.getValueAt(i, 0);
            double caMarks = Double.parseDouble(model.getValueAt(i, 3).toString());
            double finalMarks = Double.parseDouble(model.getValueAt(i, 4).toString());

            // UGC Logic අනුව ගණනය කිරීම (CA Weight 40%, Final Weight 60%)
            double totalMarks = GradeCalc.calcTotalMarks(caMarks, 40, finalMarks, 60);
            boolean caEligible = GradeCalc.isCaEligible(caMarks);
            String grade = GradeCalc.getGrade(totalMarks, caEligible);

            // Table එක Update කිරීම
            model.setValueAt(totalMarks, i, 5);
            model.setValueAt(grade, i, 6);

            // Database එකට Save/Update කිරීම
            resultDAO.saveOrUpdateMarks(enrollmentId, caMarks, finalMarks, totalMarks, grade);
        }

        JOptionPane.showMessageDialog(this, "Marks calculated and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}