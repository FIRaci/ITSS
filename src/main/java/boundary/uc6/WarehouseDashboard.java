package boundary.uc6;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class WarehouseDashboard {
    private Stage stage;

    public WarehouseDashboard(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Button btnInbound = new Button("Kiểm tra nhập kho");
        btnInbound.setOnAction(e -> new InboundVerificationScreen(new Stage()).show());

        Button btnDiscrepancy = new Button("Báo cáo sai lệch");
        btnDiscrepancy.setOnAction(e -> new DiscrepancyReportScreen(new Stage()).show());

        root.getChildren().addAll(
            new Label("WAREHOUSE DASHBOARD"),
            btnInbound, btnDiscrepancy
        );

        stage.setScene(new Scene(root, 400, 200));
        stage.setTitle("UC6 - Warehouse Dashboard");
        stage.show();
    }
}
