package tg2088.exception;


public class LecturerOperationException extends Exception {


     //Default constructor

    public LecturerOperationException() {
        super("An error occurred during the Lecturer operation.");
    }


     // Parameterized constructor with custom error message

      //param message Detailed reason for the exception

    public LecturerOperationException(String message) {
        // Calls the superclass (Exception) constructor using 'super' keyword (Lesson 03 Part 01, Slide 32)
        super(message);
    }


    public LecturerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
