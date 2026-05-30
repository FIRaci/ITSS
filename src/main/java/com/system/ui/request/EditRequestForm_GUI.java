package com.system.ui.request;

import com.system.application.masterdata.ManageMerchandiseUseCase;
import com.system.application.request.EditImportRequestUseCase;
import com.system.application.request.ViewRequestDetailUseCase;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import com.itss.Merchandise;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EditRequestForm_GUI {
    private final EditImportRequestUseCase editUseCase;
    private final ViewRequestDetailUseCase viewUseCase;
    private final ManageMerchandiseUseCase merchandiseUseCase;

    public EditRequestForm_GUI() {
        this.editUseCase = new EditImportRequestUseCase();
        this.viewUseCase = new ViewRequestDetailUseCase();
        this.merchandiseUseCase = new ManageMerchandiseUseCase();
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

        // ─── Thêm mặt hàng mới bằng autocomplete ────────────────────────
        Label lblAddNew = new Label("Thêm mặt hàng mới vào yêu cầu");
        lblAddNew.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField aSearch = new TextField();
        aSearch.setPromptText("Tìm mã hàng hoặc tên hàng để thêm...");
        aSearch.getStyleClass().add("text-field");
        aSearch.setPrefWidth(380);

        Label aSearchErr = new Label();
        aSearchErr.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");
        aSearchErr.setVisible(false);

        ListView<Merchandise> aSuggest = new ListView<>();
        aSuggest.setPrefWidth(380);
        aSuggest.setPrefHeight(160);
        aSuggest.setStyle("-fx-border-color: #94a3b8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");

        Popup aPopup = new Popup();
        aPopup.setAutoHide(true);
        aPopup.getContent().add(aSuggest);

        aSearch.addEventHandler(KeyEvent.KEY_RELEASED, ev -> {
            String kw = aSearch.getText().trim();
            aSearchErr.setVisible(false);
            aSearchErr.setManaged(false);
            if (kw.isEmpty()) { aPopup.hide(); return; }
            List<Merchandise> results = merchandiseUseCase.search(kw);
            if (results.isEmpty()) {
                aPopup.hide();
                aSearchErr.setText("Không tìm thấy mặt hàng.");
                aSearchErr.setVisible(true); aSearchErr.setManaged(true);
            } else {
                aSuggest.setItems(FXCollections.observableArrayList(results));
                Bounds bounds = aSearch.localToScreen(aSearch.getBoundsInLocal());
                if (bounds != null) aPopup.show(stage, bounds.getMinX(), bounds.getMaxY() + 2);
                aSearchErr.setVisible(false); aSearchErr.setManaged(false);
            }
        });

        // Chọn mặt hàng từ gợi ý → popup nhập SL/ĐV/Ngày (tái sử dụng logic)
        aSuggest.setOnMouseClicked(ev -> {
            Merchandise sel = aSuggest.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            aPopup.hide();
            aSearch.clear();
            // Kiểm tra trùng
            boolean dup = editingList.stream()
                    .anyMatch(d -> d.getMerchandiseCode().equals(sel.getCode()) && !d.getUiAction().equals("Delete"));
            if (dup) { showAlert("Trùng mặt hàng", "Mặt hàng '" + sel.getCode() + "' đã có trong danh sách."); return; }
            showAddItemPopup(stage, sel, editingList, importRequest, table);
        });

        aSearch.focusedProperty().addListener((obs, was, now) -> { if (!now) aPopup.hide(); });

        VBox addNewSection = new VBox(6, lblAddNew, aSearch, aSearchErr);
        addNewSection.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-color: #f8fafc; -fx-padding: 10;");


        Button btnReview = new Button("Lưu & So Sánh (Diff)");
        btnReview.getStyleClass().add("btn-primary");
        btnReview.setOnAction(e -> showDiffReviewPopup(importRequest.getId(), oldList, editingList, stage, onComplete));

        layout.getChildren().addAll(new Label("Chỉnh sửa dòng hiện có:"), table, editGrid,
                new Separator(), addNewSection, new Separator(), btnReview);
        Scene scene = new Scene(layout, 1200, 650);
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

    /** Popup nhập SL/ĐV/Ngày khi thêm mặt hàng mới từ edit form. */
    private void showAddItemPopup(Stage owner, Merchandise merch, ObservableList<ImportRequestDetail> list,
                                   ImportRequest importRequest, TableView<ImportRequestDetail> table) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Thêm mặt hàng: " + merch.getCode());

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(400);

        Label lblInfo = new Label(merch.getCode() + " - " + merch.getName());
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TextField txtQty = new TextField(); txtQty.setPromptText("Số lượng"); txtQty.getStyleClass().add("text-field");
        TextField txtUnit = new TextField(merch.getUnit()); txtUnit.setPromptText("Đơn vị"); txtUnit.getStyleClass().add("text-field");
        DatePicker dpDate = new DatePicker();
        dpDate.setDayCellFactory(pk -> new DateCell() {
            @Override public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                if (d.isBefore(LocalDate.now().plusDays(1))) { setDisable(true); }
            }
        });

        Label errLabel = new Label(); errLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");

        Button btnOk = new Button("✓ Thêm vào danh sách"); btnOk.getStyleClass().add("btn-primary");
        btnOk.setMaxWidth(Double.MAX_VALUE);
        btnOk.setOnAction(ev -> {
            try {
                if (txtUnit.getText().trim().isEmpty() || dpDate.getValue() == null) {
                    errLabel.setText("Vui lòng nhập đầy đủ thông tin bắt buộc."); return;
                }
                int qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
                if (!dpDate.getValue().isAfter(LocalDate.now())) {
                    errLabel.setText("Ngày nhận phải là ngày trong tương lai."); return;
                }
                ImportRequestDetail ct = new ImportRequestDetail(
                        0, importRequest.getId(), merch.getCode(), qty,
                        txtUnit.getText().trim(), dpDate.getValue().toString());
                ct.setUiAction("Add");
                list.add(ct); table.refresh(); popup.close();
            } catch (NumberFormatException ex) {
                errLabel.setText("Số lượng phải là số nguyên dương.");
            }
        });

        layout.getChildren().addAll(lblInfo, new Separator(),
                new Label("Số lượng *:"), txtQty,
                new Label("Đơn vị *:"), txtUnit,
                new Label("Ngày nhận *:"), dpDate,
                errLabel, btnOk);
        popup.setScene(new Scene(layout));
        popup.show();
        Platform.runLater(txtQty::requestFocus);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}
