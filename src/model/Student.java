package model;

public class Student {
    private int studentId;
    private String indexNo;
    private String name;

    public Student(int studentId, String indexNo, String name) {
        this.studentId = studentId;
        this.indexNo = indexNo;
        this.name = name;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public String getName() {
        return name;
    }
}