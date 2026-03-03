package javafx;

public class CalculatorModel {
	private float calculationValue;

	public void calculate(float firstNumber, float secondNumber, char operator) {
		switch (operator) {
			case '+':
				calculationValue = firstNumber + secondNumber;
				break;
			case '-':
				calculationValue = firstNumber - secondNumber;
				break;
			case '*':
				calculationValue = firstNumber * secondNumber;
				break;
			case '/':
				if (secondNumber != 0) {
					calculationValue = firstNumber / secondNumber;
				} else {
					throw new ArithmeticException("Cannot divide by zero");
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid operator");
		}
	}

	public float getCalculationValue() {
		return calculationValue;
	}
}