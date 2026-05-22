package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverseasScreen {
    private Main mainApp;
    private BorderPane view;
    private VBox contentArea;

    public OverseasScreen(Main mainApp) {
        this.mainApp = mainApp;
        buildView();
        showYcnhManagement();
    }

    private void buildView() {
        view = new BorderPane();
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2b3e50;");
        sidebar.setPrefWidth(220);

        Label lblMenu = new Label("BỘ PHẬN ĐẶT HÀNG QT");
        lblMenu.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnYcnh = new Button("Quản lý YCNH (Từ Sales)");
        btnYcnh.setMaxWidth(Double.MAX_VALUE);
        btnYcnh.setOnAction(e -> showYcnhManagement());

        Button btnSite = new Button("Quản lý Danh Mục Site");
        btnSite.setMaxWidth(Double.MAX_VALUE);
        btnSite.setOnAction(e -> showSiteManagement());

        Button btnOrders = new Button("Đơn Hàng Đã Đặt");
        btnOrders.setMaxWidth(Double.MAX_VALUE);
        btnOrders.setOnAction(e -> showOrders());

        Button btnCancel = new Button("Xử lý đơn hàng hủy");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setOnAction(e -> showCancellationManagement());

        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            SessionManager.logout();
            mainApp.showLoginScreen();
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(lblMenu, btnYcnh, btnSite, btnOrders, btnCancel, spacer, btnLogout);
        view.setLeft(sidebar);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        VBox rightSide = new VBox(contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        view.setCenter(rightSide);
    }

    // ================= 1. QUẢN LÝ YCNH =================
    private void showYcnhManagement() {
        contentArea.getChildren().clear();
        Label title = new Label("Danh sách YCNH từ bộ phận Bán Hàng");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<Ycnh> table = new TableView<>();
        TableColumn<Ycnh, String> colId = new TableColumn<>("Mã Yêu Cầu");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Ycnh, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Ycnh, Boolean> colAcp = new TableColumn<>("Đã tiếp nhận?");
        colAcp.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        TableColumn<Ycnh, String> colDate = new TableColumn<>("Ngày tạo");
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        table.getColumns().addAll(colId, colStatus, colAcp, colDate);

        ObservableList<Ycnh> list = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM ycnh ORDER BY created_at DESC")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(new Ycnh(rs.getString("id"), rs.getString("status"), rs.getBoolean("is_accepted"), "", rs.getString("created_at")));
        } catch (Exception e) {}
        table.setItems(list);

        Button btnProcess = new Button("Tiếp nhận & Tính toán chọn Site (Gửi đơn)");
        btnProcess.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> btnProcess.setDisable(nv == null || nv.isAccepted()));

        btnProcess.setOnAction(e -> {
            Ycnh sel = table.getSelectionModel().getSelectedItem();
            processYcnh(sel.getId());
            showYcnhManagement(); // reload
        });

        contentArea.getChildren().addAll(title, table, btnProcess);
    }

    private void processYcnh(String ycnhId) {
        List<AllocationRow> plan = buildAllocationPlan(ycnhId);
        if (plan.isEmpty()) {
            showAlert("Không thể lập kế hoạch", "Không có phương án phù hợp hoặc dữ liệu tồn kho không đủ.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Kế hoạch chọn Site");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<AllocationRow> table = new TableView<>();
        TableColumn<AllocationRow, String> c1 = new TableColumn<>("Mã hàng"); c1.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<AllocationRow, String> c2 = new TableColumn<>("Site"); c2.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<AllocationRow, Integer> c3 = new TableColumn<>("Số lượng"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<AllocationRow, String> c4 = new TableColumn<>("Vận chuyển"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        table.getColumns().addAll(c1, c2, c3, c4);
        table.setItems(FXCollections.observableArrayList(plan));

        Button btnConfirm = new Button("Xác nhận & Gửi đơn");
        btnConfirm.setOnAction(e -> {
            if (persistOrders(ycnhId, plan)) {
                stage.close();
                showAlert("Thành công", "Đã phân bổ Site và gửi đơn hàng thành công!");
            }
        });

        layout.getChildren().addAll(new Label("Dự thảo kế hoạch:"), table, btnConfirm);
        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }

    private List<AllocationRow> buildAllocationPlan(String ycnhId) {
        List<AllocationRow> plan = new ArrayList<>();
        String sqlDetail = "SELECT merchandise_code, quantity, desired_delivery_date FROM ycnh_chitiet WHERE ycnh_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement psDetails = conn.prepareStatement(sqlDetail)) {
            psDetails.setString(1, ycnhId);
            ResultSet rs = psDetails.executeQuery();
            while (rs.next()) {
                String code = rs.getString("merchandise_code");
                int qty = rs.getInt("quantity");
                LocalDate desired = rs.getDate("desired_delivery_date").toLocalDate();

                List<SiteStock> stocks = loadSiteStocks(conn, code, desired);
                int total = stocks.stream().mapToInt(s -> s.stockQty).sum();
                if (total < qty) {
                    continue;
                }

                boolean hasFeasible = stocks.stream().anyMatch(s -> s.prefRank != 2);
                if (!hasFeasible) {
                    plan.clear();
                    return plan;
                }

                stocks.sort(Comparator.comparing((SiteStock s) -> s.prefRank).thenComparing(s -> -s.stockQty));
                int remaining = qty;
                for (SiteStock s : stocks) {
                    if (remaining <= 0) break;
                    if (s.prefRank == 2) continue;

                    int useQty = Math.min(remaining, s.stockQty);
                    plan.add(new AllocationRow(code, s.siteCode, useQty, s.shippingMethod));
                    remaining -= useQty;
                }

                if (remaining > 0) {
                    plan.clear();
                    return plan;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    private List<SiteStock> loadSiteStocks(Connection conn, String merchandiseCode, LocalDate desiredDate) {
        List<SiteStock> list = new ArrayList<>();
        String sql = "SELECT s.site_code, s.days_ship, s.days_air, inv.stock_qty FROM sites s " +
                "JOIN site_inventory inv ON s.site_code = inv.site_code WHERE inv.merchandise_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchandiseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String siteCode = rs.getString("site_code");
                int daysShip = rs.getInt("days_ship");
                int daysAir = rs.getInt("days_air");
                int stock = rs.getInt("stock_qty");

                String shippingMethod = "";
                int prefRank = 2;
                LocalDate today = LocalDate.now();
                if (!today.plusDays(daysShip).isAfter(desiredDate)) {
                    shippingMethod = "Đường Biển";
                    prefRank = 0;
                } else if (!today.plusDays(daysAir).isAfter(desiredDate)) {
                    shippingMethod = "Hàng Không";
                    prefRank = 1;
                }
                list.add(new SiteStock(siteCode, stock, shippingMethod, prefRank, daysShip, daysAir));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean persistOrders(String ycnhId, List<AllocationRow> plan) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psOrder = conn.prepareStatement("INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES (?, ?, ?, ?, ?, ?)");
                for (AllocationRow row : plan) {
                    psOrder.setString(1, ycnhId);
                    psOrder.setString(2, row.getSiteCode());
                    psOrder.setString(3, row.getMerchandiseCode());
                    psOrder.setInt(4, row.getQty());
                    psOrder.setString(5, row.getShippingMethod());
                    psOrder.setString(6, "Đã đặt hàng");
                    psOrder.addBatch();
                }
                psOrder.executeBatch();

                PreparedStatement psAcp = conn.prepareStatement("UPDATE ycnh SET is_accepted = true, status = 'Đã gửi Site' WHERE id = ?");
                psAcp.setString(1, ycnhId); psAcp.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ================= 2. QUẢN LÝ SITE =================
    private void showSiteManagement() {
        contentArea.getChildren().clear();
        Label title = new Label("Danh mục Site nhập khẩu");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<SiteModel> table = new TableView<>();
        TableColumn<SiteModel, String> colCode = new TableColumn<>("Mã Site"); colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        TableColumn<SiteModel, String> colName = new TableColumn<>("Tên Site"); colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<SiteModel, Integer> colShip = new TableColumn<>("Ngày tàu"); colShip.setCellValueFactory(new PropertyValueFactory<>("daysShip"));
        TableColumn<SiteModel, Integer> colAir = new TableColumn<>("Ngày bay"); colAir.setCellValueFactory(new PropertyValueFactory<>("daysAir"));
        table.getColumns().addAll(colCode, colName, colShip, colAir);

        ObservableList<SiteModel> list = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM sites ORDER BY id ASC")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(new SiteModel(rs.getString("site_code"), rs.getString("name"), rs.getInt("days_ship"), rs.getInt("days_air")));
        } catch (Exception e) {}
        table.setItems(list);

        Button btnAdd = new Button("+ Thêm mới Site");
        btnAdd.setOnAction(e -> {
            showAddSitePopup(() -> showSiteManagement());
        });

        Button btnUpdateTransport = new Button("Cập nhật vận chuyển");
        btnUpdateTransport.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> btnUpdateTransport.setDisable(nv == null));
        btnUpdateTransport.setOnAction(e -> {
            SiteModel sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) showUpdateTransportPopup(sel, () -> showSiteManagement());
        });

        HBox actions = new HBox(10, btnAdd, btnUpdateTransport);
        contentArea.getChildren().addAll(title, table, actions);
    }

    // ================= 3. ĐƠN HÀNG ĐÃ ĐẶT =================
    private void showOrders() {
        contentArea.getChildren().clear();
        Label title = new Label("Trạng thái lệnh chuyển hàng");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<InternationalOrder> table = new TableView<>();
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã lệnh"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã YCNH"); c2.setCellValueFactory(new PropertyValueFactory<>("ycnhId"));
        TableColumn<InternationalOrder, String> c3 = new TableColumn<>("Mã hàng"); c3.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c4 = new TableColumn<>("Số lượng"); c4.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c5 = new TableColumn<>("Site"); c5.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<InternationalOrder, String> c6 = new TableColumn<>("Vận chuyển"); c6.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        TableColumn<InternationalOrder, String> c7 = new TableColumn<>("Trạng thái"); c7.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM international_orders ORDER BY id DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new InternationalOrder(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                        rs.getString("merchandise_code"), rs.getInt("qty"), rs.getString("shipping_method"), rs.getString("status")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        table.setItems(list);

        contentArea.getChildren().addAll(title, table);
    }

    private void showCancellationManagement() {
        contentArea.getChildren().clear();
        Label title = new Label("Xử lý đơn hàng hủy / lỗi cung ứng");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<InternationalOrder> table = new TableView<>();
        TableColumn<InternationalOrder, Integer> c1 = new TableColumn<>("Mã lệnh"); c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> c2 = new TableColumn<>("Mã hàng"); c2.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> c3 = new TableColumn<>("Số lượng"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<InternationalOrder, String> c4 = new TableColumn<>("Trạng thái"); c4.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(c1, c2, c3, c4);

        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders WHERE status IN ('Yêu cầu hủy', 'Lỗi cung ứng') ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new InternationalOrder(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                        rs.getString("merchandise_code"), rs.getInt("qty"), rs.getString("shipping_method"), rs.getString("status")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        table.setItems(list);

        Button btnPlan = new Button("Lập kế hoạch thay thế");
        Button btnCancel = new Button("Xác nhận hủy đơn hàng");
        btnPlan.setDisable(true);
        btnCancel.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            boolean noSel = (nv == null);
            btnPlan.setDisable(noSel);
            btnCancel.setDisable(noSel);
        });

        btnPlan.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                showReplacementPlan(sel, () -> showCancellationManagement());
            }
        });

        btnCancel.setOnAction(e -> {
            InternationalOrder sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                handleCancelOrder(sel, () -> showCancellationManagement());
            }
        });

        HBox actions = new HBox(10, btnPlan, btnCancel);
        contentArea.getChildren().addAll(title, table, actions);
    }

    private void showReplacementPlan(InternationalOrder order, Runnable onComplete) {
        LocalDate desiredDate = loadDesiredDate(order.getYcnhId(), order.getMerchandiseCode());
        if (desiredDate == null) {
            showAlert("Lỗi", "Không tìm thấy ngày nhận mong muốn.");
            return;
        }

        List<AllocationRow> plan = buildAllocationPlanForItem(order.getMerchandiseCode(), order.getQty(), desiredDate);
        if (plan.isEmpty()) {
            showAlert("Không thể lập kế hoạch", "Không có Site nào kịp giao hàng hoặc không đủ tồn kho.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Kế hoạch thay thế");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<AllocationRow> table = new TableView<>();
        TableColumn<AllocationRow, String> c1 = new TableColumn<>("Mã hàng"); c1.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<AllocationRow, String> c2 = new TableColumn<>("Site"); c2.setCellValueFactory(new PropertyValueFactory<>("siteCode"));
        TableColumn<AllocationRow, Integer> c3 = new TableColumn<>("Số lượng"); c3.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<AllocationRow, String> c4 = new TableColumn<>("Vận chuyển"); c4.setCellValueFactory(new PropertyValueFactory<>("shippingMethod"));
        table.getColumns().addAll(c1, c2, c3, c4);
        table.setItems(FXCollections.observableArrayList(plan));

        Button btnConfirm = new Button("Phê duyệt kế hoạch");
        btnConfirm.setOnAction(e -> {
            if (persistReplacementOrders(order, plan)) {
                stage.close();
                onComplete.run();
            }
        });

        layout.getChildren().addAll(new Label("Dự thảo kế hoạch thay thế:"), table, btnConfirm);
        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }

    private List<AllocationRow> buildAllocationPlanForItem(String code, int qty, LocalDate desired) {
        List<AllocationRow> plan = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            List<SiteStock> stocks = loadSiteStocks(conn, code, desired);
            int total = stocks.stream().mapToInt(s -> s.stockQty).sum();
            if (total < qty) return plan;
            boolean hasFeasible = stocks.stream().anyMatch(s -> s.prefRank != 2);
            if (!hasFeasible) return plan;

            stocks.sort(Comparator.comparing((SiteStock s) -> s.prefRank).thenComparing(s -> -s.stockQty));
            int remaining = qty;
            for (SiteStock s : stocks) {
                if (remaining <= 0) break;
                if (s.prefRank == 2) continue;
                int useQty = Math.min(remaining, s.stockQty);
                plan.add(new AllocationRow(code, s.siteCode, useQty, s.shippingMethod));
                remaining -= useQty;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return plan;
    }

    private LocalDate loadDesiredDate(String ycnhId, String merchandiseCode) {
        String sql = "SELECT desired_delivery_date FROM ycnh_chitiet WHERE ycnh_id = ? AND merchandise_code = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ycnhId);
            ps.setString(2, merchandiseCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDate("desired_delivery_date").toLocalDate();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private boolean persistReplacementOrders(InternationalOrder oldOrder, List<AllocationRow> plan) {
        String user = SessionManager.getCurrentUser().getUsername();
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psOrder = conn.prepareStatement("INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES (?, ?, ?, ?, ?, ?)");
                for (AllocationRow row : plan) {
                    psOrder.setString(1, oldOrder.getYcnhId());
                    psOrder.setString(2, row.getSiteCode());
                    psOrder.setString(3, row.getMerchandiseCode());
                    psOrder.setInt(4, row.getQty());
                    psOrder.setString(5, row.getShippingMethod());
                    psOrder.setString(6, "Đã lập kế hoạch");
                    psOrder.addBatch();
                }
                psOrder.executeBatch();

                PreparedStatement psUpdate = conn.prepareStatement("UPDATE international_orders SET status = 'Đã hủy' WHERE id = ?");
                psUpdate.setInt(1, oldOrder.getId());
                psUpdate.executeUpdate();

                PreparedStatement psCancel = conn.prepareStatement("INSERT INTO cancellation_requests (order_id, reason, status, created_by, handled_by, handled_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)");
                psCancel.setInt(1, oldOrder.getId());
                psCancel.setString(2, "Đã lập kế hoạch thay thế");
                psCancel.setString(3, "Đã lập kế hoạch");
                psCancel.setString(4, user);
                psCancel.setString(5, user);
                psCancel.executeUpdate();

                conn.commit();
                showAlert("Thành công", "Đã lập kế hoạch thay thế.");
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private void handleCancelOrder(InternationalOrder order, Runnable onComplete) {
        String user = SessionManager.getCurrentUser().getUsername();
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psUpdate = conn.prepareStatement("UPDATE international_orders SET status = 'Đã hủy' WHERE id = ?");
                psUpdate.setInt(1, order.getId());
                psUpdate.executeUpdate();

                PreparedStatement psCancel = conn.prepareStatement("INSERT INTO cancellation_requests (order_id, reason, status, created_by, handled_by, handled_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)");
                psCancel.setInt(1, order.getId());
                psCancel.setString(2, "Xác nhận hủy đơn hàng");
                psCancel.setString(3, "Đã hủy");
                psCancel.setString(4, user);
                psCancel.setString(5, user);
                psCancel.executeUpdate();

                conn.commit();
                onComplete.run();
                showAlert("Thành công", "Đã hủy đơn hàng.");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public BorderPane getView() { return view; }

    public static class SiteModel {
        private String code; private String name; private int daysShip; private int daysAir;
        public SiteModel(String c, String n, int daysShip, int daysAir) { code = c; name = n; this.daysShip = daysShip; this.daysAir = daysAir; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public int getDaysShip() { return daysShip; }
        public int getDaysAir() { return daysAir; }
    }

    private void showAddSitePopup(Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Thêm mới Site");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField txtCode = new TextField(); txtCode.setPromptText("Mã Site");
        TextField txtName = new TextField(); txtName.setPromptText("Tên Site");
        TextField txtShip = new TextField(); txtShip.setPromptText("Ngày tàu");
        TextField txtAir = new TextField(); txtAir.setPromptText("Ngày bay");
        TextArea txtInfo = new TextArea(); txtInfo.setPromptText("Thông tin khác");
        txtInfo.setPrefRowCount(3);

        Button btnSave = new Button("Lưu thông tin");
        btnSave.setOnAction(e -> {
            if (txtCode.getText().isEmpty() || txtName.getText().isEmpty() || txtShip.getText().isEmpty() || txtAir.getText().isEmpty()) {
                showAlert("Thiếu dữ liệu", "Vui lòng nhập đầy đủ các trường bắt buộc.");
                return;
            }
            int daysShip; int daysAir;
            try {
                daysShip = Integer.parseInt(txtShip.getText());
                daysAir = Integer.parseInt(txtAir.getText());
                if (daysShip <= 0 || daysAir <= 0) throw new Exception();
            } catch (Exception ex) {
                showAlert("Lỗi", "Số ngày vận chuyển phải là số nguyên dương.");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                PreparedStatement check = conn.prepareStatement("SELECT 1 FROM sites WHERE site_code = ?");
                check.setString(1, txtCode.getText());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    showAlert("Lỗi", "Mã Site đã tồn tại.");
                    return;
                }

                PreparedStatement ps = conn.prepareStatement("INSERT INTO sites (site_code, name, days_ship, days_air, other_info) VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, txtCode.getText());
                ps.setString(2, txtName.getText());
                ps.setInt(3, daysShip);
                ps.setInt(4, daysAir);
                ps.setString(5, txtInfo.getText());
                ps.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Không thể lưu thông tin Site.");
                return;
            }

            stage.close();
            onComplete.run();
            showAlert("Thành công", "Thêm mới Site nhập khẩu thành công!");
        });

        layout.getChildren().addAll(new Label("Mã Site"), txtCode, new Label("Tên Site"), txtName,
                new Label("Ngày tàu"), txtShip, new Label("Ngày bay"), txtAir, new Label("Thông tin khác"), txtInfo, btnSave);
        stage.setScene(new Scene(layout, 400, 520));
        stage.show();
    }

    private void showUpdateTransportPopup(SiteModel site, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Cập nhật vận chuyển Site");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField txtShip = new TextField(String.valueOf(site.getDaysShip()));
        TextField txtAir = new TextField(String.valueOf(site.getDaysAir()));
        TextArea txtNote = new TextArea(); txtNote.setPromptText("Ghi chú lý do thay đổi");
        txtNote.setPrefRowCount(3);

        Button btnSave = new Button("Xác nhận cập nhật");
        btnSave.setOnAction(e -> {
            int newShip; int newAir;
            try {
                newShip = Integer.parseInt(txtShip.getText());
                newAir = Integer.parseInt(txtAir.getText());
                if (newShip <= 0 || newAir <= 0) throw new Exception();
            } catch (Exception ex) {
                showAlert("Lỗi", "Số ngày vận chuyển phải là số nguyên dương.");
                return;
            }

            boolean abnormal = isAbnormalChange(site.getDaysShip(), newShip) || isAbnormalChange(site.getDaysAir(), newAir);
            if (abnormal) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Cảnh báo");
                confirm.setHeaderText(null);
                confirm.setContentText("Thông tin vận chuyển thay đổi bất thường. Bạn có chắc chắn muốn lưu?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
            }

            String user = SessionManager.getCurrentUser().getUsername();
            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    PreparedStatement ps = conn.prepareStatement("UPDATE sites SET days_ship = ?, days_air = ? WHERE site_code = ?");
                    ps.setInt(1, newShip); ps.setInt(2, newAir); ps.setString(3, site.getCode());
                    ps.executeUpdate();

                    PreparedStatement log = conn.prepareStatement("INSERT INTO site_transport_log (site_code, old_days_ship, new_days_ship, old_days_air, new_days_air, note, changed_by) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    log.setString(1, site.getCode());
                    log.setInt(2, site.getDaysShip()); log.setInt(3, newShip);
                    log.setInt(4, site.getDaysAir()); log.setInt(5, newAir);
                    log.setString(6, txtNote.getText()); log.setString(7, user);
                    log.executeUpdate();

                    conn.commit();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Không thể cập nhật thông tin vận chuyển.");
                return;
            }

            stage.close();
            onComplete.run();
            showAlert("Thành công", "Cập nhật thành công.");
        });

        layout.getChildren().addAll(new Label("Site: " + site.getCode()), new Label("Ngày tàu"), txtShip,
                new Label("Ngày bay"), txtAir, new Label("Ghi chú"), txtNote, btnSave);
        stage.setScene(new Scene(layout, 400, 420));
        stage.show();
    }

    private boolean isAbnormalChange(int oldValue, int newValue) {
        if (oldValue <= 0) return false;
        double diff = Math.abs(newValue - oldValue) / (double) oldValue;
        return diff >= 0.3;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class AllocationRow {
        private String merchandiseCode;
        private String siteCode;
        private int qty;
        private String shippingMethod;

        public AllocationRow(String merchandiseCode, String siteCode, int qty, String shippingMethod) {
            this.merchandiseCode = merchandiseCode;
            this.siteCode = siteCode;
            this.qty = qty;
            this.shippingMethod = shippingMethod;
        }

        public String getMerchandiseCode() { return merchandiseCode; }
        public String getSiteCode() { return siteCode; }
        public int getQty() { return qty; }
        public String getShippingMethod() { return shippingMethod; }
    }

    private static class SiteStock {
        private String siteCode;
        private int stockQty;
        private String shippingMethod;
        private int prefRank;
        private int daysShip;
        private int daysAir;

        public SiteStock(String siteCode, int stockQty, String shippingMethod, int prefRank, int daysShip, int daysAir) {
            this.siteCode = siteCode;
            this.stockQty = stockQty;
            this.shippingMethod = shippingMethod;
            this.prefRank = prefRank;
            this.daysShip = daysShip;
            this.daysAir = daysAir;
        }
    }
}
