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
 * Luồng sự kiện:
 *  1. Mã YC tự động sinh dạng YCNH-YYYYMMDD-001, chỉ đọc.
 *  2. Nhân viên nhập từ khóa → gợi ý autocomplete (Popup overlay).
 *  3. Chọn mặt hàng → popup nhập SL / Đơn vị / Ngày nhận.
 *  4. Lặp cho đủ → Gửi yêu cầu.
 */
public class CreateRequestScreen {
    private final CreateImportRequestUseCase useCase;
    private final ManageMerchandiseUseCase merchandiseUseCase;
    private final ObservableList<ImportRequestDetail> detailsList = FXCollections.observableArrayList();

    public CreateRequestScreen() {
        this.useCase = new CreateImportRequestUseCase();
        this.merchandiseUseCase = new ManageMerchandiseUseCase();
    }

    public void show(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Yêu Cầu Nhập Hàng Mới");

        // ── Mã Yêu Cầu (auto-serial, read-only) ──────────────────────────
        String generatedId = merchandiseUseCase.generateNextRequestId();
        TextField txtId = new TextField(generatedId);
        txtId.setEditable(false);
        txtId.setDisable(true);
        txtId.getStyleClass().add("text-field");
        txtId.setPrefWidth(260);

        Label lblId = new Label("Mã Yêu Cầu:");
        lblId.getStyleClass().add("label-form");

        HBox headerBox = new HBox(12, lblId, txtId);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // ── Tìm kiếm mặt hàng (autocomplete) ─────────────────────────────
        Label lblSearch = new Label("🔍  Tìm kiếm & Thêm mặt hàng");
        lblSearch.getStyleClass().add("section-title");

        TextField txtSearch = new TextField();
        txtSearch.getStyleClass().add("text-field");
        txtSearch.setPromptText("Nhập mã hàng hoặc tên hàng để tìm kiếm...");
        txtSearch.setPrefWidth(460);

        Label lblSearchError = new Label();
        lblSearchError.getStyleClass().add("label-subtle");
        lblSearchError.setStyle("-fx-text-fill: #ef4444;");
        lblSearchError.setVisible(false);
        lblSearchError.setManaged(false);

        // Dropdown overlay (Popup, không chiếm layout)
        ListView<Merchandise> suggestionList = new ListView<>();
        suggestionList.setPrefWidth(460);
        suggestionList.setPrefHeight(200);

        Popup suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);
        suggestionPopup.getContent().add(suggestionList);

        VBox searchBox = new VBox(6, txtSearch, lblSearchError);

