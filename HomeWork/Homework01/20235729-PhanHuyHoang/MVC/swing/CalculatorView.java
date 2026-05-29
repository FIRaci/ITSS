package swing;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class CalculatorView extends JFrame {
    private JTextField firstNumber, secondNumber, calcSolution;
    private JButton addButton, subButton, mulButton, divButton;

    public CalculatorView() {
        setTitle("MVC Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);

        firstNumber = new JTextField(10);
        secondNumber = new JTextField(10);
        calcSolution = new JTextField(10);
        calcSolution.setEditable(false);

        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");

        JPanel panel = new JPanel(new BorderLayout(10, 0));

        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.add(new JLabel("First Number:"));
        infoPanel.add(firstNumber);
        infoPanel.add(new JLabel("Second Number:"));
        infoPanel.add(secondNumber);
        infoPanel.add(new JLabel("Result:"));
        infoPanel.add(calcSolution);

        JPanel operandPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        operandPanel.add(addButton);
        operandPanel.add(subButton);
        operandPanel.add(mulButton);
        operandPanel.add(divButton);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(operandPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
    }

    public void addActionListener(ActionListener listener) {
        addButton.addActionListener(listener);
        subButton.addActionListener(listener);
        mulButton.addActionListener(listener);
        divButton.addActionListener(listener);
    }

    public String getFirstNumber() {
        return firstNumber.getText();
    }

    public String getSecondNumber() {
        return secondNumber.getText();
    }

    public void setCalcSolution(String solution) {
        calcSolution.setText(solution);
    }

}
