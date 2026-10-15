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

   private JLabel IblWelcomeName;
   private JLabel IblRegNo;

   private JLabel IblGpaVal;
   private JLabel IblCourseVal;
   private JLabel IblAttendanceVal;
   private JLabel IblMedicalVal;

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

    private JTable tblTimetable;
    private DefaultTableModel timetableModel;





}
