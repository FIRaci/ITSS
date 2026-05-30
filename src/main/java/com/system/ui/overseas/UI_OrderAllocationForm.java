package com.system.ui.overseas;

import com.system.application.overseas.ProcessImportRequestUseCase;
import com.itss.AllocationRow;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class UI_OrderAllocationForm {
    private ProcessImportRequestUseCase processUseCase;

    public UI_OrderAllocationForm() {
        this.processUseCase = new ProcessImportRequestUseCase();
    }

    public void show(String requestId, Runnable onComplete) {
        List<AllocationRow> plan = processUseCase.calculatePlan(requestId);
        if (plan == null || plan.isEmpty()) {
            showAlert("Không thể lập kế hoạch", "Không có phương án phù hợp hoặc dữ liệu tồn kho không đủ.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Kế hoạch chọn Site & Gửi đơn hàng");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<AllocationRow> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<AllocationRow, String> c1 = new TableColumn<>("Mã hàng"); c1.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<AllocationRow, String> c2 = new TableColumn<>("Site"); c2.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<AllocationRow, Integer> c3 = new TableColumn<>("Số lượng"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<AllocationRow, String> c4 = new TableColumn<>("Vận chuyển"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        table.getColumns().addAll(c1, c2, c3, c4);
        table.setItems(FXCollections.observableArrayList(plan));

        Button btnConfirm = new Button("Xác nhận & Gửi đơn");
        btnConfirm.getStyleClass().add("btn-primary");
        btnConfirm.setOnAction(e -> {
            try {
                processUseCase.submitOrders(requestId, plan);
                showSuccessPopup("Đã phân bổ Site và gửi đơn hàng thành công!");
                stage.close();
                onComplete.run();
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        layout.getChildren().addAll(new Label("Dự thảo kế hoạch tự động cho yêu cầu: " + requestId), table, btnConfirm);
        Scene scene = new Scene(layout, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}
