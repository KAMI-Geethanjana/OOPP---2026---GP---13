package util;

public class GradeCalc {

    // Final Marks (CA + Final Exam) ගණනය කිරීම
    public static double calcTotalMarks(double caMarks, double caWeight, double finalExamMarks, double finalExamWeight) {
        return (caMarks * (caWeight / 100.0)) + (finalExamMarks * (finalExamWeight / 100.0));
    }

    // CA Eligibility පරීක්ෂා කිරීම (CA සඳහා අවම වශයෙන් 40% ක් තිබිය යුතුය)
    public static boolean isCaEligible(double caMarks) {
        return caMarks >= 40.0;
    }

    // UGC Circular 12-2024 අනුව Grade එක තීරණය කිරීම
    public static String getGrade(double totalMarks, boolean caEligible) {
        if (!caEligible) {
            return "F"; // CA Ineligible නම් සෘජුවම F ශ්‍රේණිය ලබා දෙයි
        }

        if (totalMarks >= 85) return "A+";
        if (totalMarks >= 80) return "A";
        if (totalMarks >= 75) return "A-";
        if (totalMarks >= 70) return "B+";
        if (totalMarks >= 65) return "B";
        if (totalMarks >= 60) return "B-";
        if (totalMarks >= 55) return "C+";
        if (totalMarks >= 50) return "C";
        if (totalMarks >= 45) return "C-";
        if (totalMarks >= 40) return "D+";
        if (totalMarks >= 35) return "D";
        return "F";
    }

    // Grade එකට අදාළ Grade Point Value (GPV) එක ලබා ගැනීම
    public static double getGpv(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.00;
            case "A-": return 3.70;
            case "B+": return 3.30;
            case "B":  return 3.00;
            case "B-": return 2.70;
            case "C+": return 2.30;
            case "C":  return 2.00;
            case "C-": return 1.70;
            case "D+": return 1.30;
            case "D":  return 1.00;
            default:   return 0.00;
        }
    }
}