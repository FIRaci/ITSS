package boundary.admin;

import controller.admin.UserController;
import entity.chung.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class UserManagementScreen {
    private Stage stage;
    private UserController controller;
    private TableView<User> table;
    private ObservableList<User> data;

    public UserManagementScreen(Stage stage) {
        this.stage = stage;
        this.controller = new UserController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("QUẢN LÝ TÀI KHOẢN NGƯỜI DÙNG");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Form thêm mới
        HBox form = new HBox(10);
        TextField txtUsername = new TextField(); txtUsername.setPromptText("Tên đăng nhập");
        TextField txtPassword = new TextField(); txtPassword.setPromptText("Mật khẩu");
        
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("admin", "sales", "overseas", "warehouse", "site");
        cbRole.setPromptText("Vai trò (Role)");
        
        TextField txtSiteCode = new TextField(); txtSiteCode.setPromptText("Mã Site (nếu role=site)");
        Button btnAdd = new Button("Thêm mới");
        form.getChildren().addAll(txtUsername, txtPassword, cbRole, txtSiteCode, btnAdd);

        btnAdd.setOnAction(e -> {
            try {
                if (cbRole.getValue() == null) throw new Exception("Vui lòng chọn vai trò!");
                User u = new User(0, txtUsername.getText(), txtPassword.getText(), cbRole.getValue(), txtSiteCode.getText(), true);
                controller.addUser(u);
                loadData();
                txtUsername.clear(); txtPassword.clear(); cbRole.getSelectionModel().clearSelection(); txtSiteCode.clear();
                new Alert(Alert.AlertType.INFORMATION, "Thêm tài khoản thành công!").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
            }
        });

        table = new TableView<>();
        
        TableColumn<User, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));
        
        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        
        TableColumn<User, String> colRole = new TableColumn<>("Vai trò");
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        
        TableColumn<User, String> colSiteCode = new TableColumn<>("Mã Site");
        colSiteCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSiteCode()));

        TableColumn<User, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActive() ? "Hoạt động" : "Khóa"));

        TableColumn<User, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setOnAction(event -> {
                    User u = getTableView().getItems().get(getIndex());
                    try {
                        controller.toggleActiveStatus(u);
                        table.refresh();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User u = getTableView().getItems().get(getIndex());
                    btn.setText(u.isActive() ? "Khóa TK" : "Mở khóa TK");
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(colId, colUsername, colRole, colSiteCode, colStatus, colAction);

        Button btnRefresh = new Button("Làm mới danh sách");
        btnRefresh.setOnAction(e -> loadData());

        root.getChildren().addAll(title, new Label("Tạo tài khoản mới:"), form, btnRefresh, table);
        
        loadData();

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Quản lý Người dùng (Admin)");
        stage.show();
    }

    private void loadData() {
        try {
            data = FXCollections.observableArrayList(controller.getAllUsers());
            table.setItems(data);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + ex.getMessage()).showAndWait();
        }
    }
}
