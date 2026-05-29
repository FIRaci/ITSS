package boundary.uc6;

import controller.uc6.InboundController;
import entity.uc6.InboundRecord;
import entity.chung.InternationalOrder;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class InboundVerificationScreen {
    private Stage stage;
    private InboundController controller;
    private TableView<InternationalOrder> orderTable;

    public InboundVerificationScreen(Stage stage) {
        this.stage = stage;
        this.controller = new InboundController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        orderTable = new TableView<>();
        TableColumn<InternationalOrder, Integer> colId = new TableColumn<>("Mã đơn");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> colMc = new TableColumn<>("Mặt hàng");
        colMc.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> colQty = new TableColumn<>("SL");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTable.getColumns().addAll(colId, colMc, colQty, colStatus);

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.setOnAction(e -> loadData());

        Button btnConfirm = new Button("Xác nhận nhập kho");
        btnConfirm.setOnAction(e -> confirmInbound());

        HBox actions = new HBox(10, btnRefresh, btnConfirm);
        root.getChildren().addAll(
            new Label("KIỂM TRA NHẬP KHO"),
            orderTable, actions
        );

        stage.setScene(new Scene(root, 650, 400));
        stage.setTitle("UC6 - Kiểm tra nhập kho");
        stage.show();
    }

    private void loadData() {
        orderTable.getItems().setAll(controller.getIncomingOrders());
    }

    private void confirmInbound() {
        InternationalOrder sel = orderTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        TextInputDialog dialog = new TextInputDialog(String.valueOf(sel.getQty()));
        dialog.setTitle("Nhập kho");
        dialog.setHeaderText("Số lượng thực nhận cho " + sel.getMerchandiseCode());
        dialog.showAndWait().ifPresent(input -> {
            try {
                int actual = Integer.parseInt(input);
                InboundRecord record = new InboundRecord(sel, sel.getQty(), actual, "warehouse1");
                controller.confirmFullInbound(record);
                orderTable.getItems().remove(sel);
                new Alert(Alert.AlertType.INFORMATION, "Nhập kho thành công!").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
            }
        });
    }
}
