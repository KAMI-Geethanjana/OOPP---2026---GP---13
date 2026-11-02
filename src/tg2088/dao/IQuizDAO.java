package tg2088.dao;

import tg2088.exception.QuizOperationException;
import tg2088.model.Quiz;
import tg2088.model.QuizAttempt;
import tg2088.model.QuizQuestion;

import java.util.List;


public interface IQuizDAO {


    int DEFAULT_QUIZ_DURATION = 30;          // 30 minutes default duration
    double PASSING_PERCENTAGE = 50.0;        // 50% pass mark threshold
    double DEFAULT_QUESTION_POINTS = 10.0;   // 10 points per question



    int createQuiz(Quiz quiz) throws QuizOperationException;


    boolean updateQuiz(Quiz quiz) throws QuizOperationException;


    boolean deleteQuiz(int quizId) throws QuizOperationException;


    Quiz getQuizById(int quizId);

    List<Quiz> getQuizzesByCourse(String courseCode);


    List<Quiz> getQuizzesByCourse(String courseCode, boolean activeOnly);


    List<Quiz> getQuizzesByLecturer(int lecturerId);


    boolean toggleQuizStatus(int quizId, boolean isActive) throws QuizOperationException;


    int addQuestion(QuizQuestion question) throws QuizOperationException;


    boolean updateQuestion(QuizQuestion question) throws QuizOperationException;


    boolean deleteQuestion(int questionId) throws QuizOperationException;


    List<QuizQuestion> getQuestionsByQuizId(int quizId);


    boolean recordAttempt(QuizAttempt attempt) throws QuizOperationException;


    List<QuizAttempt> getAttemptsByQuizId(int quizId);


    default void printQuizDAOStatus() {
        System.out.println("[IQuizDAO Contract] Quiz DAO operational and ready for transactions.");
    }


    static boolean isPassingScore(double score, double maxScore) {
        if (maxScore <= 0) return false;
        return ((score / maxScore) * 100.0) >= PASSING_PERCENTAGE;
    }
}
