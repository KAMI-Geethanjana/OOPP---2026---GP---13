package tg2088.gui;

import common.util.UITheme;
import tg2088.dao.IQuizDAO;
import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.util.QuizFileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * ============================================================================
 * Lesson 04 (Interfaces), Lesson 05 (Exceptions), Lesson 06 (File Handling)
 * ============================================================================
 * Dialog to inspect student submissions / attempts for a specific quiz,
 * and export formatted results reports to text files.
 * ============================================================================
 */
public class QuizResultsDialog extends JDialog {

    private final Quiz quiz;
    private final IQuizDAO quizDAO;

    private JTable tblAttempts;
    private DefaultTableModel tableModel;
    private JLabel lblSummary;

    public QuizResultsDialog(JFrame parent, Quiz quiz, IQuizDAO quizDAO) {
        super(parent, "Quiz Submissions & Results - " + quiz.getTitle(), true);
        this.quiz = quiz;
        this.quizDAO = quizDAO;

        initUI();
        loadAttempts();
    }

    private void initUI() {
        setSize(850, 520);
        setMinimumSize(new Dimension(750, 450));
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SIDEBAR_BG);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Submissions & Marks: " + quiz.getTitle());
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Course Code: " + quiz.getCourseCode() + " | Maximum Marks: " + quiz.getTotalMarks());
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.SIDEBAR_TEXT);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Table Card
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.CONTENT_BG);
        centerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        lblSummary = new JLabel("Loading submission records...");
        lblSummary.setFont(UITheme.FONT_BODY_BOLD);
        lblSummary.setForeground(UITheme.TEXT_DARK);
        card.add(lblSummary, BorderLayout.NORTH);

        String[] columns = {"Attempt ID", "Reg No", "Student Name", "Score", "Max Marks", "Percentage", "Status", "Submitted At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAttempts = new JTable(tableModel);
        UITheme.styleTable(tblAttempts);

        JScrollPane scrollPane = new JScrollPane(tblAttempts);
        card.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(card, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Action Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(UITheme.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER));

        JButton btnExport = UITheme.createButton("  Export Results (.txt)  ", UITheme.PRIMARY_BLUE, Color.WHITE);
        JButton btnAddMock = UITheme.createButton(" + Demo Submission ", UITheme.WARNING_AMBER, Color.WHITE);
        JButton btnRefresh = UITheme.createButton(" Refresh ", UITheme.SIDEBAR_BG, Color.WHITE);
        JButton btnClose = UITheme.createButton(" Close ", UITheme.TEXT_MUTED, Color.WHITE);

        btnExport.addActionListener(e -> exportResultsReport());
        btnAddMock.addActionListener(e -> addDemoSubmission());
        btnRefresh.addActionListener(e -> loadAttempts());
        btnClose.addActionListener(e -> dispose());

        footer.add(btnExport);
        footer.add(btnAddMock);
        footer.add(btnRefresh);
        footer.add(btnClose);

        add(footer, BorderLayout.SOUTH);
    }

    private void loadAttempts() {
        tableModel.setRowCount(0);
        List<QuizAttempt> attempts = quizDAO.getAttemptsByQuizId(quiz.getQuizId());

        int passCount = 0;
        for (QuizAttempt a : attempts) {
            tableModel.addRow(new Object[]{
                    a.getAttemptId(),
                    a.getStudentRegNo(),
                    a.getStudentName(),
                    String.format("%.1f", a.getScore()),
                    String.format("%.1f", a.getMaxScore()),
                    String.format("%.1f%%", a.getPercentage()),
                    a.getStatus(),
                    a.getAttemptedAt()
            });
            if ("PASS".equalsIgnoreCase(a.getStatus())) {
                passCount++;
            }
        }

        lblSummary.setText("Total Submissions: " + attempts.size() + " | Passed: " + passCount +
                " | Failed: " + (attempts.size() - passCount) + " | Pass Rate: " +
                (attempts.isEmpty() ? "0.0%" : String.format("%.1f%%", ((double) passCount / attempts.size()) * 100.0)));
    }

    private void exportResultsReport() {
        List<QuizAttempt> attempts = quizDAO.getAttemptsByQuizId(quiz.getQuizId());
        if (attempts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No submission records available to export.", "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Quiz Results Report");
        fileChooser.setSelectedFile(new File("Quiz_" + quiz.getCourseCode() + "_" + quiz.getQuizId() + "_Results.txt"));

        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            boolean success = QuizFileHandler.exportQuizResultsReport(quiz, attempts, file);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Quiz results successfully exported to:\n" + file.getAbsolutePath(),
                        "Export Succeeded (Lesson 06)", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export report.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addDemoSubmission() {
        JTextField txtReg = new JTextField("TG/2024/2082");
        JTextField txtName = new JTextField("W.D.P Kaveesha");
        JTextField txtScore = new JTextField("80.0");

        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.add(new JLabel("Student Reg No:"));
        p.add(txtReg);
        p.add(new JLabel("Student Full Name:"));
        p.add(txtName);
        p.add(new JLabel("Score Earned:"));
        p.add(txtScore);

        int result = JOptionPane.showConfirmDialog(this, p, "Add Demo Student Submission (Viva Testing)", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double sc = Double.parseDouble(txtScore.getText().trim());
                QuizAttempt a = new QuizAttempt(0, quiz.getQuizId(), txtReg.getText().trim(),
                        txtName.getText().trim(), sc, quiz.getTotalMarks(), 0, "PASS", null);
                quizDAO.recordAttempt(a);
                JOptionPane.showMessageDialog(this, "Demo submission added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttempts();
            } catch (NumberFormatException | QuizOperationException ex) {
                JOptionPane.showMessageDialog(this, "Error adding demo attempt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
