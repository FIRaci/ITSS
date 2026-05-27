package com.system.infrastructure.persistence;

import com.itss.InternationalOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
