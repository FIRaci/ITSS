package com.system.ui.warehouse;

import com.system.application.warehouse.ReportDiscrepancyUseCase;
import com.itss.InternationalOrder;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UI_DiscrepancyForm {
    private ReportDiscrepancyUseCase reportUseCase;

    public UI_DiscrepancyForm() {
        this.reportUseCase = new ReportDiscrepancyUseCase();
    }

    public void show(InternationalOrder order, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Biên bản sai lệch");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        ComboBox<String> cbReason = new ComboBox<>();
        cbReason.getItems().addAll("Thiếu hàng", "Hàng hỏng/vỡ", "Sai quy cách/màu sắc", "Thừa hàng (Lưu kho tạm 7 ngày)");
        cbReason.setPromptText("Chọn lý do sai lệch...");
        cbReason.getStyleClass().add("combo-box");
        cbReason.setMaxWidth(Double.MAX_VALUE);

        TextField txtQty = new TextField();
        txtQty.setPromptText("Số lượng sai lệch");
        txtQty.getStyleClass().add("text-field");

        TextField txtEvidence = new TextField();
        txtEvidence.setPromptText("Đường dẫn ảnh minh chứng (bắt buộc nếu hỏng)");
        txtEvidence.getStyleClass().add("text-field");

        TextArea txtNote = new TextArea();
        txtNote.setPromptText("Giải trình chi tiết");
        txtNote.getStyleClass().add("text-field");
        txtNote.setPrefRowCount(3);

        Button btnSave = new Button("Lưu & Gửi biên bản");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            try {
                int qty = 0;
                try {
                    qty = Integer.parseInt(txtQty.getText());
                } catch (NumberFormatException ex) {
                    throw new Exception("Lỗi nghiệp vụ: Số lượng phải là số nguyên dương.");
                }

                String user = com.system.application.auth.SessionManager.getCurrentUser().getUsername();
                reportUseCase.reportDiscrepancy(order, cbReason.getValue(), qty, txtEvidence.getText(), txtNote.getText(), user);
                
                showSuccessPopup("Đã lập biên bản sai lệch.");
                showRTNPopup(order, cbReason.getValue(), qty);
                stage.close();
                onComplete.run();
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        Label lblHeader = new Label("⚠️ Đạt hàng: #" + order.getId() + "  —  Mã hàng: " + order.getMerchandiseCode());
        lblHeader.getStyleClass().add("section-title");

        layout.getChildren().addAll(
            lblHeader, new Separator(),
            new Label("Lý do sai lệch *:"), cbReason,
            new Label("Số lượng sai lệch *:"), txtQty,
            new Label("Ảnh minh chứng (URL):"), txtEvidence,
            new Label("Giải trình chi tiết:"), txtNote,
            new Separator(), btnSave);
        Scene scene = new Scene(layout, 460, 480);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch (Exception ignore) {}
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    private void showRTNPopup(InternationalOrder order, String reason, int qty) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("In Biên Bản / Phiếu Xuất Trả (RTN)");
        a.setHeaderText("RETURN TO VENDOR NOTE (MÔ PHỎNG)");
        String content = "Mã đơn hàng: " + order.getId() + "\n"
                       + "Mã mặt hàng: " + order.getMerchandiseCode() + "\n"
                       + "Loại sai lệch: " + reason + "\n"
                       + "Số lượng sai lệch: " + qty + "\n";
        
        if (reason.contains("Thừa hàng")) {
            content += "\n[!] Ghi chú: Hàng thừa sẽ được lưu kho tạm trong 7 ngày chờ xử lý.";
        }
        
        content += "\n\n*** Đang gửi tín hiệu máy in... ***";
        a.setContentText(content);
        a.showAndWait();
    }
}
