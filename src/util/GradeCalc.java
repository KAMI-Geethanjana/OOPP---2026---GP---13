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

    // UGC Circular 12-2024 අනුව Grade එක තීරණය කිරීම (Attempt Type සහ CA Eligibility සහිතව)
    public static String getGrade(double totalMarks, boolean caEligible, String attemptType) {
        // CA Ineligible නම් සෘජුවම E (Ineligible) ලබා දෙයි
        if (!caEligible) {
            return "E";
        }

        String rawGrade = calculateRawGrade(totalMarks);

        // Repeat Attempt එකක් නම් සහ Approved Medical නැත්නම් Grade එක Maximum 'C' දක්වා Capping වේ
        if ("REPEAT".equalsIgnoreCase(attemptType)) {
            return capGradeToC(rawGrade);
        }

        // PROPER (First Attempt) හෝ MEDICAL සඳහා සාමාන්‍ය Grade එක ලබා දෙයි
        return rawGrade;
    }

    // Overloaded method (පරණ Code වල Compatibility එක සඳහා)
    public static String getGrade(double totalMarks, boolean caEligible) {
        return getGrade(totalMarks, caEligible, "PROPER");
    }

    // Raw Grade ගණනය කිරීම
    private static String calculateRawGrade(double totalMarks) {
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

    // Repeat ළමුන්ගේ Grade එක Maximum C වලට Capping කිරීම
    private static String capGradeToC(String grade) {
        switch (grade) {
            case "A+": case "A": case "A-":
            case "B+": case "B": case "B-":
            case "C+":
                return "C";
            default:
                return grade;
        }
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