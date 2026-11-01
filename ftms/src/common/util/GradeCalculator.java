package common.util;

import common.exception.InvalidMarksException;


public class GradeCalculator {

    public static final double CA_ELIGIBILITY_THRESHOLD = 40.0;
    public static final double ATTENDANCE_ELIGIBILITY_THRESHOLD = 80.0;


    public static void validateMark(double mark, String componentName) throws InvalidMarksException {
        if (mark < 0.0 || mark > 100.0) {
            throw new InvalidMarksException(componentName + " mark must be between 0 and 100. Provided: " + mark);
        }
    }


    public static double calculateTotalMarks(double caMark, double finalExamMark) {
        // Standard distribution: 40% CA, 60% Final Exam
        return (caMark * 0.40) + (finalExamMark * 0.60);
    }


    public static double calculateTotalMarks(double caMark, double caWeight, double finalExamMark, double finalExamWeight) {
        return (caMark * (caWeight / 100.0)) + (finalExamMark * (finalExamWeight / 100.0));
    }


    public static boolean isCaEligible(double caMark) {
        return caMark >= CA_ELIGIBILITY_THRESHOLD;
    }


    public static boolean isExamEligible(double attendancePercentage, double caMark) {
        return attendancePercentage >= ATTENDANCE_ELIGIBILITY_THRESHOLD && caMark >= CA_ELIGIBILITY_THRESHOLD;
    }


    public static String determineGrade(double totalMarks, boolean isEligible, String attemptType) {
        if (!isEligible) {
            return "E"; // Not eligible candidates receive an E / fail
        }

        String rawGrade = getRawGrade(totalMarks);

        // If repeat candidate, grade is capped to C
        if ("REPEAT".equalsIgnoreCase(attemptType)) {
            return capGradeToC(rawGrade);
        }

        return rawGrade;
    }

    private static String getRawGrade(double marks) {
        if (marks >= 85.0) return "A+";
        if (marks >= 80.0) return "A";
        if (marks >= 75.0) return "A-";
        if (marks >= 70.0) return "B+";
        if (marks >= 65.0) return "B";
        if (marks >= 60.0) return "B-";
        if (marks >= 55.0) return "C+";
        if (marks >= 50.0) return "C";
        if (marks >= 45.0) return "C-";
        if (marks >= 40.0) return "D+";
        if (marks >= 35.0) return "D";
        return "E";
    }

    private static String capGradeToC(String grade) {
        switch (grade) {
            case "A+":
            case "A":
            case "A-":
            case "B+":
            case "B":
            case "B-":
            case "C+":
                return "C";
            default:
                return grade;
        }
    }


    public static double getGradePoint(String grade) {
        if (grade == null) return 0.00;
        switch (grade.trim().toUpperCase()) {
            case "A+":
            case "A":
                return 4.00;
            case "A-":
                return 3.70;
            case "B+":
                return 3.30;
            case "B":
                return 3.00;
            case "B-":
                return 2.70;
            case "C+":
                return 2.30;
            case "C":
                return 2.00;
            case "C-":
                return 1.70;
            case "D+":
                return 1.30;
            case "D":
                return 1.00;
            case "E":
            case "F":
            default:
                return 0.00;
        }
    }
}
