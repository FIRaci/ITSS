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
        
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lbl = new Label("SITE Äá»I TÃC");
        lbl.getStyleClass().add("sidebar-title");

        Button btnOrder = new Button("ÄÆ¡n hÃ ng nháº­n Ä‘Æ°á»£c");
        btnOrder.setMaxWidth(Double.MAX_VALUE);
        btnOrder.getStyleClass().add("sidebar-btn");
        btnOrder.setOnAction(e -> showOrders());

        Button btnLogout = new Button("ÄÄƒng xuáº¥t");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
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
        Label t = new Label("Danh sÃ¡ch Ä‘Æ¡n Ä‘áº·t hÃ ng tá»« IMS (UC06.1)");
        t.getStyleClass().add("header-title");

        TableView<InternationalOrder> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("MÃ£ Lá»‡nh"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("MÃ£ HÃ ng"); c2.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c3 = new TableColumn<>("Sá»‘ lÆ°á»£ng Cáº§n"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c4 = new TableColumn<>("Váº­n chuyá»ƒn"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Tráº¡ng thÃ¡i"); c5.setCellValueFactory(new PropertyValueFactory<>("status"));
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

        Button btnProc = new Button("Giao hÃ ng & Cáº­p nháº­t tá»“n kho");
        btnProc.getStyleClass().add("btn-primary");
        btnProc.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION); a.setContentText("Giao hÃ ng thÃ nh cÃ´ng!"); a.showAndWait();
        });

        VBox card = new VBox(20, t, table, btnProc);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    public BorderPane getView() { return view; }
}

