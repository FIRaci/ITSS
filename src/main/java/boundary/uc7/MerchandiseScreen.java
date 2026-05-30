package boundary.uc7;

import controller.uc7.MerchandiseController;
import entity.chung.Merchandise;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MerchandiseScreen {
    private Stage stage;
    private MerchandiseController controller;
    private TableView<Merchandise> table;
    private ObservableList<Merchandise> data;

    public MerchandiseScreen(Stage stage) {
        this.stage = stage;
        this.controller = new MerchandiseController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("QUẢN LÝ DANH MỤC MẶT HÀNG");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Form thêm mới
        HBox form = new HBox(10);
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã mặt hàng");
        TextField txtName = new TextField(); txtName.setPromptText("Tên mặt hàng");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("Đơn vị tính");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("Mô tả");
        Button btnAdd = new Button("Thêm mới");
        form.getChildren().addAll(txtCode, txtName, txtUnit, txtDesc, btnAdd);

        btnAdd.setOnAction(e -> {
            try {
                Merchandise m = new Merchandise(txtCode.getText(), txtName.getText(), txtUnit.getText(), txtDesc.getText(), "Đang kinh doanh");
                controller.addMerchandise(m);
                loadData();
                txtCode.clear(); txtName.clear(); txtUnit.clear(); txtDesc.clear();
                new Alert(Alert.AlertType.INFORMATION, "Thêm mặt hàng thành công!").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
            }
        });

        // Table
        table = new TableView<>();
        
        TableColumn<Merchandise, String> colCode = new TableColumn<>("Mã");
        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));
        
        TableColumn<Merchandise, String> colName = new TableColumn<>("Tên");
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<Merchandise, String> colUnit = new TableColumn<>("ĐVT");
        colUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnit()));
        
        TableColumn<Merchandise, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<Merchandise, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setOnAction(event -> {
                    Merchandise m = getTableView().getItems().get(getIndex());
                    try {
                        controller.toggleStatus(m);
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
                    Merchandise m = getTableView().getItems().get(getIndex());
                    btn.setText("Đang kinh doanh".equals(m.getStatus()) ? "Ngừng KD" : "Mở lại KD");
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(colCode, colName, colUnit, colStatus, colAction);

        Button btnRefresh = new Button("Làm mới danh sách");
        btnRefresh.setOnAction(e -> loadData());

        root.getChildren().addAll(title, new Label("Thêm mặt hàng mới:"), form, btnRefresh, table);
        
        loadData();

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Quản lý Mặt hàng (UC7)");
        stage.show();
    }

    private void loadData() {
        try {
            data = FXCollections.observableArrayList(controller.getAllMerchandises());
            table.setItems(data);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + ex.getMessage()).showAndWait();
        }
    }
}
