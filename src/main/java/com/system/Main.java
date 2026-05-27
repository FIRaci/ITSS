package com.system;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.itss.*;
import com.system.ui.auth.LoginScreen;
import com.system.application.auth.SessionManager;
import com.itss.User;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    public void showSalesScreen() {
        com.system.ui.request.UI_RequestList ui = new com.system.ui.request.UI_RequestList(this);
        Scene scene = new Scene(ui.getView(), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bộ phận Bán hàng - Retail App");
    }

    private Scene createStyledScene(javafx.scene.Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load style.css");
        }
        return scene;
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);
        primaryStage.setScene(createStyledScene(loginScreen.getView(), 900, 560));
        primaryStage.setTitle("RetailApp — Đăng nhập");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(480);
        primaryStage.show();
    }

    public void navigateNext() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        Scene nextScene;
        if (user.getRole().equalsIgnoreCase("admin")) {
            nextScene = createStyledScene(new com.system.ui.admin.UI_AdminDashboard(this).getView(), 1000, 700);
            primaryStage.setTitle("Admin Dashboard");
        } else if (user.getRole().equalsIgnoreCase("sales") || user.getRole().equalsIgnoreCase("banhang")) {
            nextScene = createStyledScene(new com.system.ui.request.UI_RequestList(this).getView(), 1100, 750);
            primaryStage.setTitle("Sales Dashboard - Quản lý Yêu cầu Nhập hàng");
        } else if (user.getRole().equalsIgnoreCase("overseas")) {
            nextScene = createStyledScene(new com.system.ui.overseas.UI_OverseasDashboard(this).getView(), 1100, 750);
            primaryStage.setTitle("Overseas Order Dashboard");
        } else if (user.getRole().equalsIgnoreCase("warehouse")) {
            nextScene = createStyledScene(new com.system.ui.warehouse.UI_WarehouseDashboard(this).getView(), 1100, 750);
            primaryStage.setTitle("Warehouse Dashboard");
        } else if (user.getRole().equalsIgnoreCase("site")) {
            nextScene = createStyledScene(new com.system.ui.site.UI_SiteDashboard(this).getView(), 1100, 750);
            primaryStage.setTitle("Site Dashboard");
        } else {
            VBox placeholder = new VBox(20);
            placeholder.setAlignment(Pos.CENTER);
            placeholder.getChildren().add(new Label("Welcome " + user.getRole()));
            javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("Đăng xuất");
            btnLogout.setOnAction(e -> {
                SessionManager.logout();
                showLoginScreen();
            });
            placeholder.getChildren().add(btnLogout);
            nextScene = createStyledScene(placeholder, 800, 600);
            primaryStage.setTitle(user.getRole() + " Dashboard");
        }

        primaryStage.setScene(nextScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

