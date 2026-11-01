package tg2088.gui;

import common.util.UITheme;
import tg2088.dao.IQuizDAO;
import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizQuestion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * ============================================================================
 * Lesson 04 (Callbacks, Interfaces), Lesson 05 (Exceptions), Lesson 02 (OOP GUI)
 * ============================================================================
 * Dialog for managing Multiple Choice Questions for a specific Quiz.
 * ============================================================================
 */
public class QuizQuestionDialog extends JDialog {

    private final Quiz quiz;
    private final IQuizDAO quizDAO;

    private JTable tblQuestions;
    private DefaultTableModel tableModel;

    private JTextArea txtQuestionText;
    private JTextField txtOptionA;
    private JTextField txtOptionB;
    private JTextField txtOptionC;
    private JTextField txtOptionD;
    private JComboBox<String> cbCorrectOption;
    private JTextField txtPoints;

    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnClear;
    private JLabel lblStatus;

    private int selectedQuestionId = 0; // 0 means new question, > 0 means edit mode

    public QuizQuestionDialog(JFrame parent, Quiz quiz, IQuizDAO quizDAO) {
        super(parent, "Manage Quiz Questions - " + quiz.getTitle() + " (" + quiz.getCourseCode() + ")", true);
        this.quiz = quiz;
        this.quizDAO = quizDAO;

        initUI();
        loadQuestions();
    }

