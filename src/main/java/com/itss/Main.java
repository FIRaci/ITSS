package com.itss;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);
        primaryStage.setScene(new Scene(loginScreen.getView(), 400, 300));
        primaryStage.setTitle("IMS - Login");
        primaryStage.show();
    }

    public void navigateNext() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        Scene nextScene;
        if (user.getRole().equalsIgnoreCase("Admin")) {
            nextScene = new Scene(new AdminScreen(this).getView(), 800, 600);
            primaryStage.setTitle("Admin Dashboard");
        } else if (user.getRole().equalsIgnoreCase("Bán hàng") || user.getRole().equalsIgnoreCase("banhang")) {
            nextScene = new Scene(new SalesScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Sales Dashboard - Quản lý YCNH");
        } else if (user.getRole().equalsIgnoreCase("Đặt hàng quốc tế")) {
            nextScene = new Scene(new OverseasScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Overseas Order Dashboard");
        } else if (user.getRole().equalsIgnoreCase("Quản lý kho")) {
            nextScene = new Scene(new WarehouseScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Warehouse Dashboard");
        } else if (user.getRole().equalsIgnoreCase("Site")) {
            nextScene = new Scene(new SiteScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Site Dashboard");
        } else {
            // Placeholder for unknown roles
            VBox placeholder = new VBox(20);
            placeholder.setAlignment(Pos.CENTER);
            placeholder.getChildren().add(new Label("Welcome " + user.getRole()));
            javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("Đăng xuất");
            btnLogout.setOnAction(e -> {
                SessionManager.logout();
                showLoginScreen();
            });
            placeholder.getChildren().add(btnLogout);
            nextScene = new Scene(placeholder, 800, 600);
            primaryStage.setTitle(user.getRole() + " Dashboard");
        }

        primaryStage.setScene(nextScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
