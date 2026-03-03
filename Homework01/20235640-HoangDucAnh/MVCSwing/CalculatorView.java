package MVCSwing;
import javax.swing.*;
import java.awt.*;

public class CalculatorView extends JFrame {

    JTextField txtA = new JTextField(5);
    JTextField txtB = new JTextField(5);
    JComboBox<String> cbOperator = new JComboBox<>(new String[]{"+", "-", "*", "/"});
    JButton btnCalculate = new JButton("Calculate");
    JLabel lblResult = new JLabel("Result: ");

    public CalculatorView() {
        setTitle("Calculator - Swing MVC");
        setLayout(new FlowLayout());
        setSize(300,150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(txtA);
        add(cbOperator);
        add(txtB);
        add(btnCalculate);
        add(lblResult);

        setVisible(true);
    }
}