package tg2088.util;

import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.io.*;
import java.util.Date;
import java.util.List;

public class QuizFileHandler {

    public static boolean exportQuizPaper(Quiz quiz, List<QuizQuestion> questions, File targetFile, boolean includeAnswerKey) {
        if (quiz == null || questions == null || targetFile == null) return false;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)))) {
            out.println("================================================================================");
            out.println("               FACULTY OF TECHNOLOGY - UNIVERSITY OF RUHUNA");
            out.println("                         ONLINE QUIZ ASSESSMENT");
            out.println("================================================================================");
            out.println("Course Code  : " + quiz.getCourseCode());
            out.println("Quiz Title   : " + quiz.getTitle());
            out.println("Duration     : " + quiz.getDurationMinutes() + " Minutes");
            out.println("Total Marks  : " + quiz.getTotalMarks());
            out.println("Total Items  : " + questions.size() + " Questions");
            out.println("--------------------------------------------------------------------------------\n");
            int qNum = 1;
            for (QuizQuestion q : questions) {
                out.println(qNum + ". " + q.getQuestionText() + " [" + q.getPoints() + " marks]");
                out.println("   [A] " + q.getOptionA());
                out.println("   [B] " + q.getOptionB());
                out.println("   [C] " + q.getOptionC());
                out.println("   [D] " + q.getOptionD());
                out.println();
                qNum++;
            }
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean exportQuizResultsReport(Quiz quiz, List<QuizAttempt> attempts, File targetFile) {
        if (quiz == null || attempts == null || targetFile == null) return false;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)))) {
            out.println("================================================================================");
            out.println("                     QUIZ SUBMISSIONS REPORT: " + quiz.getTitle());
            out.println("================================================================================");
            for (QuizAttempt a : attempts) {
                out.printf("%-12s | %-24s | %-10.1f | %-10s%n", a.getStudentRegNo(), a.getStudentName(), a.getScore(), a.getStatus());
            }
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
