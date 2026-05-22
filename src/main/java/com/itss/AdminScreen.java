package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminScreen {
    private Main mainApp;
    private VBox view;
    private TableView<User> table;
    private ObservableList<User> userList;

    public AdminScreen(Main mainApp) {
        this.mainApp = mainApp;
        buildView();
        loadData();
    }

    private void buildView() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        HBox topBar = new HBox(20);
        Label title = new Label("Quáº£n LÃ½ TÃ i Khoáº£n / Roles");
        title.getStyleClass().add("header-title");
        Button btnLogout = new Button("ÄÄƒng xuáº¥t");
        btnLogout.getStyleClass().add("btn-secondary");
        btnLogout.setOnAction(e -> {
            SessionManager.logout();
            mainApp.showLoginScreen();
        });
        topBar.getChildren().addAll(title, btnLogout);

        table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<User, String> colUser = new TableColumn<>("TÃªn Ä‘Äƒng nháº­p");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<User, String> colRole = new TableColumn<>("PhÃ¢n Quyá»n (Role)");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        table.getColumns().addAll(colId, colUser, colRole);
        table.setPrefHeight(400);

        HBox bottomBar = new HBox(12);
        TextField txtUser = new TextField();
        txtUser.setPromptText("TÃªn Ä‘Äƒng nháº­p");
        txtUser.getStyleClass().add("text-field");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Máº­t kháº©u");
        txtPass.getStyleClass().add("password-field");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("BÃ¡n hÃ ng", "Äáº·t hÃ ng quá»‘c táº¿", "Quáº£n lÃ½ kho", "Site", "Admin");
        cbRole.setValue("BÃ¡n hÃ ng");
        cbRole.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6px; -fx-padding: 4px;");

        Button btnAdd = new Button("ThÃªm / Cáº­p nháº­t Role");
        btnAdd.getStyleClass().add("btn-primary");
        Button btnDelete = new Button("XÃ³a");
        btnDelete.getStyleClass().add("btn-danger");

        btnAdd.setOnAction(e -> {
            addOrUpdateUser(txtUser.getText(), txtPass.getText(), cbRole.getValue());
            loadData();
        });

        btnDelete.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteUser(selected.getId());
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
        
        view.getChildren().addAll(card);
    }

    private void loadData() {
        userList = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id ASC")) {
            while (rs.next()) {
                userList.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"), rs.getString("site_code")));
            }
            table.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrUpdateUser(String user, String pass, String role) {
        if(user.isEmpty()) return;
        try (Connection conn = Database.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            check.setString(1, user);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                // update role (and password if not empty)
                String sql = pass.isEmpty() ? "UPDATE users SET role = ? WHERE username = ?" 
                                            : "UPDATE users SET role = ?, password = ? WHERE username = ?";
                PreparedStatement update = conn.prepareStatement(sql);
                update.setString(1, role);
                if (pass.isEmpty()) {
                    update.setString(2, user);
                } else {
                    update.setString(2, pass);
                    update.setString(3, user);
                }
                update.executeUpdate();
            } else {
                // insert
                if(pass.isEmpty()) pass = user + "123";
                PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                insert.setString(1, user);
                insert.setString(2, pass);
                insert.setString(3, role);
                insert.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        return view;
    }
}

