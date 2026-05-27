package com.system.ui.auth;

import com.system.Main;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

/**
 * Màn hình đăng nhập — thiết kế split-panel:
 *  - Trái: branding/logo tối (dark sidebar)
 *  - Phải: form đăng nhập trên nền sáng
 */
public class LoginScreen {
    private Main mainApp;
    private HBox view;

    public LoginScreen(Main mainApp) {
        this.mainApp = mainApp;
        buildView();
    }

    private void buildView() {
        // ── Panel trái: Branding ───────────────────────────────────────────
        VBox brandPanel = new VBox(16);
        brandPanel.setAlignment(Pos.CENTER);
        brandPanel.setPrefWidth(420);
        brandPanel.setMinWidth(320);
        brandPanel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e3a8a);" +
            "-fx-padding: 60px 50px;"
        );

        Label logo = new Label("🏪");
        logo.setStyle("-fx-font-size: 56px;");

        Label appName = new Label("RetailApp");
        appName.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        Label tagline = new Label("Hệ thống Quản lý Bán hàng\n& Nhập hàng Quốc tế");
        tagline.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-text-alignment: center;");
        tagline.setWrapText(true);
        tagline.setAlignment(Pos.CENTER);

        // Các tính năng nổi bật
        String[] features = {
            "✓  Quản lý yêu cầu nhập hàng",
            "✓  Theo dõi đơn hàng quốc tế",
            "✓  Kiểm soát tồn kho kho",
            "✓  Báo cáo & thống kê"
        };
        VBox featureList = new VBox(10);
        featureList.setPadding(new Insets(24, 0, 0, 0));
        for (String f : features) {
            Label lbl = new Label(f);
            lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            featureList.getChildren().add(lbl);
        }

        brandPanel.getChildren().addAll(logo, appName, tagline, featureList);

        // ── Panel phải: Form đăng nhập ────────────────────────────────────
        VBox formPanel = new VBox();
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setStyle("-fx-background-color: #f1f5f9;");
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(400);
        card.setMinWidth(340);
        card.setPadding(new Insets(36));

        Label title = new Label("Đăng nhập");
        title.getStyleClass().add("header-title");

        Label subtitle = new Label("Vui lòng nhập thông tin tài khoản của bạn");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        VBox header = new VBox(6, title, subtitle);

        // ── Trường Tài khoản ─────────────────────────────────────────────
        Label lblUser = new Label("Tài khoản");
        lblUser.getStyleClass().add("label-form");
        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("text-field");
        txtUser.setPromptText("Nhập tên đăng nhập");
        txtUser.setMaxWidth(Double.MAX_VALUE);

        // ── Trường Mật khẩu ──────────────────────────────────────────────
        Label lblPass = new Label("Mật khẩu");
        lblPass.getStyleClass().add("label-form");
        PasswordField txtPass = new PasswordField();
        txtPass.getStyleClass().add("password-field");
        txtPass.setPromptText("Nhập mật khẩu");
        txtPass.setMaxWidth(Double.MAX_VALUE);

        // ── Label lỗi ────────────────────────────────────────────────────
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setVisible(false);
        lblError.setManaged(false);

        // ── Nút đăng nhập ────────────────────────────────────────────────
        Button btnLogin = new Button("Đăng nhập →");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(44);
        btnLogin.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2563eb; -fx-text-fill: white; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Runnable doLogin = () -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText();
            if (u.isEmpty() || p.isEmpty()) {
                lblError.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
                lblError.setVisible(true);
                lblError.setManaged(true);
                return;
            }
            if (com.system.application.auth.SessionManager.login(u, p)) {
                lblError.setVisible(false);
                lblError.setManaged(false);
                mainApp.navigateNext();
            } else {
                lblError.setText("❌ Sai tài khoản hoặc mật khẩu!");
                lblError.setVisible(true);
                lblError.setManaged(true);
                txtPass.clear();
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        // Enter key trong cả 2 field
        txtUser.setOnAction(e -> txtPass.requestFocus());
        txtPass.setOnAction(e -> doLogin.run());

        VBox fields = new VBox(6);
        fields.getChildren().addAll(lblUser, txtUser);
        VBox fieldsPass = new VBox(6);
        fieldsPass.getChildren().addAll(lblPass, txtPass);

        Separator sep = new Separator();

        card.getChildren().addAll(header, fields, fieldsPass, lblError, sep, btnLogin);
        formPanel.getChildren().add(card);

        // ── Ghép 2 panel ─────────────────────────────────────────────────
        view = new HBox(brandPanel, formPanel);
        view.setStyle("-fx-min-height: 520px;");

        Platform.runLater(txtUser::requestFocus);
    }

    public HBox getView() {
        return view;
    }
}
