package boundary.uc5;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class OverseasDashboard {
    private Stage stage;

    public OverseasDashboard(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Button btnAllocate = new Button("Phân bổ đơn hàng");
        btnAllocate.setOnAction(e -> new OrderAllocationScreen(new Stage()).show());

        Button btnCancel = new Button("Xử lý hủy đơn");
        btnCancel.setOnAction(e -> new boundary.uc4.CancellationScreen(new Stage()).show());

        root.getChildren().addAll(
            new Label("OVERSEAS DASHBOARD"),
            btnAllocate, btnCancel
        );

        stage.setScene(new Scene(root, 400, 250));
        stage.setTitle("UC5 - Overseas Dashboard");
        stage.show();
    }
}
