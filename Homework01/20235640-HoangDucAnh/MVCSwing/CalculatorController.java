package MVCSwing;

import java.awt.event.*;

public class CalculatorController {

    private CalculatorModel model;
    private CalculatorView view;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;

        view.btnCalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double a = Double.parseDouble(view.txtA.getText());
                    double b = Double.parseDouble(view.txtB.getText());
                    String op = (String) view.cbOperator.getSelectedItem();

                    double result = model.calculate(a, b, op);
                    view.lblResult.setText("Result: " + result);

                } catch (Exception ex) {
                    view.lblResult.setText("Error!");
                }
            }
        });
    }
}