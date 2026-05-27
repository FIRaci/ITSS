package com.system.ui.masterdata;

import com.itss.Merchandise;
import com.system.application.masterdata.ManageMerchandiseUseCase;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Màn hình Quản lý Danh mục Mặt hàng (dành cho Bộ phận Bán Hàng).
 * Chức năng: Xem / Thêm / Sửa / Xóa mặt hàng trong danh mục.
 * Màn hình này mở dạng modal popup từ UI_RequestList.
 */
public class MerchandiseCatalogScreen {
    private final ManageMerchandiseUseCase useCase;

    public MerchandiseCatalogScreen() {
        this.useCase = new ManageMerchandiseUseCase();
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Quản lý Danh mục Mặt hàng");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // ─── Tiêu đề ──────────────────────────────────────────────────────
        Label lblTitle = new Label("Danh mục Mặt hàng");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // ─── Thanh tìm kiếm ────────────────────────────────────────────────
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tìm theo mã hoặc tên hàng...");
        txtSearch.setStyle("-fx-padding: 8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #cbd5e1;");
        txtSearch.setPrefWidth(320);
        Button btnSearch = new Button("🔍 Tìm");
        btnSearch.getStyleClass().add("btn-secondary");

        HBox toolbar = new HBox(10, txtSearch, btnSearch);
        toolbar.setStyle("-fx-alignment: center-left;");

        // ─── Bảng danh mục ─────────────────────────────────────────────────
        TableView<Merchandise> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable loadData = () -> table.setItems(useCase.getAll());
        loadData.run();

        btnSearch.setOnAction(e -> {
            ObservableList<Merchandise> allData = useCase.getAll();
            String kw = txtSearch.getText().trim().toLowerCase();
            if (kw.isEmpty()) {
                table.setItems(allData);
            } else {
                table.setItems(allData.filtered(m ->
                        m.getCode().toLowerCase().contains(kw) ||
                        m.getName().toLowerCase().contains(kw)));
            }
        });
        txtSearch.setOnAction(e -> btnSearch.fire());

        // ─── Form Thêm / Sửa ────────────────────────────────────────────────
        Label lblFormTitle = new Label("Thêm / Chỉnh sửa mặt hàng");
        lblFormTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField txtCode = new TextField(); txtCode.setPromptText("Mã hàng *");
        txtCode.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        TextField txtName = new TextField(); txtName.setPromptText("Tên hàng *");
        txtName.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("Đơn vị * (VD: cái, hộp, kg)");
        txtUnit.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("Mô tả (không bắt buộc)");
        txtDesc.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");

        // Khi chọn dòng → điền vào form, khoá mã hàng (không đổi PK)
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                txtCode.setText(nv.getCode());
                txtCode.setEditable(false);
                txtCode.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
                txtName.setText(nv.getName());
                txtUnit.setText(nv.getUnit());
                txtDesc.setText(nv.getDescription());
            } else {
                txtCode.setEditable(true);
                txtCode.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
            }
        });

        Button btnAdd = new Button("➕ Thêm mới");
        btnAdd.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 6; -fx-background-radius: 6;");
        Button btnUpdate = new Button("💾 Cập nhật");
        btnUpdate.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 6; -fx-background-radius: 6;");
        Button btnDelete = new Button("🗑 Xóa");
        btnDelete.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 6; -fx-background-radius: 6;");
        Button btnClear = new Button("✕ Bỏ chọn");
        btnClear.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 8 16; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");

        btnAdd.setOnAction(e -> {
            try {
                useCase.add(txtCode.getText(), txtName.getText(), txtUnit.getText(), txtDesc.getText());
                loadData.run();
                clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                showSuccessPopup("Thêm mặt hàng thành công!");
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        btnUpdate.setOnAction(e -> {
            Merchandise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Chưa chọn", "Hãy chọn một mặt hàng trong bảng để cập nhật."); return; }
            try {
                useCase.update(sel.getCode(), txtName.getText(), txtUnit.getText(), txtDesc.getText());
                loadData.run();
                clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                showSuccessPopup("Cập nhật thành công!");
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            Merchandise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Chưa chọn", "Hãy chọn một mặt hàng trong bảng để xóa."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Bạn có chắc chắn muốn xóa mặt hàng '" + sel.getCode() + " - " + sel.getName() + "' không?\n" +
                    "⚠ Hành động này không thể hoàn tác!", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText(null);
            if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                try {
                    useCase.delete(sel.getCode());
                    loadData.run();
                    clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                    showSuccessPopup("Đã xóa mặt hàng!");
                } catch (Exception ex) {
                    showAlert("Lỗi", ex.getMessage());
                }
            }
        });

        btnClear.setOnAction(e -> clearForm(txtCode, txtName, txtUnit, txtDesc, table));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10); formGrid.setVgap(8);
        formGrid.addRow(0, new Label("Mã hàng *:"), txtCode, new Label("Tên hàng *:"), txtName);
        formGrid.addRow(1, new Label("Đơn vị *:"), txtUnit, new Label("Mô tả:"), txtDesc);
        GridPane.setHgrow(txtCode, Priority.ALWAYS);
        GridPane.setHgrow(txtName, Priority.ALWAYS);
        GridPane.setHgrow(txtUnit, Priority.ALWAYS);
        GridPane.setHgrow(txtDesc, Priority.ALWAYS);
        formGrid.setStyle("-fx-padding: 10 0;");

        HBox formActions = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);

        VBox formCard = new VBox(8, lblFormTitle, formGrid, formActions);
        formCard.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 12;");

        // ─── Ghép layout tổng thể ──────────────────────────────────────────
        VBox layout = new VBox(16, lblTitle, toolbar, table, new Separator(), formCard);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 950, 680);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch (Exception ignore) {}
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private TableView<Merchandise> buildTable() {
        TableView<Merchandise> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setPlaceholder(new Label("Danh mục trống. Hãy thêm mặt hàng đầu tiên!"));
        table.setPrefHeight(320);

        TableColumn<Merchandise, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCode.setPrefWidth(120);

        TableColumn<Merchandise, String> colName = new TableColumn<>("Tên hàng");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(240);

        TableColumn<Merchandise, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colUnit.setPrefWidth(100);

        TableColumn<Merchandise, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(350);

        table.getColumns().addAll(colCode, colName, colUnit, colDesc);
        return table;
    }

    private void clearForm(TextField code, TextField name, TextField unit, TextField desc,
                           TableView<Merchandise> table) {
        code.clear(); name.clear(); unit.clear(); desc.clear();
        code.setEditable(true);
        code.setStyle("-fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");
        table.getSelectionModel().clearSelection();
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
