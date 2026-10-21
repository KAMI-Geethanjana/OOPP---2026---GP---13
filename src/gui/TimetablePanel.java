
package gui;

import dao.TimetableDAO;
import model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TimetablePanel extends JPanel {

    private final TechnicalOfficer officer;
    private final TimetableDAO dao = new TimetableDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{
                    "ID",
                    "Department ID",
                    "Course Code",
                    "Day",
                    "Start Time",
                    "End Time",
                    "Venue",
                    "Session Type"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable timetableTable = new JTable(tableModel);

    public TimetablePanel(TechnicalOfficer officer) {
        this.officer = officer;

        setLayout(new BorderLayout(10, 10));

        JButton refreshButton = new JButton("Refresh Timetable");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(refreshButton);

        timetableTable.setRowHeight(28);
        timetableTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadTimetable());

        loadTimetable();
    }

    private void loadTimetable() {
        tableModel.setRowCount(0);

        try {
            List<String[]> records = dao.getAllTimetable();

            for (String[] record : records) {
                tableModel.addRow(record);
            }

            System.out.println(
                    "Loaded " + records.size()
                            + " timetable records for department "
                            + officer.getDepartmentId()
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load timetable:\n" + ex.getMessage(),
                    "Timetable Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
