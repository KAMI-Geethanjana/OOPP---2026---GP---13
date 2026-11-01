package common.model;

/**
 * Mark entity representing individual assessment components.
 */
public class Mark {
    private int markId;
    private int enrollmentId;
    private double caMark;
    private double finalExamMark;
    private double totalMark;
    private boolean eligible;

    public Mark() {}

    public Mark(int markId, int enrollmentId, double caMark, double finalExamMark, double totalMark, boolean eligible) {
        this.markId = markId;
        this.enrollmentId = enrollmentId;
        this.caMark = caMark;
        this.finalExamMark = finalExamMark;
        this.totalMark = totalMark;
        this.eligible = eligible;
    }

    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public double getCaMark() { return caMark; }
    public void setCaMark(double caMark) { this.caMark = caMark; }

    public double getFinalExamMark() { return finalExamMark; }
    public void setFinalExamMark(double finalExamMark) { this.finalExamMark = finalExamMark; }

    public double getTotalMark() { return totalMark; }
    public void setTotalMark(double totalMark) { this.totalMark = totalMark; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
}
