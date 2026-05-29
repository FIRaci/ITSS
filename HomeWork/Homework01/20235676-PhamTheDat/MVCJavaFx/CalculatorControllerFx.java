package MVCJavaFx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CalculatorControllerFx {
    
    private CalculatorViewFx view;
    private CalculatorModelFx model;
    
    public CalculatorControllerFx(CalculatorViewFx view, CalculatorModelFx model) {
        this.view = view;
        this.model = model;
        
        // Gắn sự kiện cho các nút
        initializeHandlers();
    }
    
    private void initializeHandlers() {
        // Sự kiện nút cộng
        view.getAddButton().setOnAction(e -> handleAdd());
        
        // Sự kiện nút trừ
        view.getSubtractButton().setOnAction(e -> handleSubtract());
        
        // Sự kiện nút nhân
        view.getMultiplyButton().setOnAction(e -> handleMultiply());
        
        // Sự kiện nút chia
        view.getDivideButton().setOnAction(e -> handleDivide());
        
        // Sự kiện nút xóa
        view.getClearButton().setOnAction(e -> handleClear());
    }
    
    private void handleAdd() {
        try {
            double num1 = Double.parseDouble(view.getFirstNumberField().getText());
            double num2 = Double.parseDouble(view.getSecondNumberField().getText());
            double result = model.add(num1, num2);
            view.getResultField().setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            showError("Vui lòng nhập số hợp lệ!");
        }
    }
    
    private void handleSubtract() {
        try {
            double num1 = Double.parseDouble(view.getFirstNumberField().getText());
            double num2 = Double.parseDouble(view.getSecondNumberField().getText());
            double result = model.subtract(num1, num2);
            view.getResultField().setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            showError("Vui lòng nhập số hợp lệ!");
        }
    }
    
    private void handleMultiply() {
        try {
            double num1 = Double.parseDouble(view.getFirstNumberField().getText());
            double num2 = Double.parseDouble(view.getSecondNumberField().getText());
            double result = model.multiply(num1, num2);
            view.getResultField().setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            showError("Vui lòng nhập số hợp lệ!");
        }
    }
    
    private void handleDivide() {
        try {
            double num1 = Double.parseDouble(view.getFirstNumberField().getText());
            double num2 = Double.parseDouble(view.getSecondNumberField().getText());
            double result = model.divide(num1, num2);
            view.getResultField().setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            showError("Vui lòng nhập số hợp lệ!");
        } catch (ArithmeticException e) {
            showError(e.getMessage());
        }
    }
    
    private void handleClear() {
        view.getFirstNumberField().clear();
        view.getSecondNumberField().clear();
        view.getResultField().clear();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
