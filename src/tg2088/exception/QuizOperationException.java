package tg2088.exception;


public class QuizOperationException extends Exception {

    private static final long serialVersionUID = 1L;


     // Default constructor

    public QuizOperationException() {
        super("An error occurred during the Quiz operation.");
    }


     // Parameterized constructor with custom message (Lesson 05, Slide 50)

     // @param message Detailed reason for the exception

    public QuizOperationException(String message) {
        super(message);
    }


     //Parameterized constructor with custom message and root cause

     //@param message Detailed error description
     // @param cause Underlying Throwable cause (Lesson 05, Slide 22)

    public QuizOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
