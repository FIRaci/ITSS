package com.system.ui.overseas;

import com.system.Main;
import com.system.application.overseas.ManageSiteUseCase;
import com.system.application.request.ViewRequestDetailUseCase;
import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.itss.ImportRequest;
import com.itss.InternationalOrder;
import com.itss.Site;
import com.system.ui.order.CancelProcessingScreen;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class UI_OverseasDashboard {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    
    private ManageSiteUseCase manageSiteUseCase;
    private ViewRequestDetailUseCase viewRequestUseCase;
    private OrderRepositoryImpl orderRepository;

    public UI_OverseasDashboard(Main mainApp) {
        this.mainApp = mainApp;
        this.manageSiteUseCase = new ManageSiteUseCase();
        this.viewRequestUseCase = new ViewRequestDetailUseCase();
        this.orderRepository = new OrderRepositoryImpl();
        
        buildView();
        showRequestManagement();
    }

    private void buildView() {
        view = new BorderPane();
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lblMenu = new Label("BỘ PHẬN ĐẶT HÀNG QT");
        lblMenu.getStyleClass().add("sidebar-title");

        Button btnYcnh = new Button("Quản lý ImportRequest (Từ Sales)");
        btnYcnh.setMaxWidth(Double.MAX_VALUE);
        btnYcnh.getStyleClass().add("sidebar-btn");
        btnYcnh.setOnAction(e -> showRequestManagement());

        Button btnSite = new Button("Quản lý Danh Mục Site");
        btnSite.setMaxWidth(Double.MAX_VALUE);
        btnSite.getStyleClass().add("sidebar-btn");
        btnSite.setOnAction(e -> showSiteManagement());

        Button btnOrders = new Button("Đơn Hàng Đã Đặt");
        btnOrders.setMaxWidth(Double.MAX_VALUE);
        btnOrders.getStyleClass().add("sidebar-btn");
        btnOrders.setOnAction(e -> showOrders());

        Button btnCancel = new Button("Xử lý đơn hàng hủy");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.getStyleClass().add("sidebar-btn");
        btnCancel.setOnAction(e -> new CancelProcessingScreen(new Stage()).show());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> {
            com.system.application.auth.SessionManager.logout();
            mainApp.showLoginScreen();
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(lblMenu, btnYcnh, btnSite, btnOrders, btnCancel, spacer, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox(16);
        contentArea.setPadding(new Insets(24));
        contentArea.getStyleClass().add("content-area");
        VBox rightSide = new VBox(contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        view.setCenter(rightSide);
    }

    // ================= 1. QUẢN LÝ ImportRequest =================
    private void showRequestManagement() {
        contentArea.getChildren().clear();
        Label title = new Label("Danh sách ImportRequest từ bộ phận Bán Hàng");
        title.getStyleClass().add("header-title");

        TableView<ImportRequest> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<ImportRequest, String> colId = new TableColumn<>("Mã Yêu Cầu");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ImportRequest, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<ImportRequest, Boolean> colAcp = new TableColumn<>("Đã tiếp nhận?");
        colAcp.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        TableColumn<ImportRequest, String> colDate = new TableColumn<>("Ngày tạo");
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        table.getColumns().addAll(colId, colStatus, colAcp, colDate);

        table.setItems(viewRequestUseCase.getAllRequests(""));

        Button btnProcess = new Button("Tiếp nhận & Tính toán chọn Site (Gửi đơn)");
        btnProcess.getStyleClass().add("btn-primary");
        btnProcess.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> btnProcess.setDisable(nv == null || nv.isAccepted()));

        btnProcess.setOnAction(e -> {
            ImportRequest sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                new UI_OrderAllocationForm().show(sel.getId(), () -> {
                    table.setItems(viewRequestUseCase.getAllRequests(""));
                });
            }
        });

        VBox card = new VBox(20, title, table, btnProcess);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    // ================= 2. QUẢN LÝ SITE =================
    private void showSiteManagement() {
        contentArea.getChildren().clear();
        Label title = new Label("Quản lý Danh Mục Site & Thông tin vận chuyển");
        title.getStyleClass().add("header-title");

        TableView<Site> table = new TableView<>();
        table.getStyleClass().add("table-view");
        
        TableColumn<Site, String> cCode = new TableColumn<>("Mã Site");
        cCode.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<Site, String> cName = new TableColumn<>("Tên Site");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Site, Integer> cShip = new TableColumn<>("Ngày đường biển");
        cShip.setCellValueFactory(new PropertyValueFactory<>("daysShip"));
        TableColumn<Site, Integer> cAir = new TableColumn<>("Ngày hàng không");
        cAir.setCellValueFactory(new PropertyValueFactory<>("daysAir"));
        TableColumn<Site, String> cInfo = new TableColumn<>("Thông tin khác");
        cInfo.setCellValueFactory(new PropertyValueFactory<>("otherInfo"));

        table.getColumns().addAll(cCode, cName, cShip, cAir, cInfo);
        table.setItems(manageSiteUseCase.getAllSites());

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã Site");
        TextField txtName = new TextField(); txtName.setPromptText("Tên Site");
        TextField txtShip = new TextField(); txtShip.setPromptText("Ngày tàu (VD: 30)");
        TextField txtAir = new TextField(); txtAir.setPromptText("Ngày bay (VD: 5)");
        TextField txtInfo = new TextField(); txtInfo.setPromptText("Thông tin khác");
        
        Button btnAdd = new Button("Thêm Mới"); btnAdd.getStyleClass().add("btn-primary");
        Button btnUpdate = new Button("Cập Nhật"); btnUpdate.getStyleClass().add("btn-secondary");
        Button btnDelete = new Button("Xóa"); btnDelete.getStyleClass().add("btn-danger");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtCode.setText(newV.getSiteCode());
                txtName.setText(newV.getName());
                txtShip.setText(String.valueOf(newV.getDaysShip()));
                txtAir.setText(String.valueOf(newV.getDaysAir()));
                txtInfo.setText(newV.getOtherInfo());
                txtCode.setDisable(true); // Can't change code after creation
            } else {
                txtCode.setDisable(false);
            }
        });

        btnAdd.setOnAction(e -> {
            try {
                manageSiteUseCase.addSite(txtCode.getText(), txtName.getText(), Integer.parseInt(txtShip.getText()), Integer.parseInt(txtAir.getText()), txtInfo.getText());
                table.setItems(manageSiteUseCase.getAllSites());
                showSuccessPopup("Đã thêm Site.");
            } catch (Exception ex) { showAlert("Lỗi", ex.getMessage()); }
        });

        btnUpdate.setOnAction(e -> {
            try {
                int ship = Integer.parseInt(txtShip.getText());
                int air = Integer.parseInt(txtAir.getText());
                Site sel = table.getSelectionModel().getSelectedItem();
                if (sel != null && (Math.abs(sel.getDaysShip() - ship) > 10 || Math.abs(sel.getDaysAir() - air) > 5)) {
                    Alert warn = new Alert(Alert.AlertType.CONFIRMATION, "Thời gian thay đổi quá lớn. Bạn có chắc chắn?", ButtonType.YES, ButtonType.NO);
                    warn.showAndWait();
                    if (warn.getResult() != ButtonType.YES) return;
                }
                manageSiteUseCase.updateSite(txtCode.getText(), txtName.getText(), ship, air, txtInfo.getText());
                table.setItems(manageSiteUseCase.getAllSites());
                showSuccessPopup("Cập nhật Site thành công.");
            } catch (Exception ex) { showAlert("Lỗi", ex.getMessage()); }
        });

        btnDelete.setOnAction(e -> {
            try {
                manageSiteUseCase.deleteSite(txtCode.getText());
                table.setItems(manageSiteUseCase.getAllSites());
                showSuccessPopup("Xóa thành công.");
            } catch(Exception ex) { showAlert("Lỗi", ex.getMessage()); }
        });

        bottomBar.getChildren().addAll(txtCode, txtName, txtShip, txtAir, txtInfo, btnAdd, btnUpdate, btnDelete);

        VBox card = new VBox(20, title, table, bottomBar);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().add(card);
    }

    // ================= 3. DANH SÁCH ĐƠN HÀNG ĐÃ ĐẶT =================
    private void showOrders() {
        contentArea.getChildren().clear();
        Label title = new Label("Danh sách Đơn Hàng Quốc Tế");
        title.getStyleClass().add("header-title");

        TableView<InternationalOrder> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã ĐH"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã ImportRequest"); c2.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<InternationalOrder, String> c3 = new TableColumn<>("Site"); c3.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<InternationalOrder, String> c4 = new TableColumn<>("Mã hàng"); c4.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c5 = new TableColumn<>("SL"); c5.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c6 = new TableColumn<>("Vận chuyển"); c6.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c7 = new TableColumn<>("Trạng thái"); c7.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);
        table.setItems(orderRepository.findAllOrders());

        VBox card = new VBox(20, title, table);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() { return view; }
}
