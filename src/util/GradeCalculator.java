package util;

public class GradeCalculator {

    public static String calculateGrade(double totalMark) {
        if (totalMark >= 85) return "A+";
        if (totalMark >= 80) return "A";
        if (totalMark >= 75) return "A-";
        if (totalMark >= 70) return "B+";
        if (totalMark >= 65) return "B";
        if (totalMark >= 60) return "B-";
        if (totalMark >= 55) return "C+";
        if (totalMark >= 50) return "C";
        if (totalMark >= 45) return "C-";
        if (totalMark >= 40) return "D+";
        if (totalMark >= 35) return "D";
        return "E";
    }

    public static double calculateGPAPoints(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.00;
            case "A-": return 3.70;
            case "B+": return 3.30;
            case "B": return 3.00;
            case "B-": return 2.70;
            case "C+": return 2.30;
            case "C": return 2.00;
            case "C-": return 1.70;
            case "D+": return 1.30;
            case "D": return 1.00;
            default: return 0.00;
        }
    }
}