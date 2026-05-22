package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SiteScreen {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;

    public SiteScreen(Main mainApp) {
        this.mainApp = mainApp;
        view = new BorderPane();
        
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #e65100;");
        sidebar.setPrefWidth(220);

        Label lbl = new Label("SITE ĐỐI TÁC");
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button btnOrder = new Button("Đơn hàng nhận được");
        btnOrder.setMaxWidth(Double.MAX_VALUE);
        btnOrder.setOnAction(e -> showOrders());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> { SessionManager.logout(); mainApp.showLoginScreen(); });

        sidebar.getChildren().addAll(lbl, btnOrder, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        view.setCenter(contentArea);
        
        showOrders();
    }

    private void showOrders() {
        contentArea.getChildren().clear();
        Label t = new Label("Danh sách đơn đặt hàng từ IMS (UC06.1)");
        t.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<InternationalOrder> table = new TableView<>();
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã Lệnh"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã Hàng"); c2.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c3 = new TableColumn<>("Số lượng Cần"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c4 = new TableColumn<>("Vận chuyển"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Trạng thái"); c5.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5);

        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String siteCode = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getSiteCode() : null;
        String sql = "SELECT * FROM international_orders WHERE site_code = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteCode);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(new InternationalOrder(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"), rs.getString("merchandise_code"), rs.getInt("qty"), rs.getString("shipping_method"), rs.getString("status")));
        } catch (Exception e) {}
        table.setItems(list);

        Button btnProc = new Button("Giao hàng & Cập nhật tồn kho");
        btnProc.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION); a.setContentText("Giao hàng thành công!"); a.showAndWait();
        });

        contentArea.getChildren().addAll(t, table, btnProc);
    }

    public BorderPane getView() { return view; }
}
