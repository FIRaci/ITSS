package swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorController implements ActionListener {
    private CalculatorModel model;
    private CalculatorView view;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
        view.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            float firstNumber = Float.parseFloat(view.getFirstNumber());
            float secondNumber = Float.parseFloat(view.getSecondNumber());
            char operator = getOperator(e.getActionCommand());

            model.calculate(firstNumber, secondNumber, operator);
            view.setCalcSolution(String.valueOf(model.getCalculationValue()));
        } catch (NumberFormatException ex) {
            view.setCalcSolution("Error");
        } catch (ArithmeticException ex) {
            view.setCalcSolution("Error: Division by zero");
        } catch (IllegalArgumentException ex) {
            view.setCalcSolution("Error: Invalid operator");
        }
    }

    private char getOperator(String actionCommand) {
        switch (actionCommand) {
            case "+": return '+';
            case "-": return '-';
            case "*": return '*';
            case "/": return '/';
            default: throw new IllegalArgumentException("Invalid operator");
        }
    }
}