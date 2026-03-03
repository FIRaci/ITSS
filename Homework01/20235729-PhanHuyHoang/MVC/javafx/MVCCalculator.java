package javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MVCCalculator extends Application {

	@Override
	public void start(Stage stage) {
		CalculatorModel model = new CalculatorModel();
		CalculatorView view = new CalculatorView();
		new CalculatorController(model, view);

		Scene scene = new Scene(view.getView(), 420, 200);
		stage.setTitle("MVC Calculator (JavaFX)");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
