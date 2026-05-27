package com.system.ui.warehouse;

import com.system.Main;
import com.system.application.warehouse.ReceiveOrderUseCase;
import com.itss.InternationalOrder;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UI_WarehouseDashboard {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    
    private ReceiveOrderUseCase receiveUseCase;

    public UI_WarehouseDashboard(Main mainApp) {
        this.mainApp = mainApp;
        this.receiveUseCase = new ReceiveOrderUseCase();
        
        buildView();
        showCheckIn();
    }

    private void buildView() {
        view = new BorderPane();
        
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lbl = new Label("QUẢN LÝ KHO");
        lbl.getStyleClass().add("sidebar-title");

        Button btnIn = new Button("Đối soát Hàng Nhập Kho");
        btnIn.setMaxWidth(Double.MAX_VALUE);
        btnIn.getStyleClass().add("sidebar-btn");
        btnIn.setOnAction(e -> showCheckIn());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> {
            com.system.application.auth.SessionManager.logout();
            mainApp.showLoginScreen();
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(lbl, btnIn, spacer, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        view.setCenter(contentArea);
    }

    private void showCheckIn() {
        contentArea.getChildren().clear();
        Label t = new Label("Danh sách lô hàng đang về");
        t.getStyleClass().add("header-title");

        TableView<InternationalOrder> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã Lệnh Đặt"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã ImportRequest"); c2.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<InternationalOrder, String> c3 = new TableColumn<>("Mã Hàng"); c3.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c4 = new TableColumn<>("Số lượng (CT)"); c4.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Vận chuyển"); c5.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c6 = new TableColumn<>("Trạng thái"); c6.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);

        table.setItems(receiveUseCase.getIncomingOrders());

        Button btnConfirm = new Button("Xác nhận nhập kho");
        btnConfirm.getStyleClass().add("btn-primary");
        Button btnReport = new Button("Lập biên bản sai lệch");
        btnReport.getStyleClass().add("btn-danger");
        btnConfirm.setDisable(true);
        btnReport.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            boolean noSel = (nv == null);
            btnConfirm.setDisable(noSel);
            btnReport.setDisable(noSel);
        });

        btnConfirm.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    receiveUseCase.receiveFullOrder(sel.getId());
                    showGRNPopup(sel);
                    table.setItems(receiveUseCase.getIncomingOrders());
                } catch (Exception ex) {
                    showAlert("Lỗi", ex.getMessage());
                }
            }
        });

        btnReport.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                new UI_DiscrepancyForm().show(sel, () -> {
                    table.setItems(receiveUseCase.getIncomingOrders());
                });
            }
        });

        HBox actions = new HBox(12, btnConfirm, btnReport);
        VBox card = new VBox(20, t, table, actions);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showGRNPopup(InternationalOrder order) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("In Phiếu Nhập Kho (GRN)");
        a.setHeaderText("GOODS RECEIPT NOTE (MÔ PHỎNG)");
        String content = "Mã đơn hàng: " + order.getId() + "\n"
                       + "Mã mặt hàng: " + order.getMerchandiseCode() + "\n"
                       + "Số lượng nhập: " + order.getQty() + "\n"
                       + "Trạng thái: Đã xác nhận nguyên vẹn\n\n"
                       + "*** Đang gửi tín hiệu máy in... ***";
        a.setContentText(content);
        a.showAndWait();
    }

    public BorderPane getView() { return view; }
}
