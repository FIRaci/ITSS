package com.system.ui.request;

import com.system.application.request.CreateImportRequestUseCase;
import com.itss.ImportRequestDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CreateRequestScreen {
    private CreateImportRequestUseCase useCase;

    public CreateRequestScreen() {
        this.useCase = new CreateImportRequestUseCase();
    }

    public void show(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Yêu Cầu Nhập Hàng Mới");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextField txtId = new TextField(); 
        txtId.setPromptText("Mã Yêu Cầu (VD: REQ-002)");
        txtId.getStyleClass().add("text-field");
        HBox topBox = new HBox(10, new Label("Mã ImportRequest:"), txtId);

        // Subform for details
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã hàng"); txtCode.getStyleClass().add("text-field");
        TextField txtQty = new TextField(); txtQty.setPromptText("SL"); txtQty.getStyleClass().add("text-field");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("Đơn vị"); txtUnit.getStyleClass().add("text-field");
        DatePicker dpDate = new DatePicker();
        dpDate.getStyleClass().add("text-field");

        grid.addRow(0, new Label("Mã hàng:"), txtCode, new Label("Số lượng:"), txtQty);
        grid.addRow(1, new Label("Đơn vị:"), txtUnit, new Label("Ngày nhận:"), dpDate);

        ObservableList<ImportRequestDetail> detailsList = FXCollections.observableArrayList();
        TableView<ImportRequestDetail> table = new TableView<>();
        table.getStyleClass().add("table-view");
        setupDetailTable(table);
        table.setItems(detailsList);

        Button btnAddRow = new Button("Thêm mặt hàng");
        btnAddRow.getStyleClass().add("btn-secondary");
        btnAddRow.setOnAction(e -> {
            try {
                if (txtCode.getText().isEmpty() || txtUnit.getText().isEmpty() || dpDate.getValue() == null) {
                    showAlert("Lỗi", "Vui lòng nhập Mã hàng, Đơn vị và Ngày nhận."); return;
                }
                if (!dpDate.getValue().isAfter(LocalDate.now())) {
                    showAlert("Lỗi", "Ngày nhận phải lớn hơn ngày hiện tại."); return;
                }
                int qty = Integer.parseInt(txtQty.getText());
                if (qty <= 0) throw new Exception();
                ImportRequestDetail ct = new ImportRequestDetail(0, "", txtCode.getText(), qty, txtUnit.getText(), dpDate.getValue().toString());
                ct.setUiAction("Add");
                detailsList.add(ct);
                txtCode.clear(); txtQty.clear(); txtUnit.clear(); dpDate.setValue(null);
            } catch (Exception ex) {
                showAlert("Lỗi", "Số lượng phải là số nguyên dương.");
            }
        });

        Button btnSave = new Button("Gửi Yêu Cầu");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            String reqId = txtId.getText();
            String user = com.system.application.auth.SessionManager.getCurrentUser().getUsername();

            try {
                useCase.execute(reqId, user, detailsList);
                showSuccessPopup("Gửi yêu cầu thành công!");
                stage.close();
                onComplete.run();
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        layout.getChildren().addAll(topBox, new Separator(), grid, btnAddRow, table, btnSave);
        Scene scene = new Scene(layout, 700, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    
    @SuppressWarnings("unchecked")
    private void setupDetailTable(TableView<ImportRequestDetail> table) {
        TableColumn<ImportRequestDetail, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<ImportRequestDetail, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<ImportRequestDetail, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<ImportRequestDetail, String> colDate = new TableColumn<>("Ngày nhận");
        colDate.setCellValueFactory(new PropertyValueFactory<>("desiredDeliveryDate"));
        TableColumn<ImportRequestDetail, String> colAction = new TableColumn<>("Thay đổi");
        colAction.setCellValueFactory(new PropertyValueFactory<>("uiAction"));
        table.getColumns().addAll(colCode, colQty, colUnit, colDate, colAction);
    }
    
    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}
