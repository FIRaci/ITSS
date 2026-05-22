package com.itss;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WarehouseScreen {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;
    private WarehouseController controller;

    public WarehouseScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.controller = new WarehouseController();
        view = new BorderPane();
        
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label lbl = new Label("QUẢN LÝ KHO");
        lbl.getStyleClass().add("sidebar-title");

        Button btnIn = new Button("Đối soát Hàng Nhập Kho");
        btnIn.setMaxWidth(Double.MAX_VALUE);
        btnIn.getStyleClass().add("sidebar-btn");
        btnIn.setOnAction(e -> showCheckIn());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("sidebar-btn");
        btnLogout.setOnAction(e -> { SessionManager.logout(); mainApp.showLoginScreen(); });

        sidebar.getChildren().addAll(lbl, btnIn, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        view.setCenter(contentArea);
        
        showCheckIn();
    }

    private void showCheckIn() {
        contentArea.getChildren().clear();
        Label t = new Label("Danh sách lô hàng đang về");
        t.getStyleClass().add("header-title");

        TableView<InternationalOrder> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã Lệnh Đặt"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã ImportRequest"); c2.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<InternationalOrder, String> c3 = new TableColumn<>("Mã Hàng"); c3.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c4 = new TableColumn<>("Số lượng (CT)"); c4.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Vận chuyển"); c5.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c6 = new TableColumn<>("Trạng thái"); c6.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);

        table.setItems(controller.getIncomingOrders());

        Button btnConfirm = new Button("Xác nhận nhập kho");
        btnConfirm.getStyleClass().add("btn-primary");
        Button btnReport = new Button("Lập biên bản sai lệch");
        btnReport.getStyleClass().add("btn-danger");
        btnConfirm.setDisable(true);
        btnReport.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            boolean noSel = (nv == null);
            btnConfirm.setDisable(noSel);
            btnReport.setDisable(noSel);
        });

        btnConfirm.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if(controller.receiveFullOrder(sel.getId())) {
                    showCheckIn();
                } else {
                    showAlert("Lỗi", "Không thể cập nhật trạng thái.");
                }
            }
        });

        btnReport.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                showDiscrepancyForm(sel);
                showCheckIn();
            }
        });

        HBox actions = new HBox(12, btnConfirm, btnReport);
        VBox card = new VBox(20, t, table, actions);
        card.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().addAll(card);
    }

    private void showDiscrepancyForm(InternationalOrder order) {
        Stage stage = new Stage();
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.setTitle("Biên bản sai lệch");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        ComboBox<String> cbReason = new ComboBox<>();
        cbReason.getItems().addAll("Thiếu hàng", "Hàng hỏng/vỡ", "Sai quy cách/màu sắc");
        cbReason.setPromptText("Lý do sai lệch");
        cbReason.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6px; -fx-padding: 4px;");

        TextField txtQty = new TextField();
        txtQty.setPromptText("Số lượng sai lệch");
        txtQty.getStyleClass().add("text-field");

        TextField txtEvidence = new TextField();
        txtEvidence.setPromptText("Đường dẫn ảnh minh chứng (bắt buộc nếu hỏng)");
        txtEvidence.getStyleClass().add("text-field");

        TextArea txtNote = new TextArea();
        txtNote.setPromptText("Giải trình chi tiết");
        txtNote.getStyleClass().add("text-field");
        txtNote.setPrefRowCount(3);

        Button btnSave = new Button("Lưu & Gửi biên bản");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            if (cbReason.getValue() == null || txtQty.getText().isEmpty()) {
                showAlert("Lỗi", "Vui lòng nhập đầy đủ lý do và số lượng.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText());
                if (qty <= 0) throw new Exception();
            } catch (Exception ex) {
                showAlert("Lỗi", "Số lượng phải là số nguyên dương.");
                return;
            }
            if (cbReason.getValue().equals("Hàng hỏng/vỡ") && txtEvidence.getText().isEmpty()) {
                showAlert("Lỗi", "Vui lòng đính kèm ảnh minh chứng.");
                return;
            }

            String user = SessionManager.getCurrentUser().getUsername();
            if (controller.reportDiscrepancy(order, cbReason.getValue(), qty, txtEvidence.getText(), txtNote.getText(), user)) {
                showAlert("Thành công", "Đã lập biên bản sai lệch.");
                stage.close();
            } else {
                showAlert("Lỗi", "Có lỗi xảy ra khi lưu biên bản.");
            }
        });

        layout.getChildren().addAll(new Label("Đơn hàng: " + order.getId()), new Label("Mã hàng: " + order.getMerchandiseCode()),
                cbReason, txtQty, txtEvidence, txtNote, btnSave);
        Scene scene = new Scene(layout, 420, 420);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public BorderPane getView() { return view; }
}
