import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MVCJavaFX extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		CalculatorViewFx theView = new CalculatorViewFx();
		CalculatorModelFx theModel = new CalculatorModelFx();
		CalculatorControllerFx theController = new CalculatorControllerFx(theView, theModel);
		
		VBox root = new VBox(20);
		root.getChildren().add(theView.getView());
		root.setStyle("-fx-padding: 20;");
		
		Scene scene = new Scene(root, 900, 150);
		primaryStage.setTitle("Calculator JavaFX");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
