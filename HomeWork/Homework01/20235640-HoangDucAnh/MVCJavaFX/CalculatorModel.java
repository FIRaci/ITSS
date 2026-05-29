package MVCJavaFX;

public class CalculatorModel {

    public double calculate(int a, int b, String operator) {

        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("Divide by zero");
                }
                return (double) a / b;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }
}