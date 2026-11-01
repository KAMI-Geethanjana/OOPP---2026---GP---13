package tg2088.util;

import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ============================================================================
 * Lesson 06: Introduction to File Handling in Java
 * ============================================================================
 * Reference: Lesson 06, Slides 3-33
 * Demonstrates:
 * 1. Character Streams - Writing (Slides 12-18):
 *    File -> FileWriter -> BufferedWriter -> PrintWriter (flush, close)
 * 2. Character Streams - Reading (Slides 6-11):
 *    File -> FileReader -> BufferedReader (readLine != null, close)
 * 3. Binary Streams - Writing (Slides 27-33):
 *    File -> FileOutputStream -> BufferedOutputStream -> DataOutputStream
 * 4. Binary Streams - Reading (Slides 19-26):
 *    File -> FileInputStream -> BufferedInputStream -> DataInputStream
 * ============================================================================
 */
public class QuizFileHandler {

    // ========================================================================
    // 1. CHARACTER STREAMS - WRITING (Lesson 06, Slides 12-18)
    // Exports formatted Quiz Question Paper to a .txt file
    // ========================================================================
    public static boolean exportQuizPaper(Quiz quiz, List<QuizQuestion> questions, File targetFile, boolean includeAnswerKey) {
        if (quiz == null || questions == null || targetFile == null) {
            return false;
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;

        try {
            // Step 1: Create FileWriter (Slide 15)
            fw = new FileWriter(targetFile);
            // Step 2: Wrap with BufferedWriter for buffered efficiency (Slide 15)
            bw = new BufferedWriter(fw);
            // Step 3: Wrap with PrintWriter for formatted printing (Slide 15)
            out = new PrintWriter(bw);

            out.println("================================================================================");
            out.println("               FACULTY OF TECHNOLOGY - UNIVERSITY OF RUHUNA");
            out.println("                         ONLINE QUIZ ASSESSMENT");
            out.println("================================================================================");
            out.println("Course Code  : " + quiz.getCourseCode());
            out.println("Quiz Title   : " + quiz.getTitle());
            out.println("Duration     : " + quiz.getDurationMinutes() + " Minutes");
            out.println("Total Marks  : " + quiz.getTotalMarks());
            out.println("Total Items  : " + questions.size() + " Questions");
            out.println("Generated On : " + new Date());
            out.println("--------------------------------------------------------------------------------");
            out.println("Instructions : Choose the most appropriate single answer (A, B, C, or D).");
            out.println("================================================================================\n");

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

            if (includeAnswerKey) {
                out.println("================================================================================");
                out.println("                        CONFIDENTIAL - INSTRUCTOR ANSWER KEY");
                out.println("================================================================================");
                qNum = 1;
                for (QuizQuestion q : questions) {
                    out.printf("Question %-2d : Correct [%c] - %s%n", qNum, q.getCorrectOption(), q.getCorrectAnswerText());
                    qNum++;
                }
                out.println("================================================================================");
            }

            out.flush(); // Slide 18: Flush character stream buffer
            return true;
        } catch (IOException e) {
            System.err.println("Error writing quiz paper text file: " + e.getMessage());
            return false;
        } finally {
            // Slide 18: Close output stream resources safely
            if (out != null) {
                out.close();
            }
        }
    }

    // ========================================================================
    // 2. CHARACTER STREAMS - WRITING RESULTS SUMMARY (Lesson 06, Slides 12-18)
    // ========================================================================
    public static boolean exportQuizResultsReport(Quiz quiz, List<QuizAttempt> attempts, File targetFile) {
        if (quiz == null || attempts == null || targetFile == null) {
            return false;
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;

        try {
            fw = new FileWriter(targetFile);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            out.println("================================================================================");
            out.println("               FACULTY OF TECHNOLOGY - UNIVERSITY OF RUHUNA");
            out.println("                     QUIZ SUBMISSIONS & RESULTS REPORT");
            out.println("================================================================================");
            out.println("Course Code  : " + quiz.getCourseCode());
            out.println("Quiz Title   : " + quiz.getTitle());
            out.println("Total Marks  : " + quiz.getTotalMarks());
            out.println("Submissions  : " + attempts.size());
            out.println("Report Date  : " + new Date());
            out.println("--------------------------------------------------------------------------------");
            out.printf("%-12s | %-24s | %-10s | %-10s | %-10s | %-18s%n",
                    "Reg No", "Student Name", "Score", "Max", "Percentage", "Status");
            out.println("--------------------------------------------------------------------------------");

            for (QuizAttempt a : attempts) {
                out.printf("%-12s | %-24s | %-10.1f | %-10.1f | %-9.1f%% | %-10s%n",
                        a.getStudentRegNo(),
                        a.getStudentName(),
                        a.getScore(),
                        a.getMaxScore(),
                        a.getPercentage(),
                        a.getStatus());
            }
            out.println("--------------------------------------------------------------------------------");
            out.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error writing quiz results report: " + e.getMessage());
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // ========================================================================
    // 3. BINARY STREAMS - WRITING (Lesson 06, Slides 27-33)
    // Backs up Quiz Header and Questions into binary .dat file using DataOutputStream
    // ========================================================================
    public static boolean backupQuizToBinary(Quiz quiz, List<QuizQuestion> questions, File targetFile) {
        if (quiz == null || questions == null || targetFile == null) {
            return false;
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;

        try {
            // Slide 28: FileOutputStream -> BufferedOutputStream -> DataOutputStream
            fos = new FileOutputStream(targetFile);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);

            // Write Quiz Header (Slide 30: writeUTF, writeInt, writeDouble, writeBoolean)
            dos.writeUTF("FTMS_QUIZ_V1");
            dos.writeInt(quiz.getQuizId());
            dos.writeUTF(quiz.getCourseCode());
            dos.writeInt(quiz.getLecturerId());
            dos.writeUTF(quiz.getTitle());
            dos.writeUTF(quiz.getDescription() != null ? quiz.getDescription() : "");
            dos.writeInt(quiz.getDurationMinutes());
            dos.writeDouble(quiz.getTotalMarks());
            dos.writeBoolean(quiz.isActive());

            // Write Question Count & Records
            dos.writeInt(questions.size());
            for (QuizQuestion q : questions) {
                dos.writeInt(q.getQuestionId());
                dos.writeUTF(q.getQuestionText());
                dos.writeUTF(q.getOptionA());
                dos.writeUTF(q.getOptionB());
                dos.writeUTF(q.getOptionC());
                dos.writeUTF(q.getOptionD());
                dos.writeChar(q.getCorrectOption());
                dos.writeDouble(q.getPoints());
            }

            dos.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error creating binary quiz backup: " + e.getMessage());
            return false;
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException ignored) {}
            }
        }
    }

    // ========================================================================
    // 4. BINARY STREAMS - READING (Lesson 06, Slides 19-26)
    // Restores Quiz and Questions from binary .dat file using DataInputStream
    // ========================================================================
    public static List<QuizQuestion> readQuestionsFromBinary(File sourceFile, Quiz targetQuizHeader) {
        List<QuizQuestion> questions = new ArrayList<>();
        if (sourceFile == null || !sourceFile.exists()) {
            return questions;
        }

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            // Slide 21: FileInputStream -> BufferedInputStream -> DataInputStream
            fis = new FileInputStream(sourceFile);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            // Read magic header
            String header = dis.readUTF();
            if (!"FTMS_QUIZ_V1".equals(header)) {
                System.err.println("Unrecognized binary quiz format.");
                return questions;
            }

            int quizId = dis.readInt();
            String courseCode = dis.readUTF();
            int lecturerId = dis.readInt();
            String title = dis.readUTF();
            String description = dis.readUTF();
            int duration = dis.readInt();
            double totalMarks = dis.readDouble();
            boolean active = dis.readBoolean();

            if (targetQuizHeader != null) {
                targetQuizHeader.setQuizId(quizId);
                targetQuizHeader.setCourseCode(courseCode);
                targetQuizHeader.setLecturerId(lecturerId);
                targetQuizHeader.setTitle(title);
                targetQuizHeader.setDescription(description);
                targetQuizHeader.setDurationMinutes(duration);
                targetQuizHeader.setTotalMarks(totalMarks);
                targetQuizHeader.setActive(active);
            }

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                int qId = dis.readInt();
                String text = dis.readUTF();
                String optA = dis.readUTF();
                String optB = dis.readUTF();
                String optC = dis.readUTF();
                String optD = dis.readUTF();
                char correct = dis.readChar();
                double pts = dis.readDouble();

                QuizQuestion q = new QuizQuestion(qId, quizId, text, optA, optB, optC, optD, correct, pts);
                questions.add(q);
            }
        } catch (IOException e) {
            System.err.println("Error reading binary quiz backup: " + e.getMessage());
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ignored) {}
            }
        }
        return questions;
    }
}