        txtSearch.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            String kw = txtSearch.getText().trim();
            lblSearchError.setVisible(false);
            lblSearchError.setManaged(false);
            if (kw.isEmpty()) { suggestionPopup.hide(); return; }
            List<Merchandise> results = merchandiseUseCase.search(kw);
            if (results.isEmpty()) {
                suggestionPopup.hide();
                lblSearchError.setText("Không tìm thấy mặt hàng phù hợp.");
                lblSearchError.setVisible(true);
                lblSearchError.setManaged(true);
            } else {
                suggestionList.setItems(FXCollections.observableArrayList(results));
                Bounds bounds = txtSearch.localToScreen(txtSearch.getBoundsInLocal());
                if (bounds != null) suggestionPopup.show(stage, bounds.getMinX(), bounds.getMaxY() + 2);
                lblSearchError.setVisible(false);
                lblSearchError.setManaged(false);
            }
        });

        suggestionList.setOnMouseClicked(evt -> {
            Merchandise selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                suggestionPopup.hide();
                txtSearch.clear();
                showDetailInputPopup(stage, selected, detailsList);
            }
        });

        txtSearch.focusedProperty().addListener((obs, was, now) -> { if (!now) suggestionPopup.hide(); });

        // ── Bảng danh sách mặt hàng đã thêm ─────────────────────────────
        Label lblList = new Label("📋  Danh sách mặt hàng trong yêu cầu");
        lblList.getStyleClass().add("section-title");

        TableView<ImportRequestDetail> table = buildDetailTable();
        table.setItems(detailsList);
        table.setPrefHeight(210);

        Button btnRemoveRow = new Button("✕ Xóa dòng đã chọn");
        btnRemoveRow.getStyleClass().add("btn-danger");
        btnRemoveRow.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) ->
                btnRemoveRow.setDisable(nv == null));
        btnRemoveRow.setOnAction(e -> {
            ImportRequestDetail sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) detailsList.remove(sel);
        });

        // ── Nút Gửi / Hủy ────────────────────────────────────────────────
        Button btnSave = new Button("📤  Gửi Yêu Cầu");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setMaxWidth(Double.MAX_VALUE);

        Button btnCancel = new Button("Hủy bỏ");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> stage.close());

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

        HBox bottomButtons = new HBox(12, btnSave, btnCancel);
        HBox.setHgrow(btnSave, Priority.ALWAYS);

        // ── Layout tổng ───────────────────────────────────────────────────
        VBox layout = new VBox(16,
                headerBox,
                new Separator(),
                lblSearch, searchBox,
                new Separator(),
                lblList, table, btnRemoveRow,
                new Separator(),
                bottomButtons
        );
        layout.setPadding(new Insets(24));

        Scene scene = new Scene(layout, 760, 680);
        stage.setScene(scene);
        stage.show();

        Platform.runLater(txtSearch::requestFocus);
    }

    /**
     * Popup nhập SL / Đơn vị / Ngày nhận cho mặt hàng được chọn.
     */
    private void showDetailInputPopup(Stage parentStage, Merchandise merchandise,
                                      ObservableList<ImportRequestDetail> detailsList) {
        Stage popup = new Stage();
        popup.initOwner(parentStage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Nhập thông tin: " + merchandise.getCode());

        VBox layout = new VBox(14);
        layout.setPadding(new Insets(24));
        layout.setPrefWidth(440);

        // Thông tin mặt hàng (read-only header)
        Label lblCode = new Label(merchandise.getCode() + "  —  " + merchandise.getName());
        lblCode.getStyleClass().add("section-title");
        Label lblHint = new Label("Điền thông tin bên dưới để thêm vào yêu cầu.");
        lblHint.getStyleClass().add("label-subtle");

        Separator sep = new Separator();

        // Form nhập
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10);

        Label lblQty = new Label("Số lượng *"); lblQty.getStyleClass().add("label-form");
        TextField txtQty = new TextField();
        txtQty.getStyleClass().add("text-field");
        txtQty.setPromptText("VD: 100");
        Label lblQtyErr = new Label(); lblQtyErr.getStyleClass().add("label-subtle");
        lblQtyErr.setStyle("-fx-text-fill: #ef4444;");

        Label lblUnit = new Label("Đơn vị *"); lblUnit.getStyleClass().add("label-form");
        TextField txtUnit = new TextField(merchandise.getUnit());
        txtUnit.getStyleClass().add("text-field");
        Label lblUnitErr = new Label(); lblUnitErr.getStyleClass().add("label-subtle");
        lblUnitErr.setStyle("-fx-text-fill: #ef4444;");

        Label lblDate = new Label("Ngày nhận mong muốn *"); lblDate.getStyleClass().add("label-form");
        DatePicker dpDate = new DatePicker();
        dpDate.getStyleClass().add("date-picker");
        dpDate.setPromptText("Chọn ngày...");
        dpDate.setMaxWidth(Double.MAX_VALUE);
        // Disable ngày quá khứ
        dpDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) setDisable(true);
            }
        });
        Label lblDateErr = new Label(); lblDateErr.getStyleClass().add("label-subtle");
        lblDateErr.setStyle("-fx-text-fill: #ef4444;");

        grid.addRow(0, lblQty, txtQty);   grid.addRow(1, new Label(), lblQtyErr);
        grid.addRow(2, lblUnit, txtUnit); grid.addRow(3, new Label(), lblUnitErr);
        grid.addRow(4, lblDate, dpDate); grid.addRow(5, new Label(), lblDateErr);
        GridPane.setHgrow(txtQty, Priority.ALWAYS);
        GridPane.setHgrow(txtUnit, Priority.ALWAYS);
        GridPane.setHgrow(dpDate, Priority.ALWAYS);

        Button btnAdd = new Button("✓  Thêm vào danh sách");
        btnAdd.getStyleClass().add("btn-success");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        Button btnClose = new Button("Hủy");
        btnClose.getStyleClass().add("btn-secondary");
        btnClose.setOnAction(e -> popup.close());

        btnAdd.setOnAction(e -> {
            boolean valid = true;

            // Validate SL
            int qty = 0;
            try {
                qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
                lblQtyErr.setText("");
            } catch (NumberFormatException ex) {
                lblQtyErr.setText("Số lượng phải là số nguyên dương (> 0).");
                valid = false;
            }

            // Validate ĐV
            if (txtUnit.getText().trim().isEmpty()) {
                lblUnitErr.setText("Đơn vị không được để trống."); valid = false;
            } else { lblUnitErr.setText(""); }

            // Validate Ngày
            if (dpDate.getValue() == null || !dpDate.getValue().isAfter(LocalDate.now())) {
                lblDateErr.setText("Ngày nhận phải là ngày trong tương lai."); valid = false;
            } else { lblDateErr.setText(""); }

            if (!valid) return;

            // Kiểm tra trùng mã trong cùng yêu cầu
            if (detailsList.stream().anyMatch(d -> d.getMerchandiseCode().equals(merchandise.getCode()))) {
                showAlert("Trùng mặt hàng",
                    "Mặt hàng '" + merchandise.getCode() + "' đã có trong danh sách.\n" +
                    "Chỉnh sửa dòng hiện tại thay vì thêm mới.");
                return;
            }

            ImportRequestDetail detail = new ImportRequestDetail(
                    0, "", merchandise.getCode(), qty,
                    txtUnit.getText().trim(), dpDate.getValue().toString());
            detail.setUiAction("Add");
            detailsList.add(detail);
            popup.close();
        });

        HBox bottomBtns = new HBox(10, btnAdd, btnClose);
        HBox.setHgrow(btnAdd, Priority.ALWAYS);

        layout.getChildren().addAll(lblCode, lblHint, sep, grid, bottomBtns);

        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.show();
        Platform.runLater(txtQty::requestFocus);
    }

    @SuppressWarnings("unchecked")
    private TableView<ImportRequestDetail> buildDetailTable() {
        TableView<ImportRequestDetail> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setPlaceholder(new Label("Chưa có mặt hàng nào. Hãy tìm kiếm và thêm ở trên."));

        TableColumn<ImportRequestDetail, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode")); colCode.setPrefWidth(130);
        TableColumn<ImportRequestDetail, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity")); colQty.setPrefWidth(100);
        TableColumn<ImportRequestDetail, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit")); colUnit.setPrefWidth(100);
        TableColumn<ImportRequestDetail, String> colDate = new TableColumn<>("Ngày nhận mong muốn");
        colDate.setCellValueFactory(new PropertyValueFactory<>("desiredDeliveryDate")); colDate.setPrefWidth(180);

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
