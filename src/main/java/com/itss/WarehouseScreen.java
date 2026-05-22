package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WarehouseScreen {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;

    public WarehouseScreen(Main mainApp) {
        this.mainApp = mainApp;
        view = new BorderPane();
        
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2e7d32;");
        sidebar.setPrefWidth(220);

        Label lbl = new Label("QUẢN LÝ KHO");
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button btnIn = new Button("Đối soát Hàng Nhập Kho");
        btnIn.setMaxWidth(Double.MAX_VALUE);
        btnIn.setOnAction(e -> showCheckIn());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
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
        t.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<InternationalOrder> table = new TableView<>();
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã Lệnh Đặt"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã YCNH"); c2.setCellValueFactory(new PropertyValueFactory<>("ycnhId"));
        TableColumn<InternationalOrder, String> c3 = new TableColumn<>("Mã Hàng"); c3.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c4 = new TableColumn<>("Số lượng (CT)"); c4.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Vận chuyển"); c5.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c6 = new TableColumn<>("Trạng thái"); c6.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);

        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM international_orders ORDER BY id DESC")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(new InternationalOrder(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"), rs.getString("merchandise_code"), rs.getInt("qty"), rs.getString("shipping_method"), rs.getString("status")));
        } catch (Exception e) {}
        table.setItems(list);

        Button btnConfirm = new Button("Xác nhận nhập kho");
        Button btnReport = new Button("Lập biên bản sai lệch");
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
                updateOrderStatus(sel.getId(), "Đã nhập kho");
                showCheckIn();
            }
        });

        btnReport.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                showDiscrepancyForm(sel);
                showCheckIn();
            }
        });

        HBox actions = new HBox(10, btnConfirm, btnReport);
        contentArea.getChildren().addAll(t, table, actions);
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

        TextField txtQty = new TextField();
        txtQty.setPromptText("Số lượng sai lệch");

        TextField txtEvidence = new TextField();
        txtEvidence.setPromptText("Đường dẫn ảnh minh chứng (bắt buộc nếu hỏng)");

        TextArea txtNote = new TextArea();
        txtNote.setPromptText("Giải trình chi tiết");
        txtNote.setPrefRowCount(3);

        Button btnSave = new Button("Lưu & Gửi biên bản");
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

            saveDiscrepancy(order, cbReason.getValue(), qty, txtEvidence.getText(), txtNote.getText());
            stage.close();
        });

        layout.getChildren().addAll(new Label("Đơn hàng: " + order.getId()), new Label("Mã hàng: " + order.getMerchandiseCode()),
                cbReason, txtQty, txtEvidence, txtNote, btnSave);
        stage.setScene(new Scene(layout, 420, 420));
        stage.show();
    }

    private void saveDiscrepancy(InternationalOrder order, String reason, int qty, String evidencePath, String note) {
        String user = SessionManager.getCurrentUser().getUsername();
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psReport = conn.prepareStatement("INSERT INTO discrepancy_reports (order_id, ycnh_id, site_code, note, evidence_path, created_by) VALUES (?, ?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
                psReport.setInt(1, order.getId());
                psReport.setString(2, order.getYcnhId());
                psReport.setString(3, order.getSiteCode());
                psReport.setString(4, note);
                psReport.setString(5, evidencePath);
                psReport.setString(6, user);
                psReport.executeUpdate();

                ResultSet keys = psReport.getGeneratedKeys();
                int reportId = 0;
                if (keys.next()) reportId = keys.getInt(1);

                PreparedStatement psItem = conn.prepareStatement("INSERT INTO discrepancy_items (report_id, merchandise_code, qty_reported, reason) VALUES (?, ?, ?, ?)");
                psItem.setInt(1, reportId);
                psItem.setString(2, order.getMerchandiseCode());
                psItem.setInt(3, qty);
                psItem.setString(4, reason);
                psItem.executeUpdate();

                PreparedStatement psUpdate = conn.prepareStatement("UPDATE international_orders SET status = 'Chờ xử lý sai lệch' WHERE id = ?");
                psUpdate.setInt(1, order.getId());
                psUpdate.executeUpdate();

                conn.commit();
                showAlert("Thành công", "Đã lập biên bản sai lệch.");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateOrderStatus(int orderId, String status) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE international_orders SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
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