    private void initUI() {
        setSize(950, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout(15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SIDEBAR_BG);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Questions Management: " + quiz.getTitle());
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Course: " + quiz.getCourseCode() + " | Duration: " + quiz.getDurationMinutes() + " mins | Target Marks: " + quiz.getTotalMarks());
        lblInfo.setFont(UITheme.FONT_SUBTITLE);
        lblInfo.setForeground(UITheme.SIDEBAR_TEXT);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblInfo, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Split (Table at top, Form at bottom)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.CONTENT_BG);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Table Panel
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(5, 5));
        tableCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTableTitle = new JLabel("Questions in this Quiz (Click row to edit):");
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);
        tableCard.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Q#", "Question", "Opt A", "Opt B", "Opt C", "Opt D", "Correct", "Points"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblQuestions = new JTable(tableModel);
        UITheme.styleTable(tblQuestions);
        tblQuestions.getColumnModel().getColumn(0).setMaxWidth(40);
        tblQuestions.getColumnModel().getColumn(1).setMaxWidth(40);
        tblQuestions.getColumnModel().getColumn(7).setMaxWidth(60);
        tblQuestions.getColumnModel().getColumn(8).setMaxWidth(60);

        tblQuestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblQuestions.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblQuestions);
        scrollPane.setPreferredSize(new Dimension(900, 200));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.NORTH);

        // Editor Form Panel
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(10, 10));
        formCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblFormTitle = new JLabel("Add / Edit Multiple Choice Question:");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setBackground(UITheme.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Question Text
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblQ = new JLabel("Question Text:");
        lblQ.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblQ, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.85; gbc.gridwidth = 3;
        txtQuestionText = new JTextArea(3, 30);
        txtQuestionText.setFont(UITheme.FONT_BODY);
        txtQuestionText.setLineWrap(true);
        txtQuestionText.setWrapStyleWord(true);
        JScrollPane spQ = new JScrollPane(txtQuestionText);
        spQ.setPreferredSize(new Dimension(500, 60));
        formFields.add(spQ, gbc);

        // Option A & B
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblA = new JLabel("Option [A]:");
        lblA.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblA, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35;
        txtOptionA = new JTextField();
        formFields.add(txtOptionA, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblB = new JLabel("Option [B]:");
        lblB.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblB, gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.35;
        txtOptionB = new JTextField();
        formFields.add(txtOptionB, gbc);

        // Option C & D
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        JLabel lblC = new JLabel("Option [C]:");
        lblC.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblC, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.35;
        txtOptionC = new JTextField();
        formFields.add(txtOptionC, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.15;
        JLabel lblD = new JLabel("Option [D]:");
        lblD.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblD, gbc);

        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0.35;
        txtOptionD = new JTextField();
        formFields.add(txtOptionD, gbc);

        // Correct Option & Points
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.15;
        JLabel lblCorrect = new JLabel("Correct Option:");
        lblCorrect.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblCorrect, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.35;
        cbCorrectOption = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        cbCorrectOption.setFont(UITheme.FONT_BODY);
        formFields.add(cbCorrectOption, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.15;
        JLabel lblPts = new JLabel("Points / Marks:");
        lblPts.setFont(UITheme.FONT_BODY_BOLD);
        formFields.add(lblPts, gbc);

        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 0.35;
        txtPoints = new JTextField("10.0");
        formFields.add(txtPoints, gbc);

        formCard.add(formFields, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        buttonRow.setBackground(UITheme.CARD_BG);

        lblStatus = new JLabel("Mode: New Question");
        lblStatus.setFont(UITheme.FONT_BODY_BOLD);
        lblStatus.setForeground(UITheme.PRIMARY_BLUE);

        btnSave = UITheme.createButton("  Save Question  ", UITheme.SUCCESS_GREEN, Color.WHITE);
        btnDelete = UITheme.createButton(" Delete Question ", UITheme.DANGER_RED, Color.WHITE);
        btnClear = UITheme.createButton(" Clear Fields ", UITheme.TEXT_MUTED, Color.WHITE);
        JButton btnClose = UITheme.createButton(" Close ", UITheme.SIDEBAR_BG, Color.WHITE);

        btnDelete.setEnabled(false);

        // Callbacks (Lesson 04, Slide 30)
        btnSave.addActionListener(e -> saveQuestion());
        btnDelete.addActionListener(e -> deleteQuestion());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());

        buttonRow.add(lblStatus);
        buttonRow.add(Box.createHorizontalStrut(15));
        buttonRow.add(btnSave);
        buttonRow.add(btnDelete);
        buttonRow.add(btnClear);
        buttonRow.add(btnClose);

        formCard.add(buttonRow, BorderLayout.SOUTH);
        centerPanel.add(formCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadQuestions() {
        tableModel.setRowCount(0);
        List<QuizQuestion> questions = quizDAO.getQuestionsByQuizId(quiz.getQuizId());
        int qNum = 1;
        for (QuizQuestion q : questions) {
            tableModel.addRow(new Object[]{
                    q.getQuestionId(),
                    qNum++,
                    q.getQuestionText(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getCorrectOption(),
                    q.getPoints()
            });
        }
        quiz.setQuestionCount(questions.size());
    }

    private void populateFormFromRow(int row) {
        selectedQuestionId = (int) tableModel.getValueAt(row, 0);
        txtQuestionText.setText((String) tableModel.getValueAt(row, 2));
        txtOptionA.setText((String) tableModel.getValueAt(row, 3));
        txtOptionB.setText((String) tableModel.getValueAt(row, 4));
        txtOptionC.setText((String) tableModel.getValueAt(row, 5));
        txtOptionD.setText((String) tableModel.getValueAt(row, 6));

        char c = (char) tableModel.getValueAt(row, 7);
        cbCorrectOption.setSelectedItem(String.valueOf(c));
        txtPoints.setText(String.valueOf(tableModel.getValueAt(row, 8)));

        btnDelete.setEnabled(true);
        lblStatus.setText("Mode: Editing Question #" + tableModel.getValueAt(row, 1) + " (ID: " + selectedQuestionId + ")");
        lblStatus.setForeground(UITheme.WARNING_AMBER);
    }

    private void saveQuestion() {
        String qText = txtQuestionText.getText().trim();
        String optA = txtOptionA.getText().trim();
        String optB = txtOptionB.getText().trim();
        String optC = txtOptionC.getText().trim();
        String optD = txtOptionD.getText().trim();
        String correctStr = (String) cbCorrectOption.getSelectedItem();
        char correctOpt = (correctStr != null && !correctStr.isEmpty()) ? correctStr.charAt(0) : 'A';

        if (qText.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Question text and Options A to D) must be provided.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double points;
        try {
            points = Double.parseDouble(txtPoints.getText().trim());
            if (points < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Points must be a valid positive number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (selectedQuestionId == 0) {
                // New question
                QuizQuestion q = new QuizQuestion(0, quiz.getQuizId(), qText, optA, optB, optC, optD, correctOpt, points);
                int newId = quizDAO.addQuestion(q);
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Update existing
                QuizQuestion q = new QuizQuestion(selectedQuestionId, quiz.getQuizId(), qText, optA, optB, optC, optD, correctOpt, points);
                boolean updated = quizDAO.updateQuestion(q);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "Question updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            clearForm();
            loadQuestions();
        } catch (QuizOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteQuestion() {
        if (selectedQuestionId <= 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this question?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = quizDAO.deleteQuestion(selectedQuestionId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Question deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadQuestions();
                }
            } catch (QuizOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedQuestionId = 0;
        txtQuestionText.setText("");
        txtOptionA.setText("");
        txtOptionB.setText("");
        txtOptionC.setText("");
        txtOptionD.setText("");
        cbCorrectOption.setSelectedIndex(0);
        txtPoints.setText("10.0");
        btnDelete.setEnabled(false);
        lblStatus.setText("Mode: New Question");
        lblStatus.setForeground(UITheme.PRIMARY_BLUE);
        tblQuestions.clearSelection();
    }
}
