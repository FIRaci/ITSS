package javafx;

public class CalculatorController {
	private CalculatorModel model;
	private CalculatorView view;

	public CalculatorController(CalculatorModel model, CalculatorView view) {
		this.model = model;
		this.view = view;

		view.addButtonListener(
			() -> calculate('+'),
			() -> calculate('-'),
			() -> calculate('*'),
			() -> calculate('/'));
	}

	private void calculate(char operator) {
		try {
			float firstNumber = Float.parseFloat(view.getFirstNumber());
			float secondNumber = Float.parseFloat(view.getSecondNumber());

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

}
