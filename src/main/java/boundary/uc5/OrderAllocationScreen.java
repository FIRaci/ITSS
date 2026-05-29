package boundary.uc5;

import controller.uc5.ProcessRequestController;
import entity.chung.AllocationRow;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.List;

public class OrderAllocationScreen {
    private Stage stage;
    private ProcessRequestController controller;
    private TextField txtRequestId;
    private TableView<AllocationRow> planTable;

    public OrderAllocationScreen(Stage stage) {
        this.stage = stage;
        this.controller = new ProcessRequestController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        txtRequestId = new TextField();
        txtRequestId.setPromptText("Mã yêu cầu (VD: YCNH-20250510-001)");

        Button btnCalculate = new Button("Tính toán kế hoạch");
        btnCalculate.setOnAction(e -> calculate());

        planTable = new TableView<>();
        TableColumn<AllocationRow, String> colMc = new TableColumn<>("Mặt hàng");
        colMc.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<AllocationRow, String> colSite = new TableColumn<>("Site");
        colSite.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<AllocationRow, Integer> colQty = new TableColumn<>("SL");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<AllocationRow, String> colShip = new TableColumn<>("PTVC");
        colShip.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        planTable.getColumns().addAll(colMc, colSite, colQty, colShip);

        Button btnSubmit = new Button("Gửi đơn hàng");
        btnSubmit.setOnAction(e -> submit());

        HBox top = new HBox(5, txtRequestId, btnCalculate);
        root.getChildren().addAll(
            new Label("PHÂN BỔ ĐƠN HÀNG"),
            top, planTable, btnSubmit
        );

        stage.setScene(new Scene(root, 700, 450));
        stage.setTitle("UC5 - Phân bổ đơn hàng");
        stage.show();
    }

    private void calculate() {
        try {
            List<AllocationRow> plan = controller.calculatePlan(txtRequestId.getText());
            planTable.getItems().setAll(plan);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }

    private void submit() {
        try {
            controller.submitOrders(txtRequestId.getText(), planTable.getItems());
            new Alert(Alert.AlertType.INFORMATION, "Đã gửi đơn hàng!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }
}
