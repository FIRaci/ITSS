package MVCJavaFx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalculatorViewFx {
    
    private Stage stage;
    private TextField firstNumberField;
    private TextField secondNumberField;
    private TextField resultField;
    private Button addButton;
    private Button subtractButton;
    private Button multiplyButton;
    private Button divideButton;
    private Button clearButton;
    
    public CalculatorViewFx(Stage stage) {
        this.stage = stage;
        initialize();
    }
    
    private void initialize() {
        // Tạo các thành phần giao diện
        Label titleLabel = new Label("Máy Tính Đơn Giản");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // GridPane cho các trường nhập liệu
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setAlignment(Pos.CENTER);
        
        Label label1 = new Label("Số thứ nhất:");
        firstNumberField = new TextField();
        firstNumberField.setPromptText("Nhập số thứ nhất");
        firstNumberField.setPrefWidth(200);
        
        Label label2 = new Label("Số thứ hai:");
        secondNumberField = new TextField();
        secondNumberField.setPromptText("Nhập số thứ hai");
        secondNumberField.setPrefWidth(200);
        
        inputGrid.add(label1, 0, 0);
        inputGrid.add(firstNumberField, 1, 0);
        inputGrid.add(label2, 0, 1);
        inputGrid.add(secondNumberField, 1, 1);
        
        // HBox cho các nút phép tính
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        addButton = new Button("+");
        subtractButton = new Button("-");
        multiplyButton = new Button("×");
        divideButton = new Button("÷");
        
        addButton.setPrefWidth(60);
        subtractButton.setPrefWidth(60);
        multiplyButton.setPrefWidth(60);
        divideButton.setPrefWidth(60);
        
        addButton.setStyle("-fx-font-size: 16px;");
        subtractButton.setStyle("-fx-font-size: 16px;");
        multiplyButton.setStyle("-fx-font-size: 16px;");
        divideButton.setStyle("-fx-font-size: 16px;");
        
        buttonBox.getChildren().addAll(addButton, subtractButton, multiplyButton, divideButton);
        
        // HBox cho kết quả
        HBox resultBox = new HBox(10);
        resultBox.setAlignment(Pos.CENTER);
        
        Label resultLabel = new Label("Kết quả:");
        resultLabel.setStyle("-fx-font-weight: bold;");
        
        resultField = new TextField();
        resultField.setEditable(false);
        resultField.setPrefWidth(200);
        resultField.setStyle("-fx-background-color: #f0f0f0;");
        
        resultBox.getChildren().addAll(resultLabel, resultField);
        
        // Nút xóa
        clearButton = new Button("Xóa");
        clearButton.setPrefWidth(100);
        
        // VBox chứa tất cả
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(titleLabel, inputGrid, buttonBox, resultBox, clearButton);
        
        // Tạo Scene và thiết lập Stage
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Máy Tính MVC JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);
    }
    
    // Getter methods
    public TextField getFirstNumberField() {
        return firstNumberField;
    }
    
    public TextField getSecondNumberField() {
        return secondNumberField;
    }
    
    public TextField getResultField() {
        return resultField;
    }
    
    public Button getAddButton() {
        return addButton;
    }
    
    public Button getSubtractButton() {
        return subtractButton;
    }
    
    public Button getMultiplyButton() {
        return multiplyButton;
    }
    
    public Button getDivideButton() {
        return divideButton;
    }
    
    public Button getClearButton() {
        return clearButton;
    }
    
    public void show() {
        stage.show();
    }
}
