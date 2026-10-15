package gui;

import dao.UndergrduateDAO;
import model.Undergraduate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UndergraduateDashboard extends JFrame {
    private Undergraduate student;
    private UndergrduateDAO studentDAO;
    private NoticeDAO noticeDAO;

    private JPanel contentCards;
    private CardLayout cardLayout;

    private JButton btnNavProfile;
    private JButton btnNavDashboard;
    private JButton btnNavCourses;
    private JButton btnNavAttendance;
    private JButton btnNavMedical;
    private JButton btnNavGrades;
    private JButton btnNavNotice;
    private JButton btnNavTimetable;

   private JLabel lblWelcomeName;
   private JLabel lblRegNo;

   private JLabel lblGpaVal;
   private JLabel lblCourseVal;
   private JLabel lblAttendanceVal;
   private JLabel lblMedicalVal;

    private JTable tblAttendance;
    private DefaultTableModel attModel;

    private JLabel tblMedical;
    private DefaultTableModel medModel;
    private JComboBox<String> cbMedCourse;
    private JTextField txtMedDate;
    private JTextArea txtMedReason;
    private JTextField txtMedPath;

    private JTable tblCourses;
    private DefaultTableModel courseModel;
    private JTable tblMaterials;
    private DefaultTableModel materialsModel;

    private JTable tblGrades;
    private DefaultTableModel gradesModel;
    private JLabel lblSgpa;


    private JTable tblTimetable;
    private DefaultTableModel timetableModel;

private JTable tblNotices;
private DefaultTableModel noticesModel;

private JTextField txtProfEmail, txtProfPhone;
private JLabel lblProfRegNo, lblProfBatch, lblProfDept, lblProfStatus;

    public UndergraduateDashboard(Undergraduate student) {
        this.student = student;
        this.studentDAO = new UndergrduateDAO();
        this.noticeDAO = new NoticeDAO();

        if (student != null && student.getDepartmentName() == null) {
            Undergraduate fullStu = studentDAO.getUndergraduateByUserId(student.getUserId());
            if (fullStu != null) {
                this.student = fullStu;
            }
        }

        initUI();
    }




}
