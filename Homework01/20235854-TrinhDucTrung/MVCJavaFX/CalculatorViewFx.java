import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CalculatorViewFx {
	private TextField firstNumber = new TextField();
	private TextField secondNumber = new TextField();
	private Button calculateButton = new Button("Calculate");
	private TextField calcSolution = new TextField();
	
	public CalculatorViewFx(Stage primaryStage) {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);
		
		Label firstLabel = new Label("First Number:");
		GridPane.setConstraints(firstLabel, 0, 0);
		GridPane.setConstraints(firstNumber, 1, 0);
		
		Label secondLabel = new Label("Second Number:");
		GridPane.setConstraints(secondLabel, 0, 1);
		GridPane.setConstraints(secondNumber, 1, 1);
		
		GridPane.setConstraints(calculateButton, 1, 2);
		
		Label solutionLabel = new Label("Solution:");
		GridPane.setConstraints(solutionLabel, 0, 3);
		GridPane.setConstraints(calcSolution, 1, 3);
		
		grid.getChildren().addAll(firstLabel, firstNumber, secondLabel, secondNumber, 
								  calculateButton, solutionLabel, calcSolution);
		
		Scene scene = new Scene(grid, 300, 200);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Calculator");
		primaryStage.show();
	}
	
	public int getFirstNumber() {
		return Integer.parseInt(firstNumber.getText());
	}
	
	public int getSecondNumber() {
		return Integer.parseInt(secondNumber.getText());
	}
	
	public void setCalcSolution(int solution) {
		calcSolution.setText(Integer.toString(solution));
	}
	
	public Button getCalculateButton() {
		return calculateButton;
	}
	
	public void displayErrorMessage(String errorMessage) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(errorMessage);
		alert.showAndWait();
	}
}
