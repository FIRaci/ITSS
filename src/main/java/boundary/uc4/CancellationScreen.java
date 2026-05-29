package boundary.uc4;

import controller.uc4.CancellationController;
import entity.uc4.CancellationProposal;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class CancellationScreen {
    private Stage stage;
    private CancellationController controller;
    private TableView<CancellationProposal> proposalTable;

    public CancellationScreen(Stage stage) {
        this.stage = stage;
        this.controller = new CancellationController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        proposalTable = new TableView<>();
        TableColumn<CancellationProposal, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<CancellationProposal, Integer> colOrderId = new TableColumn<>("Mã đơn");
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        TableColumn<CancellationProposal, String> colReason = new TableColumn<>("Lý do");
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        proposalTable.getColumns().addAll(colId, colOrderId, colReason);

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.setOnAction(e -> loadData());

        Button btnApprove = new Button("Duyệt hủy");
        btnApprove.setOnAction(e -> approve());

        Button btnReject = new Button("Từ chối");
        btnReject.setOnAction(e -> reject());

        HBox actions = new HBox(10, btnRefresh, btnApprove, btnReject);
        root.getChildren().addAll(
            new Label("XỬ LÝ HỦY ĐƠN HÀNG"),
            proposalTable, actions
        );

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("UC4 - Xử lý hủy đơn hàng");
        stage.show();
    }

    private void loadData() {
        proposalTable.getItems().setAll(controller.getPendingCancellations());
    }

    private void approve() {
        CancellationProposal sel = proposalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            controller.approveCancellation(sel.getId(), sel.getOrderId());
            proposalTable.getItems().remove(sel);
            new Alert(Alert.AlertType.INFORMATION, "Đã duyệt hủy đơn!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    private void reject() {
        CancellationProposal sel = proposalTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            controller.rejectCancellation(sel.getId(), sel.getOrderId());
            proposalTable.getItems().remove(sel);
            new Alert(Alert.AlertType.INFORMATION, "Đã từ chối hủy đơn!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }
}
