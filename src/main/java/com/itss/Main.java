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
        if (user.getRole().equalsIgnoreCase("Admin")) {
            nextScene = createStyledScene(new AdminScreen(this).getView(), 1000, 700);
            primaryStage.setTitle("Admin Dashboard");
        } else if (user.getRole().equalsIgnoreCase("BÃ¡n hÃ ng") || user.getRole().equalsIgnoreCase("banhang")) {
            nextScene = createStyledScene(new SalesScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Sales Dashboard - Quáº£n lÃ½ ImportRequest");
        } else if (user.getRole().equalsIgnoreCase("Äáº·t hÃ ng quá»‘c táº¿")) {
            nextScene = createStyledScene(new OverseasScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Overseas Order Dashboard");
        } else if (user.getRole().equalsIgnoreCase("Quáº£n lÃ½ kho")) {
            nextScene = createStyledScene(new WarehouseScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Warehouse Dashboard");
        } else if (user.getRole().equalsIgnoreCase("Site")) {
            nextScene = createStyledScene(new SiteScreen(this).getView(), 1100, 750);
            primaryStage.setTitle("Site Dashboard");
        } else {
            // Placeholder for unknown roles
            VBox placeholder = new VBox(20);
            placeholder.setAlignment(Pos.CENTER);
            placeholder.getChildren().add(new Label("Welcome " + user.getRole()));
            javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("ÄÄƒng xuáº¥t");
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

