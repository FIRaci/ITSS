package com.system.ui.request;

import com.system.application.masterdata.ManageMerchandiseUseCase;
import com.system.application.request.CreateImportRequestUseCase;
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

/**
 * Màn hình Tạo Yêu Cầu Nhập Hàng Mới (Bộ phận Bán Hàng).
 *
 * Luồng sự kiện chính:
 *  1. Mã YC tự động sinh, chỉ đọc.
 *  2. Nhân viên nhập mã hàng vào thanh tìm kiếm → hệ thống gợi ý (autocomplete).
 *  3. Chọn mặt hàng → popup nhập SL / Đơn vị / Ngày nhận.
 *  4. Lặp đến khi đủ hàng → Gửi yêu cầu.
 */
public class CreateRequestScreen {
    private final CreateImportRequestUseCase useCase;
    private final ManageMerchandiseUseCase merchandiseUseCase;

    // Danh sách mặt hàng đã thêm vào yêu cầu
    private final ObservableList<ImportRequestDetail> detailsList = FXCollections.observableArrayList();

    public CreateRequestScreen() {
        this.useCase = new CreateImportRequestUseCase();
        this.merchandiseUseCase = new ManageMerchandiseUseCase();
    }

    public void show(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Yêu Cầu Nhập Hàng Mới");

        // ─── Mã Yêu Cầu (auto-serial, read-only) ────────────────────────────
        String generatedId = merchandiseUseCase.generateNextRequestId();
        TextField txtId = new TextField(generatedId);
        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        Label lblId = new Label("Mã Yêu Cầu:");
        lblId.setStyle("-fx-font-weight: bold;");
        HBox headerBox = new HBox(12, lblId, txtId);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 8, 0));

        Separator sep1 = new Separator();

        // ─── Thanh tìm kiếm mặt hàng (autocomplete) ────────────────────────
        Label lblSearch = new Label("Tìm kiếm & Thêm mặt hàng");
        lblSearch.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Nhập mã hàng hoặc tên hàng để tìm kiếm...");
        txtSearch.setStyle("-fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #cbd5e1;");
        txtSearch.setPrefWidth(450);

        // Label lỗi mã hàng không tìm thấy
        Label lblSearchError = new Label();
        lblSearchError.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        lblSearchError.setVisible(false);
        lblSearchError.setManaged(false);

        // Dropdown gợi ý dùng Popup để nổi lên trên layout, không đẩy content xuống
        ListView<Merchandise> suggestionList = new ListView<>();
        suggestionList.setPrefWidth(450);
        suggestionList.setPrefHeight(180);
        suggestionList.setStyle("-fx-border-color: #94a3b8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");

        Popup suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);
        suggestionPopup.getContent().add(suggestionList);

        VBox searchBox = new VBox(4, txtSearch, lblSearchError);

        // Xử lý nhập từ khóa → gợi ý
        txtSearch.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            String kw = txtSearch.getText().trim();
            lblSearchError.setVisible(false);
            lblSearchError.setManaged(false);
            if (kw.isEmpty()) {
                suggestionPopup.hide();
                return;
            }
            List<Merchandise> results = merchandiseUseCase.search(kw);
            if (results.isEmpty()) {
                suggestionPopup.hide();
                lblSearchError.setText("Không tìm thấy mặt hàng phù hợp.");
                lblSearchError.setVisible(true);
                lblSearchError.setManaged(true);
            } else {
                suggestionList.setItems(FXCollections.observableArrayList(results));
                // Hiện popup ngay bên dưới ô tìm kiếm
                Bounds bounds = txtSearch.localToScreen(txtSearch.getBoundsInLocal());
                if (bounds != null) {
                    suggestionPopup.show(stage, bounds.getMinX(), bounds.getMaxY() + 2);
                }
                lblSearchError.setVisible(false);
                lblSearchError.setManaged(false);
            }
        });

        // Chọn mặt hàng từ gợi ý → mở popup nhập thông tin chi tiết
        suggestionList.setOnMouseClicked(evt -> {
            Merchandise selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                suggestionPopup.hide();
                txtSearch.clear();
                showDetailInputPopup(stage, selected, detailsList);
            }
        });

        // Ẩn popup khi mất focus
        txtSearch.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) suggestionPopup.hide();
        });

        // ─── Bảng danh sách mặt hàng đã thêm ──────────────────────────────
        Label lblList = new Label("Danh sách mặt hàng trong yêu cầu");
        lblList.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TableView<ImportRequestDetail> table = buildDetailTable();
        table.setItems(detailsList);
        table.setPrefHeight(220);

        // Nút xóa dòng đã chọn trong bảng
        Button btnRemoveRow = new Button("✕ Xóa dòng đã chọn");
        btnRemoveRow.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-border-color: #fca5a5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
        btnRemoveRow.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) ->
                btnRemoveRow.setDisable(nv == null));
        btnRemoveRow.setOnAction(e -> {
            ImportRequestDetail sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) detailsList.remove(sel);
        });

        Separator sep2 = new Separator();

        // ─── Nút Gửi Yêu Cầu ────────────────────────────────────────────────
        Button btnSave = new Button("📤 Gửi Yêu Cầu");
        btnSave.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 24; -fx-border-radius: 8; -fx-background-radius: 8;");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setOnAction(e -> {
            if (detailsList.isEmpty()) {
                showAlert("Lỗi", "Phải có ít nhất 1 mặt hàng trong danh sách!");
                return;
            }
            String reqId = txtId.getText();
            String user = com.system.application.auth.SessionManager.getCurrentUser().getUsername();
            try {
                useCase.execute(reqId, user, detailsList);
                showSuccessPopup("Gửi yêu cầu thành công!\nMã yêu cầu: " + reqId);
                stage.close();
                onComplete.run();
            } catch (Exception ex) {
                showAlert("Lỗi hệ thống", ex.getMessage());
            }
        });

        Button btnCancel = new Button("Hủy bỏ");
        btnCancel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-padding: 10 24; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #cbd5e1;");
        btnCancel.setOnAction(e -> stage.close());

        HBox bottomButtons = new HBox(12, btnSave, btnCancel);
        HBox.setHgrow(btnSave, Priority.ALWAYS);

        // ─── Ghép layout ────────────────────────────────────────────────────
        VBox layout = new VBox(14,
                headerBox,
                sep1,
                lblSearch, searchBox,
                sep2,
                lblList, table, btnRemoveRow,
                new Separator(),
                bottomButtons
        );
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 750, 680);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch (Exception ignore) {}
        stage.setScene(scene);
        stage.show();

        // Focus vào ô tìm kiếm sau khi mở
        Platform.runLater(txtSearch::requestFocus);
    }

    /**
     * Popup nhập thông tin chi tiết (SL, Đơn vị, Ngày nhận) cho mặt hàng được chọn.
     */
    private void showDetailInputPopup(Stage parentStage, Merchandise merchandise,
                                      ObservableList<ImportRequestDetail> detailsList) {
        Stage popup = new Stage();
        popup.initOwner(parentStage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Nhập thông tin mặt hàng");

        VBox layout = new VBox(14);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(420);

        // Thông tin mặt hàng (read-only)
        Label lblCode = new Label("Mã hàng: " + merchandise.getCode());
        lblCode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
        Label lblName = new Label("Tên hàng: " + merchandise.getName());
        lblName.setStyle("-fx-text-fill: #475569;");

        Separator sep = new Separator();

        // ─── Form nhập ───────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);

        // Số lượng
        Label lblQty = new Label("Số lượng *");
        lblQty.setStyle("-fx-font-weight: bold;");
        TextField txtQty = new TextField();
        txtQty.setPromptText("VD: 100");
        txtQty.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        Label lblQtyErr = new Label();
        lblQtyErr.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");

        // Đơn vị (điền sẵn từ danh mục nhưng cho sửa)
        Label lblUnit = new Label("Đơn vị *");
        lblUnit.setStyle("-fx-font-weight: bold;");
        TextField txtUnit = new TextField(merchandise.getUnit());
        txtUnit.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        Label lblUnitErr = new Label();
        lblUnitErr.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");

        // Ngày nhận mong muốn
        Label lblDate = new Label("Ngày nhận mong muốn *");
        lblDate.setStyle("-fx-font-weight: bold;");
        DatePicker dpDate = new DatePicker();
        dpDate.setPromptText("Chọn ngày...");
        dpDate.setStyle("-fx-pref-width: 200;");
        // Chặn chọn ngày trong quá khứ
        dpDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
                }
            }
        });
        Label lblDateErr = new Label();
        lblDateErr.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");

        grid.addRow(0, lblQty, txtQty);
        grid.addRow(1, new Label(), lblQtyErr);
        grid.addRow(2, lblUnit, txtUnit);
        grid.addRow(3, new Label(), lblUnitErr);
        grid.addRow(4, lblDate, dpDate);
        grid.addRow(5, new Label(), lblDateErr);
        GridPane.setHgrow(txtQty, Priority.ALWAYS);
        GridPane.setHgrow(txtUnit, Priority.ALWAYS);
        GridPane.setHgrow(dpDate, Priority.ALWAYS);

        // Nút "Thêm vào danh sách"
        Button btnAdd = new Button("✓ Thêm vào danh sách");
        btnAdd.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 8; -fx-background-radius: 8;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        Button btnClose = new Button("Hủy");
        btnClose.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 10 20; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #cbd5e1;");
        btnClose.setOnAction(e -> popup.close());

        btnAdd.setOnAction(e -> {
            boolean valid = true;

            // Validate Số lượng
            int qty = 0;
            try {
                qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
                lblQtyErr.setText("");
                txtQty.setStyle(txtQty.getStyle().replace("-fx-border-color: #ef4444;", "-fx-border-color: #cbd5e1;"));
            } catch (NumberFormatException ex) {
                lblQtyErr.setText("Số lượng phải là số nguyên dương (> 0).");
                txtQty.setStyle("-fx-border-color: #ef4444; -fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6;");
                valid = false;
            }

            // Validate Đơn vị
            if (txtUnit.getText().trim().isEmpty()) {
                lblUnitErr.setText("Đơn vị không được để trống.");
                txtUnit.setStyle("-fx-border-color: #ef4444; -fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6;");
                valid = false;
            } else {
                lblUnitErr.setText("");
                txtUnit.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
            }

            // Validate Ngày nhận
            if (dpDate.getValue() == null || !dpDate.getValue().isAfter(LocalDate.now())) {
                lblDateErr.setText("Ngày nhận phải là ngày trong tương lai.");
                valid = false;
            } else {
                lblDateErr.setText("");
            }

            if (!valid) return;

            // Kiểm tra không thêm trùng mã hàng trong cùng yêu cầu
            boolean duplicate = detailsList.stream()
                    .anyMatch(d -> d.getMerchandiseCode().equals(merchandise.getCode()));
            if (duplicate) {
                showAlert("Trùng mặt hàng",
                        "Mặt hàng '" + merchandise.getCode() + "' đã có trong danh sách.\n" +
                        "Vui lòng chỉnh sửa dòng hiện tại thay vì thêm mới.");
                return;
            }

            ImportRequestDetail detail = new ImportRequestDetail(
                    0, "", merchandise.getCode(), qty,
                    txtUnit.getText().trim(), dpDate.getValue().toString()
            );
            detail.setUiAction("Add");
            detailsList.add(detail);
            popup.close();
        });

        HBox bottomBtns = new HBox(10, btnAdd, btnClose);
        HBox.setHgrow(btnAdd, Priority.ALWAYS);

        layout.getChildren().addAll(lblCode, lblName, sep, grid, bottomBtns);

        Scene scene = new Scene(layout);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch (Exception ignore) {}
        popup.setScene(scene);
        popup.show();
        Platform.runLater(txtQty::requestFocus);
    }

    /** Tạo bảng hiển thị danh sách mặt hàng đã thêm. */
    @SuppressWarnings("unchecked")
    private TableView<ImportRequestDetail> buildDetailTable() {
        TableView<ImportRequestDetail> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setPlaceholder(new Label("Chưa có mặt hàng nào. Hãy tìm kiếm và thêm ở trên."));

        TableColumn<ImportRequestDetail, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        colCode.setPrefWidth(130);

        TableColumn<ImportRequestDetail, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setPrefWidth(100);

        TableColumn<ImportRequestDetail, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colUnit.setPrefWidth(100);

        TableColumn<ImportRequestDetail, String> colDate = new TableColumn<>("Ngày nhận mong muốn");
        colDate.setCellValueFactory(new PropertyValueFactory<>("desiredDeliveryDate"));
        colDate.setPrefWidth(180);

        table.getColumns().addAll(colCode, colQty, colUnit, colDate);
        return table;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void showSuccessPopup(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
