package tg2088.gui;

import common.util.UITheme;
import tg2088.dao.IQuizDAO;
import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * ============================================================================
 * Lesson 02 (OOP GUI Design), Lesson 04 (Callbacks), Lesson 05 (Exceptions)
 * ============================================================================
 * Interactive Quiz Preview / Test Runner for Lecturers.
 * Allows lecturers to test-drive their quizzes and verify grading logic live.
 * ============================================================================
 */
public class QuizTestRunnerDialog extends JDialog {

    private final Quiz quiz;
    private final IQuizDAO quizDAO;
    private final List<QuizQuestion> questions;
    private final List<ButtonGroup> optionGroups = new ArrayList<>();

    public QuizTestRunnerDialog(JFrame parent, Quiz quiz, IQuizDAO quizDAO) {
        super(parent, "Live Quiz Preview & Test Runner - " + quiz.getTitle(), true);
        this.quiz = quiz;
        this.quizDAO = quizDAO;
        this.questions = quizDAO.getQuestionsByQuizId(quiz.getQuizId());

        initUI();
    }

    private void initUI() {
        setSize(850, 650);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Preview: " + quiz.getTitle());
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Course: " + quiz.getCourseCode() + " | Questions: " + questions.size() +
                " | Allocated Time: " + quiz.getDurationMinutes() + " mins | Maximum Marks: " + quiz.getTotalMarks());
        lblInfo.setFont(UITheme.FONT_SUBTITLE);
        lblInfo.setForeground(UITheme.SIDEBAR_TEXT);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblInfo, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        if (questions.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(UITheme.CONTENT_BG);
            JLabel lblEmpty = new JLabel("No questions added to this quiz yet. Please add questions first!");
            lblEmpty.setFont(UITheme.FONT_HEADER);
            lblEmpty.setForeground(UITheme.TEXT_MUTED);
            emptyPanel.add(lblEmpty);
            add(emptyPanel, BorderLayout.CENTER);
            return;
        }

        // Center: Scrollable list of questions
        JPanel questionsContainer = new JPanel();
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        questionsContainer.setBackground(UITheme.CONTENT_BG);
        questionsContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        int qIndex = 1;
        for (QuizQuestion q : questions) {
            JPanel qCard = UITheme.createCardPanel();
            qCard.setLayout(new BorderLayout(8, 8));
            qCard.setBorder(new EmptyBorder(12, 16, 12, 16));
            qCard.setMaximumSize(new Dimension(800, 220));

            // Question Header
            JLabel lblQText = new JLabel("<html><b>" + qIndex + ". " + escapeHtml(q.getQuestionText()) +
                    "</b> <font color='#6366f1'>[" + q.getPoints() + " marks]</font></html>");
            lblQText.setFont(UITheme.FONT_BODY);
            lblQText.setForeground(UITheme.TEXT_DARK);
            qCard.add(lblQText, BorderLayout.NORTH);

            // Radio options panel
            JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 4, 4));
            optionsPanel.setBackground(UITheme.CARD_BG);

            ButtonGroup bg = new ButtonGroup();
            JRadioButton rbA = new JRadioButton("A) " + q.getOptionA());
            JRadioButton rbB = new JRadioButton("B) " + q.getOptionB());
            JRadioButton rbC = new JRadioButton("C) " + q.getOptionC());
            JRadioButton rbD = new JRadioButton("D) " + q.getOptionD());

            rbA.setActionCommand("A");
            rbB.setActionCommand("B");
            rbC.setActionCommand("C");
            rbD.setActionCommand("D");

            Font optFont = UITheme.FONT_BODY;
            for (JRadioButton rb : new JRadioButton[]{rbA, rbB, rbC, rbD}) {
                rb.setFont(optFont);
                rb.setBackground(UITheme.CARD_BG);
                rb.setFocusPainted(false);
                bg.add(rb);
                optionsPanel.add(rb);
            }

            optionGroups.add(bg);
            qCard.add(optionsPanel, BorderLayout.CENTER);

            questionsContainer.add(qCard);
            questionsContainer.add(Box.createVerticalStrut(12));
            qIndex++;
        }

        JScrollPane scrollPane = new JScrollPane(questionsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer with Submit Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(UITheme.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER));

        JCheckBox chkRecord = new JCheckBox("Save this attempt in Database as Lecturer Preview");
        chkRecord.setBackground(UITheme.CARD_BG);
        chkRecord.setFont(UITheme.FONT_BODY);

        JButton btnSubmit = UITheme.createButton("  Submit & Evaluate Quiz  ", UITheme.SUCCESS_GREEN, Color.WHITE);
        JButton btnClose = UITheme.createButton(" Close ", UITheme.SIDEBAR_BG, Color.WHITE);

        btnSubmit.addActionListener(e -> evaluateQuiz(chkRecord.isSelected()));
        btnClose.addActionListener(e -> dispose());

        footer.add(chkRecord);
        footer.add(Box.createHorizontalStrut(10));
        footer.add(btnSubmit);
        footer.add(btnClose);

        add(footer, BorderLayout.SOUTH);
    }

    private void evaluateQuiz(boolean recordInDb) {
        double earnedPoints = 0.0;
        double maxPoints = 0.0;
        int correctCount = 0;
        int answeredCount = 0;

        StringBuilder feedback = new StringBuilder();
        feedback.append("=== QUIZ EVALUATION RESULTS ===\n\n");

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            maxPoints += q.getPoints();

            ButtonGroup bg = optionGroups.get(i);
            ButtonModel selected = bg.getSelection();

            char studentChoice = ' ';
            if (selected != null) {
                studentChoice = selected.getActionCommand().charAt(0);
                answeredCount++;
            }

            boolean isCorrect = (studentChoice == q.getCorrectOption());
            if (isCorrect) {
                earnedPoints += q.getPoints();
                correctCount++;
                feedback.append(String.format("Q%d: Correct! (+%.1f marks)\n", (i + 1), q.getPoints()));
            } else {
                feedback.append(String.format("Q%d: Incorrect. Your Answer: [%c] | Correct Answer: [%c] - %s\n",
                        (i + 1), (studentChoice == ' ' ? '-' : studentChoice), q.getCorrectOption(), q.getCorrectAnswerText()));
            }
        }

        double percentage = (maxPoints > 0) ? (earnedPoints / maxPoints) * 100.0 : 0.0;
        String status = IQuizDAO.isPassingScore(earnedPoints, maxPoints) ? "PASS" : "FAIL";

        feedback.append("\n----------------------------------------\n");
        feedback.append(String.format("Total Questions Answered : %d / %d\n", answeredCount, questions.size()));
        feedback.append(String.format("Total Score Earned       : %.1f / %.1f\n", earnedPoints, maxPoints));
        feedback.append(String.format("Overall Percentage       : %.1f%%\n", percentage));
        feedback.append(String.format("Result Status            : %s (Threshold: %.0f%%)\n", status, IQuizDAO.PASSING_PERCENTAGE));

        if (recordInDb) {
            try {
                QuizAttempt attempt = new QuizAttempt(0, quiz.getQuizId(), "LEC/PREVIEW", "Lecturer Preview",
                        earnedPoints, maxPoints, percentage, status, null);
                quizDAO.recordAttempt(attempt);
                feedback.append("\nAttempt saved successfully to database!");
            } catch (QuizOperationException ex) {
                feedback.append("\nFailed to save attempt: ").append(ex.getMessage());
            }
        }

        JTextArea ta = new JTextArea(feedback.toString(), 16, 45);
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(ta);

        JOptionPane.showMessageDialog(this, sp, "Quiz Evaluation Summary",
                status.equals("PASS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
