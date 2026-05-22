package com.itss;

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
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(20));

        Label title = new Label("ĐĂNG NHẬP (IMS)");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblUser = new Label("Tên đăng nhập:");
        TextField txtUser = new TextField();

        Label lblPass = new Label("Mật khẩu:");
        PasswordField txtPass = new PasswordField();

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(txtPass, 1, 1);

        Button btnLogin = new Button("Đăng nhập");
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        btnLogin.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();
            if (SessionManager.login(u, p)) {
                mainApp.navigateNext();
            } else {
                lblError.setText("Sai tài khoản hoặc mật khẩu!");
            }
        });

        view.getChildren().addAll(title, grid, btnLogin, lblError);
    }

    public VBox getView() {
        return view;
    }
}
