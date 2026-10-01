import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame {
    // Your exact auto-generated visual component variable parameters
    private JRadioButton metricUnitsKgCmRadioButton;
    private JRadioButton englishUnitsLbsInRadioButton;
    private JTextField txtWeight;
    private JTextField txtHeight;
    private JButton btnCalculate;
    private JButton btnclear;
    private JTextField txtBMIResult;
    private JTextField txtStatus;

    // Links explicitly to your custom designer root canvas panel named in Step 1
    private JPanel mainPanel;

    public Calculator() {
        // Base window layout frame setup configurations
        setTitle("Body Mass Index (BMI) Calculator");

        // Binds your exact custom designed visual form workspace directly onto the screen
        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 480); // Adjusted dimensions matching your grid proportions nicely
        setLocationRelativeTo(null); // Displays the framework perfectly in the center of your screen

        // 1. Calculate Action Button Interaction Listener
        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performBMICalculation();
            }
        });

        // 2. Clear Action Button Interaction Listener
        btnclear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFormFields();
            }
        });
    }

    // Mathematical Core Calculation Processing Engine
    private void performBMICalculation() {
        try {
            double weight = Double.parseDouble(txtWeight.getText().trim());
            double height = Double.parseDouble(txtHeight.getText().trim());

            // Positive metric validation guard checks
            if (weight <= 0 || height <= 0) {
                JOptionPane.showMessageDialog(this, "Measurements must be positive numbers greater than zero.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double bmi = 0;

            // Route standard system formula calculation methods based on selected unit profile toggles [INDEX: 0.1.1]
            if (metricUnitsKgCmRadioButton.isSelected()) {
                // Metric Standard Formula: weight (kg) / [height (m)]^2
                double heightInMeters = height / 100.0;
                bmi = weight / (heightInMeters * heightInMeters);
            } else {
                // English Standard Formula: (weight (lbs) * 703) / [height (in)]^2
                bmi = (weight * 703) / (height * height);
            }

            // Print rounded string variable data tokens into visual result output box fields
            txtBMIResult.setText(String.format("%.1f", bmi));

            // Run color theme transformations matching official NIH guideline classifications [INDEX: 0.1.1]
            updateClassificationUI(bmi);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid characters detected. Please input numbers only.", "Data Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Updates text content layouts cleanly based on target NIH standard metrics [INDEX: 0.1.1]
    private void updateClassificationUI(double bmi) {
        String status;
        Color colorBg;

        if (bmi < 18.5) {
            status = "Underweight";
            colorBg = new Color(210, 235, 245); // Soft Pastel Blue
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            status = "Normal";
            colorBg = new Color(220, 245, 220); // Soft Pastel Green
        } else if (bmi >= 25.0 && bmi <= 29.9) {
            status = "Overweight";
            colorBg = new Color(255, 240, 210); // Soft Pastel Orange
        } else {
            status = "Obese";
            colorBg = new Color(255, 215, 215); // Soft Pastel Red
        }

        txtStatus.setText(status);
        txtStatus.setBackground(colorBg); // Dynamically applies color styles onto classification text view background
    }

    // Flush visual state values from user entry scopes
    private void resetFormFields() {
        txtWeight.setText("");
        txtHeight.setText("");
        txtBMIResult.setText("");
        txtStatus.setText("");
        txtStatus.setBackground(UIManager.getColor("TextField.background")); // Reverts look back to standard background color template
    }

    // Static Main Executable Program Master Run Thread Link
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Calculator().setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
