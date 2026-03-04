package MVCJavaFX;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CalculatorController {

    @FXML
    private TextField txtA;

    @FXML
    private TextField txtB;

    @FXML
    private ComboBox<String> cbOperator;

    @FXML
    private Label lblResult;

    private CalculatorModel model = new CalculatorModel();

    @FXML
    public void initialize() {
        cbOperator.getItems().addAll("+", "-", "*", "/");
        cbOperator.setValue("+");
    }

    @FXML
    private void handleCalculate() {

        try {
            int a = Integer.parseInt(txtA.getText());
            int b = Integer.parseInt(txtB.getText());
            String op = cbOperator.getValue();

            double result = model.calculate(a, b, op);
            lblResult.setText("Result: " + result);

        } catch (NumberFormatException e) {
            lblResult.setText("You need to enter 2 integers");
        } catch (ArithmeticException e) {
            lblResult.setText("Cannot divide by zero");
        } catch (Exception e) {
            lblResult.setText("Error!");
        }
    }
}