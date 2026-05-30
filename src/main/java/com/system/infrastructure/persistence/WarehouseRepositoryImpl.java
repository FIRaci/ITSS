package com.system.infrastructure.persistence;

import com.itss.InternationalOrder;
import com.system.domain.warehouse.DiscrepancyReport;
import com.system.domain.warehouse.Inventory;
import com.system.domain.warehouse.TemporaryInventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WarehouseRepositoryImpl {

    public ObservableList<InternationalOrder> getIncomingOrders() {
        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders WHERE status IN ('Đã đặt hàng', 'Đang giao') ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new InternationalOrder(
                    rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                    rs.getString("merchandise_code"), rs.getInt("qty"),
                    rs.getString("shipping_method"), rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean receiveFullOrder(int orderId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE international_orders SET status = 'Đã nhập kho' WHERE id = ? AND status IN ('Đã đặt hàng', 'Đang giao')")) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public void saveInventory(Inventory inv) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO warehouse_inventory (merchandise_code, qty, location, created_at) VALUES (?, ?, ?, datetime('now'))")) {
            ps.setString(1, inv.getSkuId());
            ps.setInt(2, inv.getCurrentQty());
            ps.setString(3, inv.getStorageLocation() != null ? inv.getStorageLocation() : "DEFAULT");
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveTemporary(TemporaryInventory temp) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO temporary_inventory (order_id, merchandise_code, qty, hold_until) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, temp.getOrderId());
            ps.setString(2, temp.getMerchandiseId());
            ps.setInt(3, temp.getSurplusQty());
            ps.setTimestamp(4, new java.sql.Timestamp(temp.getExpiryDate().getTime()));
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveReport(DiscrepancyReport report) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO discrepancy_reports (order_id, note, evidence_path, created_by, created_at) VALUES (?, ?, ?, ?, datetime('now'))")) {
            ps.setString(1, report.getOrderId());
            ps.setString(2, report.getDamageDescription());
            ps.setString(3, report.getEvidenceImage());
            ps.setString(4, "SYSTEM");
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<TemporaryInventory> findExpiredHoldings() {
        List<TemporaryInventory> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM temporary_inventory WHERE hold_until <= datetime('now') AND status = 'ACTIVE'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TemporaryInventory t = new TemporaryInventory(rs.getString("id"), 0, 0);
                t.setOrderId(rs.getString("order_id"));
                t.setSurplusQty(rs.getInt("qty"));
                list.add(t);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean reportDiscrepancy(InternationalOrder order, String reason, int qty, String evidencePath, String note, String user) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int reportId = 0;
                try (PreparedStatement psReport = conn.prepareStatement("INSERT INTO discrepancy_reports (order_id, ycnh_id, site_code, note, evidence_path, created_by) VALUES (?, ?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psReport.setInt(1, order.getId());
                    psReport.setString(2, order.getRequestId());
                    psReport.setString(3, order.getSiteCode());
                    psReport.setString(4, note);
                    psReport.setString(5, evidencePath);
                    psReport.setString(6, user);
                    psReport.executeUpdate();

                    try (ResultSet keys = psReport.getGeneratedKeys()) {
                        if (keys.next()) reportId = keys.getInt(1);
                    }
                }

                try (PreparedStatement psItem = conn.prepareStatement("INSERT INTO discrepancy_items (report_id, merchandise_code, qty_reported, reason) VALUES (?, ?, ?, ?)")) {
                    psItem.setInt(1, reportId);
                    psItem.setString(2, order.getMerchandiseCode());
                    psItem.setInt(3, qty);
                    psItem.setString(4, reason);
                    psItem.executeUpdate();
                }

                try (PreparedStatement psUpdate = conn.prepareStatement("UPDATE international_orders SET status = 'Chờ xử lý sai lệch' WHERE id = ? AND status IN ('Đã đặt hàng', 'Đang giao')")) {
                    psUpdate.setInt(1, order.getId());
                    if (psUpdate.executeUpdate() == 0) {
                        throw new Exception("Lỗi: Đơn hàng không hợp lệ hoặc đã bị thay đổi trạng thái.");
                    }
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
