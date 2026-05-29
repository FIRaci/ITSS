package boundary.uc3;

import controller.uc3.ViewRequestDetailController;
import entity.uc3.RequestDetailViewModel;
import entity.chung.ImportRequest;
import entity.chung.ImportRequestDetail;
import entity.chung.ImportRequestHistory;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class RequestDetailScreen {
    private Stage stage;
    private ViewRequestDetailController controller;
    private TableView<ImportRequest> masterTable;
    private TableView<ImportRequestDetail> detailTable;
    private TableView<ImportRequestHistory> historyTable;
    private TextField txtKeyword;

    public RequestDetailScreen(Stage stage) {
        this.stage = stage;
        this.controller = new ViewRequestDetailController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        txtKeyword = new TextField();
        txtKeyword.setPromptText("Tìm kiếm theo mã yêu cầu...");
        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.setOnAction(e -> loadMasterData());

        masterTable = new TableView<>();
        TableColumn<ImportRequest, String> colId = new TableColumn<>("Mã YCNH");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ImportRequest, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        masterTable.getColumns().addAll(colId, colStatus);

        masterTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) loadDetailData(sel.getId());
        });

        detailTable = new TableView<>();
        TableColumn<ImportRequestDetail, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<ImportRequestDetail, Integer> colQty = new TableColumn<>("SL");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        detailTable.getColumns().addAll(colCode, colQty);

        Button btnDelete = new Button("Xóa yêu cầu");
        btnDelete.setOnAction(e -> deleteRequest());

        HBox searchBar = new HBox(5, txtKeyword, btnSearch);
        root.getChildren().addAll(
            new Label("XEM CHI TIẾT YÊU CẦU NHẬP HÀNG"),
            searchBar, masterTable,
            new Label("Chi tiết mặt hàng:"), detailTable,
            btnDelete
        );

        stage.setScene(new Scene(root, 700, 550));
        stage.setTitle("UC3 - Xem chi tiết yêu cầu");
        stage.show();
    }

    private void loadMasterData() {
        RequestDetailViewModel vm = controller.search(txtKeyword.getText());
        if (vm != null) masterTable.getItems().setAll(vm.getRequest() != null ? java.util.Arrays.asList(vm.getRequest()) : java.util.Collections.emptyList());
    }

    private void loadDetailData(String requestId) {
        RequestDetailViewModel vm = controller.getDetail(requestId);
        if (vm != null) {
            detailTable.getItems().setAll(vm.getDetails());
        }
    }

    private void deleteRequest() {
        ImportRequest selected = masterTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            controller.deleteRequest(selected.getId());
            masterTable.getItems().remove(selected);
            new Alert(Alert.AlertType.INFORMATION, "Đã xóa yêu cầu!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }
}
