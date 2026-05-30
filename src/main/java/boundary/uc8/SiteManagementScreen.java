package boundary.uc8;

import controller.uc8.SiteController;
import entity.chung.Site;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SiteManagementScreen {
    private Stage stage;
    private SiteController controller;
    private TableView<Site> table;
    private ObservableList<Site> data;

    public SiteManagementScreen(Stage stage) {
        this.stage = stage;
        this.controller = new SiteController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("QUẢN LÝ THÔNG TIN ĐỐI TÁC (SITE)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        
        TableColumn<Site, String> colCode = new TableColumn<>("Mã Site");
        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSiteCode()));
        
        TableColumn<Site, String> colName = new TableColumn<>("Tên Site");
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<Site, Number> colShip = new TableColumn<>("Ngày đi Tàu");
        colShip.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDaysShip()));
        
        TableColumn<Site, Number> colAir = new TableColumn<>("Ngày đi Bay");
        colAir.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDaysAir()));

        TableColumn<Site, String> colInfo = new TableColumn<>("Thông tin khác");
        colInfo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOtherInfo()));

        table.getColumns().addAll(colCode, colName, colShip, colAir, colInfo);

        Button btnEdit = new Button("Chỉnh sửa Site đã chọn");
        btnEdit.setOnAction(e -> {
            Site selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn 1 Site để chỉnh sửa!").showAndWait();
                return;
            }
            showEditDialog(selected);
        });

        root.getChildren().addAll(title, btnEdit, table);
        
        loadData();

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Quản lý Site (UC8)");
        stage.show();
    }

    private void showEditDialog(Site site) {
        Stage dialog = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        TextField txtShip = new TextField(String.valueOf(site.getDaysShip()));
        TextField txtAir = new TextField(String.valueOf(site.getDaysAir()));
        TextField txtInfo = new TextField(site.getOtherInfo());

        Button btnSave = new Button("Lưu thay đổi");
        btnSave.setOnAction(e -> {
            try {
                site.setDaysShip(Integer.parseInt(txtShip.getText()));
                site.setDaysAir(Integer.parseInt(txtAir.getText()));
                site.setOtherInfo(txtInfo.getText());
                controller.updateSiteTransport(site);
                loadData();
                dialog.close();
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
            }
        });

        root.getChildren().addAll(
            new Label("Cập nhật Site: " + site.getName()),
            new Label("Số ngày đi Tàu:"), txtShip,
            new Label("Số ngày đi Bay:"), txtAir,
            new Label("Thông tin khác:"), txtInfo,
            btnSave
        );

        dialog.setScene(new Scene(root, 300, 300));
        dialog.setTitle("Sửa Site");
        dialog.showAndWait();
    }

    private void loadData() {
        try {
            data = FXCollections.observableArrayList(controller.getAllSites());
            table.setItems(data);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + ex.getMessage()).showAndWait();
        }
    }
}
