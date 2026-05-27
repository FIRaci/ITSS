package com.system.ui.request;

import com.system.application.request.EditImportRequestUseCase;
import com.system.application.request.ViewRequestDetailUseCase;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditRequestForm_GUI {
    private EditImportRequestUseCase editUseCase;
    private ViewRequestDetailUseCase viewUseCase;

    public EditRequestForm_GUI() {
        this.editUseCase = new EditImportRequestUseCase();
        this.viewUseCase = new ViewRequestDetailUseCase();
    }

    public void show(ImportRequest importRequest, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chỉnh sửa ImportRequest: " + importRequest.getId());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ObservableList<ImportRequestDetail> oldList = viewUseCase.getRequestDetails(importRequest.getId());
        ObservableList<ImportRequestDetail> editingList = FXCollections.observableArrayList();
        for(ImportRequestDetail c : oldList) editingList.add(c.clone());

        TableView<ImportRequestDetail> table = new TableView<>();
        table.getStyleClass().add("table-view");
        setupDetailTable(table);
        table.setItems(editingList);

        // Edit existing selected row
        GridPane editGrid = new GridPane();
        editGrid.setHgap(10); editGrid.setVgap(10);
        TextField eQty = new TextField(); eQty.setPromptText("SL mới"); eQty.getStyleClass().add("text-field");
        DatePicker eDate = new DatePicker(); eDate.getStyleClass().add("text-field");
        Button btnUpdateRow = new Button("Cập nhật dòng"); btnUpdateRow.getStyleClass().add("btn-secondary");
        Button btnDeleteRow = new Button("Xóa dòng"); btnDeleteRow.getStyleClass().add("btn-danger");
        
        btnUpdateRow.setOnAction(e -> {
            ImportRequestDetail sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && !sel.getUiAction().equals("Delete")) {
                if (eDate.getValue() == null) {
                    showAlert("Lỗi", "Ngày nhận không được để trống."); return;
                }
                if (!eDate.getValue().isAfter(LocalDate.now())) {
                    showAlert("Lỗi", "Ngày nhận phải lớn hơn ngày hiện tại."); return;
                }
                int newQty = Integer.parseInt(eQty.getText());
                if (newQty <= 0) { showAlert("Lỗi", "Số lượng phải là số nguyên dương."); return; }
                sel.setQuantity(newQty);
                sel.setDesiredDeliveryDate(eDate.getValue().toString());
                sel.setUiAction(sel.getUiAction().equals("Add") ? "Add" : "Edit");
                table.refresh();
            }
        });
        btnDeleteRow.setOnAction(e -> {
            ImportRequestDetail sel = table.getSelectionModel().getSelectedItem();
            if(sel != null) {
                sel.setUiAction("Delete"); table.refresh();
            }
        });
        editGrid.addRow(0, new Label("Sửa/Xóa dòng chọn -> SL:"), eQty, new Label("Ngày:"), eDate, btnUpdateRow, btnDeleteRow);

        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if(nv != null) {
                eQty.setText(String.valueOf(nv.getQuantity()));
                eDate.setValue(LocalDate.parse(nv.getDesiredDeliveryDate()));
            }
        });

        // Add new item entirely
        GridPane addGrid = new GridPane();
        addGrid.setHgap(10); addGrid.setVgap(10);
        TextField aCode = new TextField(); aCode.setPromptText("Mã hàng"); aCode.getStyleClass().add("text-field");
        TextField aQty = new TextField(); aQty.setPromptText("SL"); aQty.getStyleClass().add("text-field");
        TextField aUnit = new TextField(); aUnit.setPromptText("Đơn vị"); aUnit.getStyleClass().add("text-field");
        DatePicker aDate = new DatePicker(); aDate.getStyleClass().add("text-field");
        Button btnAddRow = new Button("Thêm hàng mới"); btnAddRow.getStyleClass().add("btn-secondary");

        btnAddRow.setOnAction(e -> {
            try {
                if (aCode.getText().isEmpty() || aUnit.getText().isEmpty() || aDate.getValue() == null) {
                    showAlert("Lỗi", "Vui lòng nhập Mã hàng, Đơn vị và Ngày nhận."); return;
                }
                if (!aDate.getValue().isAfter(LocalDate.now())) {
                    showAlert("Lỗi", "Ngày nhận phải lớn hơn ngày hiện tại."); return;
                }
                int qty = Integer.parseInt(aQty.getText());
                if (qty <= 0) throw new Exception();
                ImportRequestDetail ct = new ImportRequestDetail(0, importRequest.getId(), aCode.getText(), qty, aUnit.getText(), aDate.getValue().toString());
                ct.setUiAction("Add");
                editingList.add(ct);
                table.refresh();
                aCode.clear(); aQty.clear(); aUnit.clear(); aDate.setValue(null);
            } catch (Exception ex) {
                showAlert("Lỗi", "Số lượng phải là số nguyên dương.");
            }
        });
        addGrid.addRow(0, new Label("Thêm mặt hàng mới -> Mã:"), aCode, new Label("SL:"), aQty, new Label("ĐV:"), aUnit, new Label("Ngày:"), aDate, btnAddRow);


        Button btnReview = new Button("Lưu & So Sánh (Diff)");
        btnReview.getStyleClass().add("btn-primary");
        btnReview.setOnAction(e -> {
            showDiffReviewPopup(importRequest.getId(), oldList, editingList, stage, onComplete);
        });

        layout.getChildren().addAll(new Label("Tính năng thay đổi:"), table, editGrid, addGrid, btnReview);
        Scene scene = new Scene(layout, 1200, 650);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void showDiffReviewPopup(String requestId, ObservableList<ImportRequestDetail> oldList, ObservableList<ImportRequestDetail> newList, Stage parentStage, Runnable onComplete) {
        String diffText = editUseCase.generateDiffText(oldList, newList);
        if (diffText.isEmpty()) {
            long remainCount = newList.stream().filter(n -> !n.getUiAction().equals("Delete")).count();
            if (remainCount == 0) {
                showAlert("Lỗi", "Danh sách hàng không được để trống. Vui lòng hủy yêu cầu nếu không còn nhu cầu.");
                return;
            }
            showAlert("Thông báo", "Không có thay đổi nào để lưu!"); 
            return;
        }

        Stage diffStage = new Stage();
        diffStage.initModality(Modality.APPLICATION_MODAL);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        TextArea txtDiff = new TextArea(diffText); txtDiff.setEditable(false);
        txtDiff.getStyleClass().add("text-field");
        TextField txtReason = new TextField(); txtReason.setPromptText("Nhập lý do thay đổi (Bắt buộc)");
        txtReason.getStyleClass().add("text-field");
        Button btnConfirm = new Button("Xác nhận");
        btnConfirm.getStyleClass().add("btn-primary");

        btnConfirm.setOnAction(e -> {
            String user = com.system.application.auth.SessionManager.getCurrentUser().getUsername();
            try {
                editUseCase.execute(requestId, oldList, newList, txtReason.getText(), user);
                diffStage.close(); parentStage.close();
                showSuccessPopup("Cập nhật thành công!");
                onComplete.run();
            } catch(Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        layout.getChildren().addAll(new Label("Bảng So Sánh (Diff):"), txtDiff, new Label("Lý do:"), txtReason, btnConfirm);
        Scene scene = new Scene(layout, 550, 450);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        diffStage.setScene(scene);
        diffStage.show();
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
