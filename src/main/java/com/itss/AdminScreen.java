package com.itss;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminScreen {
    private Main mainApp;
    private BorderPane view;
    private TableView<User> table;
    private ObservableList<User> userList;
    private UserController userController;

    public AdminScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.userController = new UserController();
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
        btnLogout.setOnAction(e -> { SessionManager.logout(); mainApp.showLoginScreen(); });

        sidebar.getChildren().addAll(lbl, btnUsers, btnLogout);
        view.setLeft(sidebar);

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(20));
        Label title = new Label("Quản lý tài khoản người dùng");
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
        bottomBar.setPadding(new Insets(20));
        TextField txtUser = new TextField();
        txtUser.setPromptText("Tài khoản");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu (trống=giữ nguyên)");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("admin", "sales", "overseas", "warehouse", "site");
        cbRole.setPromptText("Vai trò");

        Button btnAdd = new Button("Thêm / Cập nhật Role");
        btnAdd.getStyleClass().add("btn-primary");
        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-danger");

        btnAdd.setOnAction(e -> {
            userController.addOrUpdateUser(txtUser.getText(), txtPass.getText(), cbRole.getValue());
            loadData();
        });

        btnDelete.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userController.deleteUser(selected.getId());
                loadData();
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
        userList = userController.getAllUsers();
        table.setItems(userList);
    }

    public BorderPane getView() {
        return view;
    }
}
