package com.system.ui.admin;

import com.system.Main;
import com.system.application.auth.ManageUsersUseCase;
import com.itss.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UI_AdminDashboard {
    private Main mainApp;
    private BorderPane view;
    private TableView<User> table;
    private ManageUsersUseCase manageUsersUseCase;

    public UI_AdminDashboard(Main mainApp) {
        this.mainApp = mainApp;
        this.manageUsersUseCase = new ManageUsersUseCase();
        
        buildView();
    }

    private void buildView() {
        view = new BorderPane();
        
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lbl = new Label("QUẢN TRỊ VIÊN");
        lbl.getStyleClass().add("sidebar-title");

        Button btnUsers = new Button("Quản lý người dùng");
        btnUsers.setMaxWidth(Double.MAX_VALUE);
        btnUsers.getStyleClass().add("sidebar-btn");
        
        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> {
            com.system.application.auth.SessionManager.logout();
            mainApp.showLoginScreen();
        });

        sidebar.getChildren().addAll(lbl, btnUsers, btnLogout);
        view.setLeft(sidebar);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(20, 24, 16, 24));
        Label title = new Label("👤 Quản lý tài khoản người dùng");
        title.getStyleClass().add("header-title");
        topBar.getChildren().add(title);

        table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> colUser = new TableColumn<>("Tài khoản");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> colRole = new TableColumn<>("Vai trò (Role)");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> colSite = new TableColumn<>("Site Code");
        colSite.setCellValueFactory(new PropertyValueFactory<>("siteCode"));

        table.getColumns().addAll(colId, colUser, colRole, colSite);
        loadData();

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(16, 4, 4, 4));
        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("text-field");
        txtUser.setPromptText("Tài khoản");
        PasswordField txtPass = new PasswordField();
        txtPass.getStyleClass().add("password-field");
        txtPass.setPromptText("Mật khẩu (trống=giữ nguyên)");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getStyleClass().add("combo-box");
        cbRole.getItems().addAll("admin", "sales", "overseas", "warehouse", "site");
        cbRole.setPromptText("Chọn vai trò...");
        cbRole.setPrefWidth(160);

        Button btnAdd = new Button("Thêm / Cập nhật Role");
        btnAdd.getStyleClass().add("btn-primary");
        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-danger");

        btnAdd.setOnAction(e -> {
            try {
                manageUsersUseCase.addOrUpdateUser(txtUser.getText(), txtPass.getText(), cbRole.getValue());
                loadData();
                showSuccessPopup("Thêm/Cập nhật thành công.");
            } catch (Exception ex) {
                showAlert("Lỗi", ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    manageUsersUseCase.deleteUser(selected.getId());
                    loadData();
                    showSuccessPopup("Xóa thành công.");
                } catch (Exception ex) {
                    showAlert("Lỗi", ex.getMessage());
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtUser.setText(newV.getUsername());
                cbRole.setValue(newV.getRole());
            }
        });

        bottomBar.getChildren().addAll(txtUser, txtPass, cbRole, btnAdd, btnDelete);

        VBox card = new VBox(20, topBar, table, bottomBar);
        card.getStyleClass().add("card");
        
        view.setCenter(card);
    }

    private void loadData() {
        table.setItems(manageUsersUseCase.getAllUsers());
    }

    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccessPopup(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }

    public BorderPane getView() {
        return view;
    }
}
