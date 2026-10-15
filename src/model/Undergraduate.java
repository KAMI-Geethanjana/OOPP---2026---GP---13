package model;

public class Undergraduate extends User {
    private int studentId;
    private String regNo;
    private int batchYear;
    private String status;
    private int departmentId;
    private String departmentName;
    private String contactNo;


    public Undergraduate() {
        super();
        setRole("UNDERGRADUATE");
    }

    public Undergraduate(int userId, String username,String passwordHash,String fullname,
                         String email, String phoneNo, int studentId, String regNo, int batchYear, String status, int departmentId) {
        super(userId,username,passwordHash,fullname,email,phoneNo, "UNDERGRADUATE");
        this.studentId = studentId;
        this.regNo = regNo;
        this.batchYear = batchYear;
        this.status = status;
        this.departmentId = departmentId;


    }

    public String getRoleDisplayName(){
        return "Undergraduate Student";
    }

    public void openDashboard(JFrame parent){

    }



}
