package MVCJavaFx;

public class CalculatorModelFx {
    
    private double result;
    
    // Phép cộng
    public double add(double num1, double num2) {
        result = num1 + num2;
        return result;
    }
    
    // Phép trừ
    public double subtract(double num1, double num2) {
        result = num1 - num2;
        return result;
    }
    
    // Phép nhân
    public double multiply(double num1, double num2) {
        result = num1 * num2;
        return result;
    }
    
    // Phép chia
    public double divide(double num1, double num2) throws ArithmeticException {
        if (num2 == 0) {
            throw new ArithmeticException("Không thể chia cho 0");
        }
        result = num1 / num2;
        return result;
    }
    
    public double getResult() {
        return result;
    }
}
