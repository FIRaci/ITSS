package com.system.ui.site;

import com.system.Main;
import com.system.application.site.ManageSiteInventoryUseCase;
import com.system.application.site.ProcessSiteOrderUseCase;
import com.system.application.overseas.ManageSiteUseCase;
import com.itss.InternationalOrder;
import com.itss.Site;
import com.itss.SiteInventoryItem;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UI_SiteDashboard {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    
    private ProcessSiteOrderUseCase processOrderUseCase;
    private ManageSiteInventoryUseCase inventoryUseCase;
    private ManageSiteUseCase siteProfileUseCase;

    public UI_SiteDashboard(Main mainApp) {
        this.mainApp = mainApp;
        this.processOrderUseCase = new ProcessSiteOrderUseCase();
        this.inventoryUseCase = new ManageSiteInventoryUseCase();
        this.siteProfileUseCase = new ManageSiteUseCase();
        
        buildView();
        showOrders();
    }

    private void buildView() {
        view = new BorderPane();
        
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lbl = new Label("SITE ĐỐI TÁC");
        lbl.getStyleClass().add("sidebar-title");

        Button btnOrder = new Button("Đơn hàng nhận được");
        btnOrder.setMaxWidth(Double.MAX_VALUE);
        btnOrder.getStyleClass().add("sidebar-btn");
        btnOrder.setOnAction(e -> showOrders());

        Button btnProfile = new Button("Thông tin Site");
        btnProfile.setMaxWidth(Double.MAX_VALUE);
        btnProfile.getStyleClass().add("sidebar-btn");
        btnProfile.setOnAction(e -> showProfile());

        Button btnInventory = new Button("Cập nhật Tồn kho");
        btnInventory.setMaxWidth(Double.MAX_VALUE);
        btnInventory.getStyleClass().add("sidebar-btn");
        btnInventory.setOnAction(e -> showInventory());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> {
            com.system.application.auth.SessionManager.logout();
            mainApp.showLoginScreen();
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(lbl, btnOrder, btnInventory, btnProfile, spacer, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox(16);
        contentArea.setPadding(new Insets(24));
        contentArea.getStyleClass().add("content-area");
        view.setCenter(contentArea);
    }

    private void showProfile() {
        contentArea.getChildren().clear();
        Label t = new Label("Cập nhật thông tin Site");
        t.getStyleClass().add("header-title");

        String siteCode = com.system.application.auth.SessionManager.getCurrentUser() != null ? com.system.application.auth.SessionManager.getCurrentUser().getSiteCode() : null;
        if (siteCode == null) return;

        Site currentSite = siteProfileUseCase.getAllSites().stream().filter(s -> s.getSiteCode().equals(siteCode)).findFirst().orElse(null);

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("card");

        TextField txtName = new TextField(); txtName.setPromptText("Tên Site");
        TextField txtShip = new TextField(); txtShip.setPromptText("Ngày tàu");
        TextField txtAir = new TextField(); txtAir.setPromptText("Ngày bay");
        TextArea txtInfo = new TextArea(); txtInfo.setPromptText("Thông tin khác (Liên hệ, Địa chỉ...)");
        txtInfo.setPrefRowCount(3);

        if (currentSite != null) {
            txtName.setText(currentSite.getName());
            txtShip.setText(String.valueOf(currentSite.getDaysShip()));
            txtAir.setText(String.valueOf(currentSite.getDaysAir()));
            txtInfo.setText(currentSite.getOtherInfo());
        }

        Button btnSave = new Button("Lưu thông tin");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            try {
                int ship = Integer.parseInt(txtShip.getText());
                int air = Integer.parseInt(txtAir.getText());
                siteProfileUseCase.updateSite(siteCode, txtName.getText(), ship, air, txtInfo.getText());
                showSuccessPopup("Cập nhật thành công!");
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        form.getChildren().addAll(
            new Label("Tên Site:"), txtName,
            new Label("Thời gian vận chuyển đường biển (ngày):"), txtShip,
            new Label("Thời gian vận chuyển hàng không (ngày):"), txtAir,
            new Label("Thông tin khác:"), txtInfo,
            btnSave
        );

        contentArea.getChildren().addAll(t, form);
    }

    private void showOrders() {
        contentArea.getChildren().clear();
        Label t = new Label("Danh sách đơn đặt hàng từ IMS (UC06.1)");
        t.getStyleClass().add("header-title");

        TableView<InternationalOrder> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã Lệnh"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã Hàng"); c2.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c3 = new TableColumn<>("SL Cần"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c4 = new TableColumn<>("Vận chuyển"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Trạng thái"); c5.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5);

        String siteCode = com.system.application.auth.SessionManager.getCurrentUser() != null ? com.system.application.auth.SessionManager.getCurrentUser().getSiteCode() : null;
        table.setItems(processOrderUseCase.getOrdersForSite(siteCode));

        Button btnProc = new Button("Giao hàng & Cập nhật tồn kho");
        btnProc.getStyleClass().add("btn-primary");
        btnProc.setOnAction(e -> {
            InternationalOrder selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Cảnh báo", "Vui lòng chọn 1 đơn hàng!");
                return;
            }
            try {
                processOrderUseCase.shipOrder(selected.getId());
                showSuccessPopup("Giao hàng thành công!");
                showOrders(); // reload
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        VBox card = new VBox(20, t, table, btnProc);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showInventory() {
        contentArea.getChildren().clear();
        Label t = new Label("Quản lý Mặt hàng & Tồn kho thực tế");
        t.getStyleClass().add("header-title");

        TableView<SiteInventoryItem> table = new TableView<>();
        table.getStyleClass().add("table-view");
        
        TableColumn<SiteInventoryItem, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<SiteInventoryItem, String> cMerch = new TableColumn<>("Mã Hàng");
        cMerch.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<SiteInventoryItem, Integer> cQty = new TableColumn<>("Số lượng Tồn kho");
        cQty.setCellValueFactory(new PropertyValueFactory<>("stockQty"));

        table.getColumns().addAll(cId, cMerch, cQty);

        String siteCode = com.system.application.auth.SessionManager.getCurrentUser() != null ? com.system.application.auth.SessionManager.getCurrentUser().getSiteCode() : null;
        table.setItems(inventoryUseCase.getInventory(siteCode));

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        TextField txtMerch = new TextField(); txtMerch.setPromptText("Mã Hàng");
        TextField txtQty = new TextField(); txtQty.setPromptText("Số lượng (VD: 100)");

        Button btnAdd = new Button("Thêm Mặt Hàng"); btnAdd.getStyleClass().add("btn-primary");
        Button btnUpdate = new Button("Cập nhật Tồn kho"); btnUpdate.getStyleClass().add("btn-secondary");
        Button btnDelete = new Button("Xóa"); btnDelete.getStyleClass().add("btn-danger");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtMerch.setText(newV.getMerchandiseCode());
                txtQty.setText(String.valueOf(newV.getStockQty()));
                txtMerch.setDisable(true); // Can't change merchandise code
            } else {
                txtMerch.setDisable(false);
            }
        });

        btnAdd.setOnAction(e -> {
            try {
                int qty = Integer.parseInt(txtQty.getText());
                inventoryUseCase.addInventory(siteCode, txtMerch.getText(), qty);
                table.setItems(inventoryUseCase.getInventory(siteCode));
                showSuccessPopup("Thêm hàng thành công.");
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        btnUpdate.setOnAction(e -> {
            try {
                SiteInventoryItem sel = table.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    int qty = Integer.parseInt(txtQty.getText());
                    int oldQty = sel.getStockQty();
                    inventoryUseCase.updateInventory(sel.getId(), qty, oldQty);
                    table.setItems(inventoryUseCase.getInventory(siteCode));
                    showSuccessPopup("Cập nhật thành công.");
                }
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            try {
                SiteInventoryItem sel = table.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    inventoryUseCase.deleteInventory(sel.getId());
                    table.setItems(inventoryUseCase.getInventory(siteCode));
                    showSuccessPopup("Xóa thành công.");
                }
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        bottomBar.getChildren().addAll(txtMerch, txtQty, btnAdd, btnUpdate, btnDelete);

        VBox card = new VBox(20, t, table, bottomBar);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() { return view; }
}
