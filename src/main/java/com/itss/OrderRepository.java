package com.itss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class OrderRepository {

    public boolean insertOrders(String requestId, List<AllocationRow> plan) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psOrder = conn.prepareStatement("INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES (?, ?, ?, ?, ?, ?)");
                for (AllocationRow row : plan) {
                    psOrder.setString(1, requestId);
                    psOrder.setString(2, row.getSiteCode());
                    psOrder.setString(3, row.getMerchandiseCode());
                    psOrder.setInt(4, row.getQty());
                    psOrder.setString(5, row.getShippingMethod());
                    psOrder.setString(6, "Đã đặt hàng");
                    psOrder.addBatch();
                }
                psOrder.executeBatch();

                PreparedStatement psAcp = conn.prepareStatement("UPDATE ImportRequest SET is_accepted = TRUE WHERE id = ?");
                psAcp.setString(1, requestId);
                psAcp.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<InternationalOrder> findAllOrders() {
        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders ORDER BY id DESC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new InternationalOrder(
                    rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                    rs.getString("merchandise_code"), rs.getInt("qty"),
                    rs.getString("shipping_method"), rs.getString("status"),
                    rs.getString("created_at")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<InternationalOrder> findOrdersBySite(String siteCode) {
        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders WHERE site_code = ? ORDER BY id DESC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new InternationalOrder(
                    rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                    rs.getString("merchandise_code"), rs.getInt("qty"),
                    rs.getString("shipping_method"), rs.getString("status"),
                    rs.getString("created_at")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE international_orders SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<CancellationRequest> getPendingCancellations() {
        ObservableList<CancellationRequest> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_cancellation_requests WHERE status = 'CHỜ DUYỆT' ORDER BY requested_at DESC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CancellationRequest(
                        rs.getInt("id"), rs.getInt("order_id"), rs.getString("reason"),
                        rs.getString("status"), rs.getString("requested_at")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean approveCancellation(int cancelId, int orderId) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement p1 = conn.prepareStatement("UPDATE order_cancellation_requests SET status = 'ĐÃ DUYỆT' WHERE id = ?");
                p1.setInt(1, cancelId);
                p1.executeUpdate();

                PreparedStatement p2 = conn.prepareStatement("UPDATE international_orders SET status = 'Đã hủy' WHERE id = ?");
                p2.setInt(1, orderId);
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean rejectCancellation(int cancelId, int orderId) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement p1 = conn.prepareStatement("UPDATE order_cancellation_requests SET status = 'TỪ CHỐI' WHERE id = ?");
                p1.setInt(1, cancelId);
                p1.executeUpdate();

                PreparedStatement p2 = conn.prepareStatement("UPDATE international_orders SET status = 'Đã đặt hàng' WHERE id = ?");
                p2.setInt(1, orderId);
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
