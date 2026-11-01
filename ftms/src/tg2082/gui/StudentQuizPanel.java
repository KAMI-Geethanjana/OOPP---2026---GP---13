package tg2082.gui;

import common.model.Course;
import common.util.UITheme;
import tg2088.dao.IQuizDAO;
import tg2088.dao.QuizDAO;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;
import tg2088.exception.QuizOperationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/** Student-facing quiz UI. Lecturer's quiz creation code is unchanged. */
final class StudentQuizPanel {
    private static final String KEY = "studentQuizTable";
    private StudentQuizPanel() {}

    static JPanel create(UndergraduateDashboard d) {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout(12, 12));
        JLabel heading = new JLabel("My Active Course Quizzes");
        heading.setFont(UITheme.FONT_HEADER);
        panel.add(heading, BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Quiz ID", "Course", "Quiz", "Questions", "Duration (min)"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        panel.putClientProperty(KEY, table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = UITheme.createButton("Refresh Quizzes", UITheme.SIDEBAR_BG, Color.WHITE);
        JButton take = UITheme.createButton("Attempt Selected Quiz", UITheme.SUCCESS_GREEN, Color.WHITE);
        refresh.addActionListener(e -> load(d));
        take.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(d, "Select a quiz first."); return; }
            int quizId = (Integer) model.getValueAt(table.convertRowIndexToModel(row), 0);
            attempt(d, quizId);
        });
        actions.add(refresh);
        actions.add(take);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JTable table(UndergraduateDashboard d) {
        for (Component c : d.contentCards.getComponents()) {
            if (c instanceof JPanel p && p.getClientProperty(KEY) instanceof JTable t) return t;
        }
        return null;
    }

    static void load(UndergraduateDashboard d) {
        JTable t = table(d);
        if (t == null) return;
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        model.setRowCount(0);
        if (d.student == null) return;
        try {
            IQuizDAO dao = new QuizDAO();
            Set<Integer> seen = new HashSet<>();
            for (Course course : d.studentDAO.getEnrolledCourses(d.student.getStudentId())) {
                for (Quiz q : dao.getQuizzesByCourse(course.getCourseCode(), true)) {
                    if (seen.add(q.getQuizId())) model.addRow(new Object[]{q.getQuizId(), q.getCourseCode(), q.getTitle(), q.getQuestionCount(), q.getDurationMinutes()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(d, "Unable to load quizzes: " + ex.getMessage(), "Quiz Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void attempt(UndergraduateDashboard d, int quizId) {
        if (d.student == null) return;
        IQuizDAO dao = new QuizDAO();
        Quiz quiz = dao.getQuizById(quizId);
        if (quiz == null || !quiz.isActive()) { JOptionPane.showMessageDialog(d, "Quiz is not available."); load(d); return; }
        boolean enrolled = d.studentDAO.getEnrolledCourses(d.student.getStudentId()).stream()
                .anyMatch(c -> quiz.getCourseCode().equals(c.getCourseCode()));
        if (!enrolled) { JOptionPane.showMessageDialog(d, "You are not enrolled in this course."); return; }
        List<QuizQuestion> questions = dao.getQuestionsByQuizId(quizId);
        if (questions.isEmpty()) { JOptionPane.showMessageDialog(d, "This quiz has no questions yet."); return; }
        JDialog dialog = new JDialog(d, "Quiz: " + quiz.getTitle(), true);
        dialog.setSize(790, 630);
        dialog.setLocationRelativeTo(d);
        dialog.setLayout(new BorderLayout(10, 10));
        JLabel heading = new JLabel("  " + quiz.getTitle() + "  |  " + quiz.getCourseCode() + "  |  " + quiz.getDurationMinutes() + " min");
        heading.setFont(UITheme.FONT_HEADER);
        dialog.add(heading, BorderLayout.NORTH);
        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        List<ButtonGroup> groups = new ArrayList<>();
        int n = 1;
        for (QuizQuestion q : questions) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10), BorderFactory.createTitledBorder("Question " + n++ + " (" + q.getPoints() + " marks)")));
            JLabel question = new JLabel("<html><body style='width:590px'>" + escape(q.getQuestionText()) + "</body></html>");
            card.add(question, BorderLayout.NORTH);
            JPanel choices = new JPanel(new GridLayout(4, 1));
            ButtonGroup group = new ButtonGroup();
            String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
            for (int i = 0; i < 4; i++) {
                JRadioButton button = new JRadioButton((char)('A' + i) + ") " + opts[i]);
                button.setActionCommand(String.valueOf((char)('A' + i)));
                group.add(button); choices.add(button);
            }
            groups.add(group);
            card.add(choices, BorderLayout.CENTER);
            items.add(card);
        }
        JScrollPane scroll = new JScrollPane(items);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll, BorderLayout.CENTER);
        JButton submit = UITheme.createButton("Submit Answers", UITheme.SUCCESS_GREEN, Color.WHITE);
        dialog.add(submit, BorderLayout.SOUTH);
        submit.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(dialog, "Submit your answers?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) return;
            double score = 0, max = 0;
            int correct = 0;
            for (int i = 0; i < questions.size(); i++) {
                QuizQuestion q = questions.get(i);
                max += q.getPoints();
                ButtonModel selection = groups.get(i).getSelection();
                if (selection != null && selection.getActionCommand().charAt(0) == q.getCorrectOption()) {
                    score += q.getPoints(); correct++;
                }
            }
            double percentage = max > 0 ? 100 * score / max : 0;
            String status = IQuizDAO.isPassingScore(score, max) ? "PASS" : "FAIL";
            try {
                QuizAttempt record = new QuizAttempt(0, quiz.getQuizId(), d.student.getRegNo(), d.student.getFullName(), score, max, percentage, status, null);
                if (!dao.recordAttempt(record)) throw new QuizOperationException("Attempt was not saved.");
                dialog.dispose();
                JOptionPane.showMessageDialog(d, String.format("Answers submitted and saved!\nCorrect: %d / %d\nScore: %.1f / %.1f\nPercentage: %.1f%%\nResult: %s", correct, questions.size(), score, max, percentage, status), "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
            } catch (QuizOperationException ex) {
                JOptionPane.showMessageDialog(dialog, "Could not save attempt. Please retry.\n" + ex.getMessage(), "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
