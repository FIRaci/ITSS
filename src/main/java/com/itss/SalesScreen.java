package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesScreen {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;

    public SalesScreen(Main mainApp) {
        this.mainApp = mainApp;
        buildView();
        showYcnhList(); // Default view
    }

    private void buildView() {
        view = new BorderPane();
        
        // Sidebar
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #333645;");
        sidebar.setPrefWidth(200);

        Label lblMenu = new Label("MENU BÁN HÀNG");
        lblMenu.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnList = new Button("Danh sách YCNH");
        btnList.setMaxWidth(Double.MAX_VALUE);
        btnList.setOnAction(e -> showYcnhList());

        Button btnHistory = new Button("Lịch sử YCNH");
        btnHistory.setMaxWidth(Double.MAX_VALUE);
        btnHistory.setOnAction(e -> showHistory());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            SessionManager.logout();
            mainApp.showLoginScreen();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(lblMenu, btnList, btnHistory, spacer, btnLogout);
        view.setLeft(sidebar);

        // Content Area wrapper
        contentArea = new VBox();
        contentArea.setPadding(new Insets(20));

        // Sidebar toggle button
        Button btnToggle = new Button("☰ Ẩn/Hiện Menu");
        btnToggle.setOnAction(e -> {
            sidebar.setVisible(!sidebar.isVisible());
            sidebar.setManaged(sidebar.isVisible());
        });
        HBox topBar = new HBox(btnToggle);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #e0e0e0;");

        VBox rightSide = new VBox(topBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        view.setCenter(rightSide);
    }

    // ================= FLOW 1: XEM DANH SÁCH =================
    private void showYcnhList() {
        contentArea.getChildren().clear();

        Label title = new Label("Quản lý Yêu Cầu Nhập Hàng");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox toolbar = new HBox(10);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tra cứu theo Mã YCNH...");
        Button btnSearch = new Button("Tìm kiếm");
        Button btnAdd = new Button("+ Tạo YCNH mới");

        toolbar.getChildren().addAll(txtSearch, btnSearch, btnAdd);

        TableView<Ycnh> table = new TableView<>();
        setupYcnhTable(table);

        Button btnEdit = new Button("Chỉnh sửa YCNH được chọn");
        Button btnDelete = new Button("Xóa YCNH được chọn");
        btnDelete.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean noSelection = (newV == null);
            btnEdit.setDisable(noSelection);
            btnDelete.setDisable(noSelection);
        });

        Runnable loadData = () -> {
            table.setItems(fetchYcnhMaster(txtSearch.getText().toLowerCase()));
        };

        btnSearch.setOnAction(e -> loadData.run());
        btnAdd.setOnAction(e -> showAddMasterPopup(loadData));
        btnEdit.setOnAction(e -> {
            Ycnh selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isAccepted()) {
                    showAlert("Từ chối", "Đơn hàng đã được BPĐHQT chấp nhận xử lý, không thể chỉnh sửa.");
                } else {
                    showEditMasterPopup(selected, loadData);
                }
            }
        });

        btnDelete.setOnAction(e -> {
            Ycnh selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isAccepted()) {
                    showAlert("Từ chối", "Đơn hàng đã được BPĐHQT tiếp nhận, không thể xóa.");
                } else {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Xác nhận xóa");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Bạn có chắc chắn muốn xóa toàn bộ YCNH " + selected.getId() + " không?");
                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        try (Connection conn = Database.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ycnh WHERE id = ?")) {
                            pstmt.setString(1, selected.getId());
                            pstmt.executeUpdate();
                            loadData.run();
                            showSuccessPopup("Đã xóa hoàn toàn YCNH!");
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }
        });

        loadData.run();

        HBox bottomActions = new HBox(10, btnEdit, btnDelete);
        contentArea.getChildren().addAll(title, toolbar, table, bottomActions);
    }

    @SuppressWarnings("unchecked")
    private void setupYcnhTable(TableView<Ycnh> table) {
        TableColumn<Ycnh, String> colId = new TableColumn<>("Mã Yêu Cầu");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Ycnh, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Ycnh, Boolean> colAcp = new TableColumn<>("Đã duyệt(BPĐHQT)?");
        colAcp.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        TableColumn<Ycnh, String> colBy = new TableColumn<>("Người tạo");
        colBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        TableColumn<Ycnh, String> colAt = new TableColumn<>("Ngày tạo");
        colAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        table.getColumns().addAll(colId, colStatus, colAcp, colBy, colAt);
        table.setPrefHeight(300);

        table.setRowFactory(tv -> {
            TableRow<Ycnh> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    showDetailsTablePopup(row.getItem());
                }
            });
            return row;
        });
    }

    private ObservableList<Ycnh> fetchYcnhMaster(String keyword) {
        ObservableList<Ycnh> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh WHERE LOWER(id) LIKE ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Ycnh(rs.getString("id"), rs.getString("status"), 
                        rs.getBoolean("is_accepted"), rs.getString("created_by"), rs.getString("created_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private void showDetailsTablePopup(Ycnh ycnh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chi tiết: " + ycnh.getId());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<YcnhChiTiet> table = new TableView<>();
        setupDetailTable(table);
        table.setItems(fetchDetails(ycnh.getId()));

        Button btnClose = new Button("Đóng");
        btnClose.setOnAction(e -> stage.close());

        layout.getChildren().addAll(new Label("Danh sách các mặt hàng:"), table, btnClose);
        stage.setScene(new Scene(layout, 1200, 650));
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private void setupDetailTable(TableView<YcnhChiTiet> table) {
        TableColumn<YcnhChiTiet, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<YcnhChiTiet, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<YcnhChiTiet, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<YcnhChiTiet, String> colDate = new TableColumn<>("Ngày nhận");
        colDate.setCellValueFactory(new PropertyValueFactory<>("desiredDeliveryDate"));
        TableColumn<YcnhChiTiet, String> colAction = new TableColumn<>("Thay đổi");
        colAction.setCellValueFactory(new PropertyValueFactory<>("uiAction"));
        table.getColumns().addAll(colCode, colQty, colUnit, colDate, colAction);
    }

    private ObservableList<YcnhChiTiet> fetchDetails(String ycnhId) {
        ObservableList<YcnhChiTiet> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_chitiet WHERE ycnh_id = ? ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ycnhId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new YcnhChiTiet(rs.getInt("id"), rs.getString("ycnh_id"), 
                        rs.getString("merchandise_code"), rs.getInt("quantity"), 
                        rs.getString("unit"), String.valueOf(rs.getDate("desired_delivery_date"))));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ================= FLOW 2: THÊM YCNH (Master - Detail) =================
    private void showAddMasterPopup(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Yêu Cầu Nhập Hàng Mới");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextField txtId = new TextField(); txtId.setPromptText("Mã Yêu Cầu (VD: REQ-002)");
        HBox topBox = new HBox(10, new Label("Mã YCNH:"), txtId);

        // Subform for details
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã hàng");
        TextField txtQty = new TextField(); txtQty.setPromptText("SL");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("Đơn vị");
        DatePicker dpDate = new DatePicker();

        grid.addRow(0, new Label("Mã hàng:"), txtCode, new Label("Số lượng:"), txtQty);
        grid.addRow(1, new Label("Đơn vị:"), txtUnit, new Label("Ngày nhận:"), dpDate);

        ObservableList<YcnhChiTiet> detailsList = FXCollections.observableArrayList();
        TableView<YcnhChiTiet> table = new TableView<>();
        setupDetailTable(table);
        table.setItems(detailsList);

        Button btnAddRow = new Button("Thêm mặt hàng");
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
                YcnhChiTiet ct = new YcnhChiTiet(0, "", txtCode.getText(), qty, txtUnit.getText(), dpDate.getValue().toString());
                ct.setUiAction("Add");
                detailsList.add(ct);
                txtCode.clear(); txtQty.clear(); txtUnit.clear(); dpDate.setValue(null);
            } catch (Exception ex) {
                showAlert("Lỗi", "Số lượng phải là số nguyên dương.");
            }
        });

        Button btnSave = new Button("Gửi Yêu Cầu");
        btnSave.setOnAction(e -> {
            if (txtId.getText().isEmpty()) { showAlert("Lỗi", "Mã YCNH không được để trống!"); return; }
            if (detailsList.isEmpty()) { showAlert("Lỗi", "Danh sách mặt hàng trống!"); return; }

            String reqId = txtId.getText();
            String user = SessionManager.getCurrentUser().getUsername();

            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // 1. Insert Master
                    String sqlMaster = "INSERT INTO ycnh (id, created_by) VALUES (?, ?)";
                    try(PreparedStatement psM = conn.prepareStatement(sqlMaster)) {
                        psM.setString(1, reqId); psM.setString(2, user); psM.executeUpdate();
                    }

                    // 2. Insert Details
                    String sqlDetail = "INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)";
                    try(PreparedStatement psD = conn.prepareStatement(sqlDetail)) {
                        for(YcnhChiTiet ct : detailsList) {
                            psD.setString(1, reqId);
                            psD.setString(2, ct.getMerchandiseCode());
                            psD.setInt(3, ct.getQuantity());
                            psD.setString(4, ct.getUnit());
                            psD.setDate(5, java.sql.Date.valueOf(LocalDate.parse(ct.getDesiredDeliveryDate())));
                            psD.addBatch();
                        }
                        psD.executeBatch();
                    }

                    // 3. Log History
                    String sqlLog = "INSERT INTO ycnh_history (ycnh_id, action_type, changed_by, diff_text, reason) VALUES (?, ?, ?, ?, ?)";
                    try(PreparedStatement psL = conn.prepareStatement(sqlLog)) {
                        psL.setString(1, reqId); psL.setString(2, "TẠO MỚI"); psL.setString(3, user); 
                        psL.setString(4, "Tạo mới yêu cầu với " + detailsList.size() + " mặt hàng.");
                        psL.setString(5, "Tạo mới"); psL.executeUpdate();
                    }

                    conn.commit();
                    showSuccessPopup("Gửi yêu cầu thành công!");
                    stage.close();
                    onComplete.run();
                } catch (Exception ex) {
                    conn.rollback();
                    showAlert("Lỗi", "Hệ thống lỗi hoặc Mã YCNH đã tồn tại! " + ex.getMessage());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        layout.getChildren().addAll(topBox, new Separator(), grid, btnAddRow, table, btnSave);
        stage.setScene(new Scene(layout, 600, 600));
        stage.show();
    }

    // ================= FLOW 3: CHỈNH SỬA & DIFF =================
    private void showEditMasterPopup(Ycnh ycnh, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chỉnh sửa YCNH: " + ycnh.getId());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ObservableList<YcnhChiTiet> oldList = fetchDetails(ycnh.getId());
        ObservableList<YcnhChiTiet> editingList = FXCollections.observableArrayList();
        for(YcnhChiTiet c : oldList) editingList.add(c.clone());

        TableView<YcnhChiTiet> table = new TableView<>();
        setupDetailTable(table);
        table.setItems(editingList);

        // Edit existing selected row
        GridPane editGrid = new GridPane();
        editGrid.setHgap(10); editGrid.setVgap(10);
        TextField eQty = new TextField(); eQty.setPromptText("SL mới");
        DatePicker eDate = new DatePicker();
        Button btnUpdateRow = new Button("Cập nhật dòng");
        Button btnDeleteRow = new Button("Xóa dòng");
        
        btnUpdateRow.setOnAction(e -> {
            YcnhChiTiet sel = table.getSelectionModel().getSelectedItem();
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
            YcnhChiTiet sel = table.getSelectionModel().getSelectedItem();
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
        TextField aCode = new TextField(); aCode.setPromptText("Mã hàng");
        TextField aQty = new TextField(); aQty.setPromptText("SL");
        TextField aUnit = new TextField(); aUnit.setPromptText("Đơn vị");
        DatePicker aDate = new DatePicker();
        Button btnAddRow = new Button("Thêm hàng mới");

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
                YcnhChiTiet ct = new YcnhChiTiet(0, ycnh.getId(), aCode.getText(), qty, aUnit.getText(), aDate.getValue().toString());
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
        btnReview.setOnAction(e -> {
            showDiffReviewPopup(ycnh.getId(), oldList, editingList, stage, onComplete);
        });

        layout.getChildren().addAll(new Label("Tính năng thay đổi:"), table, editGrid, addGrid, btnReview);
        stage.setScene(new Scene(layout, 1200, 650));
        stage.show();
    }

    private void showDiffReviewPopup(String ycnhId, ObservableList<YcnhChiTiet> oldList, ObservableList<YcnhChiTiet> newList, Stage parentStage, Runnable onComplete) {
        long remainCount = newList.stream().filter(n -> !n.getUiAction().equals("Delete")).count();
        if (remainCount == 0) {
            showAlert("Lỗi", "Danh sách hàng không được để trống. Vui lòng hủy yêu cầu nếu không còn nhu cầu.");
            return;
        }

        StringBuilder diff = new StringBuilder();
        List<YcnhChiTiet> toInsert = new ArrayList<>();
        List<YcnhChiTiet> toUpdate = new ArrayList<>();
        List<YcnhChiTiet> toDelete = new ArrayList<>();

        for(YcnhChiTiet n : newList) {
            if(n.getUiAction().equals("Delete")) {
                diff.append("- Xóa: ").append(n.getMerchandiseCode()).append("\n");
                toDelete.add(n);
            } else if(n.getUiAction().equals("Edit")) {
                YcnhChiTiet old = oldList.stream().filter(o -> o.getId() == n.getId()).findFirst().orElse(null);
                if(old != null) {
                    if (old.getQuantity() != n.getQuantity() || !old.getDesiredDeliveryDate().equals(n.getDesiredDeliveryDate())) {
                         diff.append("~ Cập nhật ").append(n.getMerchandiseCode()).append(": ")
                             .append("SL(").append(old.getQuantity()).append("->").append(n.getQuantity()).append(") ")
                             .append("Ngày(").append(old.getDesiredDeliveryDate()).append("->").append(n.getDesiredDeliveryDate()).append(")\n");
                         toUpdate.add(n);
                    }
                }
            } else if(n.getUiAction().equals("Add")) {
                diff.append("+ Thêm: ").append(n.getMerchandiseCode()).append(" SL:").append(n.getQuantity()).append("\n");
                toInsert.add(n);
            }
        }

        if(diff.length() == 0) {
            showAlert("Thông báo", "Không có thay đổi nào để lưu!"); return;
        }

        Stage diffStage = new Stage();
        diffStage.initModality(Modality.APPLICATION_MODAL);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        TextArea txtDiff = new TextArea(diff.toString()); txtDiff.setEditable(false);
        TextField txtReason = new TextField(); txtReason.setPromptText("Nhập lý do thay đổi (Bắt buộc)");
        Button btnConfirm = new Button("Xác nhận");

        btnConfirm.setOnAction(e -> {
            if(txtReason.getText().isEmpty()) { showAlert("Lỗi", "Phải nhập lý do thay đổi!"); return; }
            applyUpdatesToDb(ycnhId, toInsert, toUpdate, toDelete, txtDiff.getText(), txtReason.getText());
            diffStage.close(); parentStage.close();
            showSuccessPopup("Cập nhật thành công!");
            onComplete.run();
        });

        layout.getChildren().addAll(new Label("Bảng So Sánh (Diff):"), txtDiff, new Label("Lý do:"), txtReason, btnConfirm);
        diffStage.setScene(new Scene(layout, 500, 400));
        diffStage.show();
    }

    private void applyUpdatesToDb(String reqId, List<YcnhChiTiet> inserts, List<YcnhChiTiet> updates, List<YcnhChiTiet> deletes, String diffText, String reason) {
        String user = SessionManager.getCurrentUser().getUsername();
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete
                try(PreparedStatement psD = conn.prepareStatement("DELETE FROM ycnh_chitiet WHERE id = ?")) {
                    for(YcnhChiTiet c : deletes) { psD.setInt(1, c.getId()); psD.addBatch(); }
                    psD.executeBatch();
                }
                // Update
                try(PreparedStatement psU = conn.prepareStatement("UPDATE ycnh_chitiet SET quantity=?, desired_delivery_date=? WHERE id=?")) {
                    for(YcnhChiTiet c : updates) { 
                        psU.setInt(1, c.getQuantity()); psU.setDate(2, java.sql.Date.valueOf(c.getDesiredDeliveryDate()));
                        psU.setInt(3, c.getId()); psU.addBatch(); 
                    }
                    psU.executeBatch();
                }
                // Add
                try(PreparedStatement psA = conn.prepareStatement("INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)")) {
                    for(YcnhChiTiet c : inserts) {
                        psA.setString(1, reqId); psA.setString(2, c.getMerchandiseCode()); psA.setInt(3, c.getQuantity());
                        psA.setString(4, c.getUnit()); psA.setDate(5, java.sql.Date.valueOf(c.getDesiredDeliveryDate()));
                        psA.addBatch();
                    }
                    psA.executeBatch();
                }
                // Log
                try(PreparedStatement psL = conn.prepareStatement("INSERT INTO ycnh_history (ycnh_id, action_type, changed_by, diff_text, reason) VALUES (?, ?, ?, ?, ?)")) {
                    psL.setString(1, reqId); psL.setString(2, "CHỈNH SỬA"); psL.setString(3, user);
                    psL.setString(4, diffText); psL.setString(5, reason); psL.executeUpdate();
                }
                conn.commit();
            } catch(Exception e) { conn.rollback(); e.printStackTrace(); }
        } catch(Exception e) { e.printStackTrace(); }
    }

    // ================= FLOW 4: LỊCH SỬ (LOG) =================
    private void showHistory() {
        contentArea.getChildren().clear();
        Label title = new Label("Lịch sử chỉnh sửa YCNH (History Log)");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<YcnhHistory> table = new TableView<>();
        TableColumn<YcnhHistory, String> colYcnh = new TableColumn<>("Mã YCNH");
        colYcnh.setCellValueFactory(new PropertyValueFactory<>("ycnhId"));
        TableColumn<YcnhHistory, String> colAction = new TableColumn<>("Hành động");
        colAction.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        TableColumn<YcnhHistory, String> colBy = new TableColumn<>("Bởi");
        colBy.setCellValueFactory(new PropertyValueFactory<>("changedBy"));
        TableColumn<YcnhHistory, String> colDiff = new TableColumn<>("Diff (So sánh)");
        colDiff.setCellValueFactory(new PropertyValueFactory<>("diffText"));
        colDiff.setPrefWidth(250);
        TableColumn<YcnhHistory, String> colReason = new TableColumn<>("Lý do");
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        TableColumn<YcnhHistory, String> colAt = new TableColumn<>("Thời gian");
        colAt.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        table.getColumns().addAll(colYcnh, colAction, colBy, colDiff, colReason, colAt);
        table.setPrefHeight(500);

        ObservableList<YcnhHistory> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_history ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new YcnhHistory(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("action_type"),
                        rs.getString("changed_by"), rs.getString("diff_text"), rs.getString("reason"), rs.getString("changed_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        table.setItems(list);
        contentArea.getChildren().addAll(title, table);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() { return view; }
}
