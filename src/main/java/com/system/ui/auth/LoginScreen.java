package com.system.ui.auth;
import com.system.Main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LoginScreen {
    private Main mainApp;
    private VBox view;

    public LoginScreen(Main mainApp) {
        this.mainApp = mainApp;
        buildView();
    }

    private void buildView() {
        view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(20));

        VBox card = new VBox(24);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);

        Label title = new Label("ÄÄƒng Nháº­p");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Há»‡ thá»‘ng Quáº£n lÃ½ BÃ¡n hÃ ng & Tá»“n kho");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        VBox header = new VBox(5, title, subtitle);
        header.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);

        Label lblUser = new Label("TÃ i khoáº£n");
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("text-field");
        txtUser.setPromptText("Nháº­p tÃªn Ä‘Äƒng nháº­p");

        Label lblPass = new Label("Máº­t kháº©u");
        lblPass.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        PasswordField txtPass = new PasswordField();
        txtPass.getStyleClass().add("password-field");
        txtPass.setPromptText("Nháº­p máº­t kháº©u");

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 0, 1);
        grid.add(lblPass, 0, 2);
        grid.add(txtPass, 0, 3);
        
        // Cá»‘ Ä‘á»‹nh chiá»u rá»™ng TextField
        txtUser.setPrefWidth(280);
        txtPass.setPrefWidth(280);

        Button btnLogin = new Button("ÄÄƒng nháº­p");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(40);
        
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        btnLogin.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();
            if (com.system.application.auth.SessionManager.login(u, p)) {
                mainApp.navigateNext();
            } else {
                lblError.setText("Sai tÃ i khoáº£n hoáº·c máº­t kháº©u!");
            }
        });

        card.getChildren().addAll(header, grid, btnLogin, lblError);
        view.getChildren().add(card);
    }

    public VBox getView() {
        return view;
    }
}

