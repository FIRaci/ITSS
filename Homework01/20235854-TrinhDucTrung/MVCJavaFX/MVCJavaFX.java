import javafx.application.Application;
import javafx.stage.Stage;

public class MVCJavaFX extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		CalculatorViewFx theView = new CalculatorViewFx(primaryStage);
		CalculatorModelFx theModel = new CalculatorModelFx();
		CalculatorControllerFx theController = new CalculatorControllerFx(theView, theModel);
	}
}
