
package gui;

import dao.MedicalDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicalPanel extends JPanel {

    private final MedicalDAO dao = new MedicalDAO();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Medical ID", "Student", "Date",
                            "Reason", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);

    public MedicalPanel() {
        setLayout(new BorderLayout(10, 10));

        JButton refreshButton = new JButton("Refresh Requests");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        refreshButton.addActionListener(e -> loadRequests());
        approveButton.addActionListener(e -> updateStatus("APPROVED"));
        rejectButton.addActionListener(e -> updateStatus("REJECTED"));

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(refreshButton);
        buttons.add(approveButton);
        buttons.add(rejectButton);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadRequests();
    }

    private void loadRequests() {
        try {
            List<String[]> requests = dao.getAllMedicalRequests();
            tableModel.setRowCount(0);

            for (String[] request : requests) {
                tableModel.addRow(request);
            }

        } catch (SQLException ex) {
            showMessage("Could not load medical requests: "
                    + ex.getMessage());
        }
    }

    private void updateStatus(String status) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            showMessage("Select a medical request first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Change the selected request to " + status + "?",
                "Confirm Status Change",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int medicalId = Integer.parseInt(
                    tableModel.getValueAt(selectedRow, 0).toString());

            if (dao.updateMedicalStatus(medicalId, status)) {
                showMessage("Medical request updated.");
                loadRequests();
            } else {
                showMessage("No request was updated.");
            }

        } catch (SQLException ex) {
            showMessage("Could not update request: " + ex.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
