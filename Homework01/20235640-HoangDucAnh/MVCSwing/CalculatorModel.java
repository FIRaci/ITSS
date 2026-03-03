package MVCSwing;
public class CalculatorModel {

    public double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b != 0) return a / b;
                else throw new ArithmeticException("Cannot divide by 0");
            default: return 0;
        }
    }
}