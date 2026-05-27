package com.itss;
import com.system.Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SalesScreen {
    private com.system.Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    private ImportRequestController controller;

    public SalesScreen(com.system.Main mainApp) {
        this.mainApp = mainApp;
        this.controller = new ImportRequestController();
        buildView();
        showImportRequestList(); // Default view
    }

    private void buildView() {
        view = new BorderPane();
        
        // Sidebar
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lblMenu = new Label("MENU BÁN HÀNG");
        lblMenu.getStyleClass().add("sidebar-title");

        Button btnList = new Button("Danh sách ImportRequest");
        btnList.setMaxWidth(Double.MAX_VALUE);
        btnList.getStyleClass().add("sidebar-btn");
        btnList.setOnAction(e -> showImportRequestList());

        Button btnHistory = new Button("Lịch sử ImportRequest");
        btnHistory.setMaxWidth(Double.MAX_VALUE);
        btnHistory.getStyleClass().add("sidebar-btn");
        btnHistory.setOnAction(e -> showHistory());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> {
            com.system.application.auth.SessionManager.logout();
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
        btnToggle.getStyleClass().add("btn-secondary");
        btnToggle.setOnAction(e -> {
            sidebar.setVisible(!sidebar.isVisible());
            sidebar.setManaged(sidebar.isVisible());
        });
        HBox topBar = new HBox(btnToggle);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 12px 20px; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 0 0 1px 0;");

        VBox rightSide = new VBox(topBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        view.setCenter(rightSide);
    }

    // ================= FLOW 1: XEM DANH SÁCH =================
    private void showImportRequestList() {
        contentArea.getChildren().clear();

        Label title = new Label("Quản lý Yêu Cầu Nhập Hàng");
        title.getStyleClass().add("header-title");

        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("toolbar");
        TextField txtSearch = new TextField();
        txtSearch.getStyleClass().add("text-field");
        txtSearch.setPromptText("Tra cứu theo Mã ImportRequest...");
        txtSearch.setPrefWidth(300);
        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-secondary");
        Button btnAdd = new Button("+ Tạo ImportRequest mới");
        btnAdd.getStyleClass().add("btn-primary");

        toolbar.getChildren().addAll(txtSearch, btnSearch, btnAdd);

        TableView<ImportRequest> table = new TableView<>();
        table.getStyleClass().add("table-view");
        setupRequestTable(table);

        Button btnEdit = new Button("Chỉnh sửa ImportRequest được chọn");
        btnEdit.getStyleClass().add("btn-secondary");
        Button btnDelete = new Button("Xóa ImportRequest được chọn");
        btnDelete.getStyleClass().add("btn-danger");

        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean noSelection = (newV == null);
            btnEdit.setDisable(noSelection);
            btnDelete.setDisable(noSelection);
        });

        Runnable loadData = () -> {
            table.setItems(controller.getAllRequests(txtSearch.getText().toLowerCase()));
        };

        btnSearch.setOnAction(e -> loadData.run());
        btnAdd.setOnAction(e -> showAddMasterPopup(loadData));
        btnEdit.setOnAction(e -> {
            ImportRequest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isAccepted()) {
                    showAlert("Từ chối", "Đơn hàng đã được BPĐHQT chấp nhận xử lý, không thể chỉnh sửa.");
                } else {
                    showEditMasterPopup(selected, loadData);
                }
            }
        });

        btnDelete.setOnAction(e -> {
            ImportRequest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isAccepted()) {
                    showAlert("Từ chối", "Đơn hàng đã được BPĐHQT tiếp nhận, không thể xóa.");
                } else {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Xác nhận xóa");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Bạn có chắc chắn muốn xóa toàn bộ ImportRequest " + selected.getId() + " không?");
                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        try {
                            controller.deleteRequest(selected.getId());
                            loadData.run();
                            showSuccessPopup("Đã xóa hoàn toàn ImportRequest!");
                        } catch (Exception ex) {
                            showAlert("Lỗi", "Không thể xóa: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        loadData.run();

        HBox bottomActions = new HBox(12, btnEdit, btnDelete);
        VBox mainCard = new VBox(20, title, toolbar, table, bottomActions);
        mainCard.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        
        contentArea.getChildren().addAll(mainCard);
    }

    @SuppressWarnings("unchecked")
    private void setupRequestTable(TableView<ImportRequest> table) {
        TableColumn<ImportRequest, String> colId = new TableColumn<>("Mã Yêu Cầu");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ImportRequest, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<ImportRequest, Boolean> colAcp = new TableColumn<>("Đã duyệt(BPĐHQT)?");
        colAcp.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        TableColumn<ImportRequest, String> colBy = new TableColumn<>("Người tạo");
        colBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        TableColumn<ImportRequest, String> colAt = new TableColumn<>("Ngày tạo");
        colAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        table.getColumns().addAll(colId, colStatus, colAcp, colBy, colAt);
        table.setPrefHeight(300);

        table.setRowFactory(tv -> {
            TableRow<ImportRequest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    showDetailsTablePopup(row.getItem());
                }
            });
            return row;
        });
    }

    private void showDetailsTablePopup(ImportRequest importRequest) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chi tiết: " + importRequest.getId());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<ImportRequestDetail> table = new TableView<>();
        setupDetailTable(table);
        table.setItems(controller.getRequestDetails(importRequest.getId()));

        Button btnClose = new Button("Đóng");
        btnClose.getStyleClass().add("btn-secondary");
        btnClose.setOnAction(e -> stage.close());

        layout.getChildren().addAll(new Label("Danh sách các mặt hàng:"), table, btnClose);
        Scene scene = new Scene(layout, 1200, 650);
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

    // ================= FLOW 2: THÊM ImportRequest (Master - Detail) =================
    private void showAddMasterPopup(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Yêu Cầu Nhập Hàng Mới");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextField txtId = new TextField(); txtId.setPromptText("Mã Yêu Cầu (VD: REQ-002)");
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
                controller.createNewRequest(reqId, user, detailsList);
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

    // ================= FLOW 3: CHỈNH SỬA & DIFF =================
    private void showEditMasterPopup(ImportRequest importRequest, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chỉnh sửa ImportRequest: " + importRequest.getId());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ObservableList<ImportRequestDetail> oldList = controller.getRequestDetails(importRequest.getId());
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
        String diffText = controller.generateDiffText(oldList, newList);
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
                controller.updateRequest(requestId, oldList, newList, txtReason.getText(), user);
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

    // ================= FLOW 4: LỊCH SỬ (LOG) =================
    private void showHistory() {
        contentArea.getChildren().clear();
        Label title = new Label("Lịch sử chỉnh sửa ImportRequest (History Log)");
        title.getStyleClass().add("header-title");

        TableView<ImportRequestHistory> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<ImportRequestHistory, String> colYcnh = new TableColumn<>("Mã ImportRequest");
        colYcnh.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<ImportRequestHistory, String> colAction = new TableColumn<>("Hành động");
        colAction.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        TableColumn<ImportRequestHistory, String> colBy = new TableColumn<>("Bởi");
        colBy.setCellValueFactory(new PropertyValueFactory<>("changedBy"));
        TableColumn<ImportRequestHistory, String> colDiff = new TableColumn<>("Diff (So sánh)");
        colDiff.setCellValueFactory(new PropertyValueFactory<>("diffText"));
        colDiff.setPrefWidth(250);
        TableColumn<ImportRequestHistory, String> colReason = new TableColumn<>("Lý do");
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        TableColumn<ImportRequestHistory, String> colAt = new TableColumn<>("Thời gian");
        colAt.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        table.getColumns().addAll(colYcnh, colAction, colBy, colDiff, colReason, colAt);
        table.setPrefHeight(500);

        table.setItems(controller.getAllHistory());

        VBox card = new VBox(20, title, table);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() { return view; }
}
