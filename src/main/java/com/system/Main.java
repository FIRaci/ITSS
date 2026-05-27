package com.system;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
        primaryStage.setScene(createStyledScene(loginScreen.getView(), 600, 450));
        primaryStage.setTitle("IMS - Login");
        primaryStage.show();
    }

    public void navigateNext() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        Scene nextScene;
        if (user.getRole().equalsIgnoreCase("admin")) {
            nextScene = createStyledScene(new AdminScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Admin Dashboard");
        } else if (user.getRole().equalsIgnoreCase("sales") || user.getRole().equalsIgnoreCase("banhang")) {
            nextScene = createStyledScene(new SalesScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Sales Dashboard - Quản lý Yêu cầu Nhập hàng");
        } else if (user.getRole().equalsIgnoreCase("overseas")) {
            nextScene = createStyledScene(new OverseasScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Overseas Order Dashboard");
        } else if (user.getRole().equalsIgnoreCase("warehouse")) {
            nextScene = createStyledScene(new WarehouseScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Warehouse Dashboard");
        } else if (user.getRole().equalsIgnoreCase("site")) {
            nextScene = createStyledScene(new SiteScreen(this).getView(), 1100, 750);
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

