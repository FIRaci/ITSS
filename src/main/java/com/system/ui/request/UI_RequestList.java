package com.system.ui.request;

import com.system.Main;
import com.system.application.request.ViewRequestDetailUseCase;
import com.system.ui.masterdata.MerchandiseCatalogScreen;
import com.itss.ImportRequest;
import com.itss.ImportRequestHistory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UI_RequestList {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    private ViewRequestDetailUseCase viewUseCase;

    public UI_RequestList(Main mainApp) {
        this.mainApp = mainApp;
        this.viewUseCase = new ViewRequestDetailUseCase();
        buildView();
        showImportRequestList();
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

        Button btnCatalog = new Button("📦 Quản lý Mặt hàng");
        btnCatalog.setMaxWidth(Double.MAX_VALUE);
        btnCatalog.getStyleClass().add("sidebar-btn");
        btnCatalog.setOnAction(e -> new MerchandiseCatalogScreen().show());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(lblMenu, btnList, btnHistory, btnCatalog, spacer, btnLogout);
        view.setLeft(sidebar);

        // Content Area wrapper
        contentArea = new VBox(16);
        contentArea.setPadding(new Insets(24));
        contentArea.getStyleClass().add("content-area");

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
            table.setItems(viewUseCase.getAllRequests(txtSearch.getText().toLowerCase()));
        };

        btnSearch.setOnAction(e -> loadData.run());
        btnAdd.setOnAction(e -> new CreateRequestScreen().show(loadData));
        btnEdit.setOnAction(e -> {
            ImportRequest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isAccepted()) {
                    showAlert("Từ chối", "Đơn hàng đã được BPĐHQT chấp nhận xử lý, không thể chỉnh sửa.");
                } else {
                    new EditRequestForm_GUI().show(selected, loadData);
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
                            viewUseCase.deleteRequest(selected.getId());
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
                    new UI_RequestDetail().show(row.getItem());
                }
            });
            return row;
        });
    }

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

        table.setItems(viewUseCase.getAllHistory());

        VBox card = new VBox(20, title, table);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() { return view; }
}
