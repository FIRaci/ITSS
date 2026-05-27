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
        stage.setMinHeight(620);

        // ── Tiêu đề ───────────────────────────────────────────────────────
        Label lblTitle = new Label("📦 Danh mục Mặt hàng");
        lblTitle.getStyleClass().add("header-title");

        // ── Thanh tìm kiếm ────────────────────────────────────────────────
        TextField txtSearch = new TextField();
        txtSearch.getStyleClass().add("text-field");
        txtSearch.setPromptText("🔍 Tìm theo mã hoặc tên hàng...");
        txtSearch.setPrefWidth(300);

        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-secondary");

        HBox toolbar = new HBox(10, txtSearch, btnSearch);
        toolbar.getStyleClass().add("toolbar");

        // ── Bảng danh mục ─────────────────────────────────────────────────
        TableView<Merchandise> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable loadData = () -> table.setItems(useCase.getAll());
        loadData.run();

        btnSearch.setOnAction(e -> {
            ObservableList<Merchandise> all = useCase.getAll();
            String kw = txtSearch.getText().trim().toLowerCase();
            if (kw.isEmpty()) { table.setItems(all); return; }
            table.setItems(all.filtered(m ->
                m.getCode().toLowerCase().contains(kw) ||
                m.getName().toLowerCase().contains(kw)));
        });
        txtSearch.setOnAction(e -> btnSearch.fire());

        // ── Form Thêm / Sửa ──────────────────────────────────────────────
        Label lblFormTitle = new Label("Thêm / Chỉnh sửa mặt hàng");
        lblFormTitle.getStyleClass().add("section-title");

        TextField txtCode = new TextField(); txtCode.getStyleClass().add("text-field"); txtCode.setPromptText("Mã hàng *");
        TextField txtName = new TextField(); txtName.getStyleClass().add("text-field"); txtName.setPromptText("Tên hàng *");
        TextField txtUnit = new TextField(); txtUnit.getStyleClass().add("text-field"); txtUnit.setPromptText("Đơn vị * (VD: cái, kg)");
        TextField txtDesc = new TextField(); txtDesc.getStyleClass().add("text-field"); txtDesc.setPromptText("Mô tả");

        // Chọn dòng → điền form, khoá mã (không đổi PK)
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                txtCode.setText(nv.getCode());
                txtCode.setEditable(false);
                txtCode.setDisable(true);
                txtName.setText(nv.getName());
                txtUnit.setText(nv.getUnit());
                txtDesc.setText(nv.getDescription());
            } else {
                resetCodeField(txtCode);
            }
        });

        Button btnAdd    = new Button("➕ Thêm mới");    btnAdd.getStyleClass().add("btn-primary");
        Button btnUpdate = new Button("💾 Cập nhật");    btnUpdate.getStyleClass().add("btn-success");
        Button btnDelete = new Button("🗑 Xóa");          btnDelete.getStyleClass().add("btn-danger");
        Button btnClear  = new Button("✕ Bỏ chọn");      btnClear.getStyleClass().add("btn-secondary");

        btnAdd.setOnAction(e -> {
            try {
                useCase.add(txtCode.getText(), txtName.getText(), txtUnit.getText(), txtDesc.getText());
                loadData.run();
                clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                showSuccess("Thêm mặt hàng thành công!");
            } catch (Exception ex) { showAlert("Lỗi", ex.getMessage()); }
        });

        btnUpdate.setOnAction(e -> {
            Merchandise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Chưa chọn", "Hãy chọn một mặt hàng để cập nhật."); return; }
            try {
                useCase.update(sel.getCode(), txtName.getText(), txtUnit.getText(), txtDesc.getText());
                loadData.run();
                clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                showSuccess("Cập nhật thành công!");
            } catch (Exception ex) { showAlert("Lỗi", ex.getMessage()); }
        });

        btnDelete.setOnAction(e -> {
            Merchandise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Chưa chọn", "Hãy chọn một mặt hàng để xóa."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa mặt hàng '" + sel.getCode() + " - " + sel.getName() + "'?\n⚠ Không thể hoàn tác!",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Xác nhận xóa"); confirm.setHeaderText(null);
            if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                try {
                    useCase.delete(sel.getCode());
                    loadData.run();
                    clearForm(txtCode, txtName, txtUnit, txtDesc, table);
                    showSuccess("Đã xóa mặt hàng!");
                } catch (Exception ex) { showAlert("Lỗi", ex.getMessage()); }
            }
        });

        btnClear.setOnAction(e -> clearForm(txtCode, txtName, txtUnit, txtDesc, table));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12); formGrid.setVgap(10);
        formGrid.addRow(0, new Label("Mã hàng *:"), txtCode, new Label("Tên hàng *:"), txtName);
        formGrid.addRow(1, new Label("Đơn vị *:"), txtUnit, new Label("Mô tả:"), txtDesc);
        GridPane.setHgrow(txtCode, Priority.ALWAYS); GridPane.setHgrow(txtName, Priority.ALWAYS);
        GridPane.setHgrow(txtUnit, Priority.ALWAYS); GridPane.setHgrow(txtDesc, Priority.ALWAYS);

        HBox formActions = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);

        VBox formCard = new VBox(10, lblFormTitle, formGrid, formActions);
        formCard.getStyleClass().add("card");
        formCard.setPadding(new Insets(16));

        // ── Layout tổng ───────────────────────────────────────────────────
        VBox layout = new VBox(16, lblTitle, toolbar, table, new Separator(), formCard);
        layout.setPadding(new Insets(24));
        layout.getStyleClass().add("content-area");

        Scene scene = new Scene(layout, 950, 700);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch (Exception ignore) {}
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private TableView<Merchandise> buildTable() {
        TableView<Merchandise> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setPlaceholder(new Label("Danh mục trống. Hãy thêm mặt hàng đầu tiên!"));
        table.setPrefHeight(300);

        TableColumn<Merchandise, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code")); colCode.setPrefWidth(120);
        TableColumn<Merchandise, String> colName = new TableColumn<>("Tên hàng");
        colName.setCellValueFactory(new PropertyValueFactory<>("name")); colName.setPrefWidth(260);
        TableColumn<Merchandise, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit")); colUnit.setPrefWidth(100);
        TableColumn<Merchandise, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description")); colDesc.setPrefWidth(360);

        table.getColumns().addAll(colCode, colName, colUnit, colDesc);
        return table;
    }

    private void resetCodeField(TextField txtCode) {
        txtCode.setEditable(true);
        txtCode.setDisable(false);
    }

    private void clearForm(TextField code, TextField name, TextField unit, TextField desc, TableView<Merchandise> table) {
        code.clear(); name.clear(); unit.clear(); desc.clear();
        resetCodeField(code);
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
