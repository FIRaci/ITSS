package MVCJavaFx;

import javafx.application.Application;
import javafx.stage.Stage;

public class MVCJavaFX extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Tạo Model
            CalculatorModelFx model = new CalculatorModelFx();
            
            // Tạo View
            CalculatorViewFx view = new CalculatorViewFx(primaryStage);
            
            // Tạo Controller và kết nối View với Model
            CalculatorControllerFx controller = new CalculatorControllerFx(view, model);
            
            // Hiển thị giao diện
            view.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
