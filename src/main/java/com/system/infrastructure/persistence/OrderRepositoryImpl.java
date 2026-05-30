package com.system.infrastructure.persistence;

import com.itss.AllocationRow;
import com.itss.CancellationRequest;
import com.itss.InternationalOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrderRepositoryImpl {

    public boolean insertOrders(String requestId, List<AllocationRow> plan) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psOrder = conn.prepareStatement("INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES (?, ?, ?, ?, ?, ?)");
                     PreparedStatement psInventory = conn.prepareStatement("UPDATE site_inventory SET stock_qty = stock_qty - ? WHERE site_code = ? AND merchandise_code = ? AND stock_qty >= ?")) {
                    
                    for (AllocationRow row : plan) {
                        psOrder.setString(1, requestId);
                        psOrder.setString(2, row.getSiteCode());
                        psOrder.setString(3, row.getMerchandiseCode());
                        psOrder.setInt(4, row.getQty());
                        psOrder.setString(5, row.getShippingMethod());
                        psOrder.setString(6, "Đã đặt hàng");
                        psOrder.addBatch();
                        
                        psInventory.setInt(1, row.getQty());
                        psInventory.setString(2, row.getSiteCode());
                        psInventory.setString(3, row.getMerchandiseCode());
                        psInventory.setInt(4, row.getQty());
                        psInventory.addBatch();
                    }
                    psOrder.executeBatch();
                    int[] invResults = psInventory.executeBatch();
                    for (int res : invResults) {
                        if (res == 0) {
                            throw new Exception("Không đủ tồn kho do có giao dịch đồng thời (Negative Inventory Protection)!");
                        }
                    }
                }

                try (PreparedStatement psAcp = conn.prepareStatement("UPDATE ycnh SET is_accepted = TRUE WHERE id = ?")) {
                    psAcp.setString(1, requestId);
                    psAcp.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<InternationalOrder> findAllOrders() {
        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders ORDER BY id DESC";
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

    public ObservableList<InternationalOrder> findOrdersBySite(String siteCode) {
        ObservableList<InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders WHERE site_code = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteCode);
            ResultSet rs = ps.executeQuery();
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

    public boolean updateOrderStatus(int orderId, String status) {
        try (Connection conn = Database.getConnection();
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
        String sql = "SELECT * FROM cancellation_requests WHERE status = 'CHỜ DUYỆT' ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CancellationRequest(
                        rs.getInt("id"), rs.getInt("order_id"), rs.getString("reason"),
                        rs.getString("status"), rs.getString("created_at")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean approveCancellation(int cancelId, int orderId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement p1 = conn.prepareStatement("UPDATE cancellation_requests SET status = 'ĐÃ DUYỆT' WHERE id = ? AND status = 'CHỜ DUYỆT'")) {
                    p1.setInt(1, cancelId);
                    if (p1.executeUpdate() == 0) {
                        throw new Exception("Lỗi: Yêu cầu hủy không tồn tại hoặc đã được xử lý trước đó.");
                    }
                }

                try (PreparedStatement p2 = conn.prepareStatement("UPDATE international_orders SET status = 'Đã hủy' WHERE id = ?")) {
                    p2.setInt(1, orderId);
                    p2.executeUpdate();
                }

                // Refund stock
                try (PreparedStatement p3 = conn.prepareStatement("UPDATE site_inventory SET stock_qty = stock_qty + (SELECT qty FROM international_orders WHERE id = ?) WHERE site_code = (SELECT site_code FROM international_orders WHERE id = ?) AND merchandise_code = (SELECT merchandise_code FROM international_orders WHERE id = ?)")) {
                    p3.setInt(1, orderId);
                    p3.setInt(2, orderId);
                    p3.setInt(3, orderId);
                    p3.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean rejectCancellation(int cancelId, int orderId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement p1 = conn.prepareStatement("UPDATE cancellation_requests SET status = 'TỪ CHỐI' WHERE id = ? AND status = 'CHỜ DUYỆT'")) {
                    p1.setInt(1, cancelId);
                    if (p1.executeUpdate() == 0) {
                        throw new Exception("Lỗi: Yêu cầu hủy không tồn tại hoặc đã được xử lý trước đó.");
                    }
                }

                try (PreparedStatement p2 = conn.prepareStatement("UPDATE international_orders SET status = 'Đã đặt hàng' WHERE id = ?")) { // return back to previous status
                    p2.setInt(1, orderId);
                    p2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
