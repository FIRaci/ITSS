import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class CalculatorViewFx {
	private TextField firstNumber = new TextField();
	private TextField secondNumber = new TextField();
	private TextField calcSolution = new TextField();
	private Button calculateButton = new Button("Tính toán");
	
	public HBox getView() {
		firstNumber.setPromptText("Số đầu tiên");
		secondNumber.setPromptText("Số thứ hai");
		calcSolution.setPromptText("Kết quả");
		calcSolution.setEditable(false);
		
		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(firstNumber, new Label("+"), secondNumber, calculateButton, calcSolution);
		hbox.setStyle("-fx-padding: 20; -fx-alignment: center;");
		
		return hbox;
	}
	
	public int getFirstNumber(){
		return Integer.parseInt(firstNumber.getText());
	}
	
	public int getSecondNumber(){
		return Integer.parseInt(secondNumber.getText());
	}
	
	public void setCalcSolution(int solution){
		calcSolution.setText(Integer.toString(solution));
	}
	
	public Button getCalculateButton(){
		return calculateButton;
	}
	
	public void displayErrorMessage(String errorMessage){
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Lỗi");
		alert.setHeaderText(null);
		alert.setContentText(errorMessage);
		alert.showAndWait();
	}
}
