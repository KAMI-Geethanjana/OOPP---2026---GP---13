import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BMICalculatorApp extends JFrame {
    private JPanel panel1;
    private JRadioButton englishLbsInchesRadioButton;
    private JRadioButton metricKgMetersRadioButton;
    private JTextField textField2;
    private JTextField textField1;
    private JButton CALCULATEBMIButton;
    private JLabel resultLabel;

    public BMICalculatorApp() {
        setTitle("BMI Calculator");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        ButtonGroup group = new ButtonGroup();
        group.add(englishLbsInchesRadioButton);
        group.add(metricKgMetersRadioButton);
        englishLbsInchesRadioButton.setSelected(true);

        CALCULATEBMIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        try {
            double weight = Double.parseDouble(textField2.getText());
            double height = Double.parseDouble(textField1.getText());
            double bmi = 0;
            String category = "";

            if (englishLbsInchesRadioButton.isSelected()) {
                bmi = (weight * 703) / (height * height);
            } else if (metricKgMetersRadioButton.isSelected()) {
                bmi = weight / (height * height);
            }

            if (bmi < 18.5) {
                category = "Underweight";
            } else if (bmi >= 18.5 && bmi <= 24.9) {
                category = "Normal";
            } else if (bmi >= 25.0 && bmi <= 29.9) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            resultLabel.setText(String.format("Your BMI: %.2f  |  Category: %s", bmi, category));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BMICalculatorApp().setVisible(true);
            }
        });
    }
}