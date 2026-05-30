package com.system.infrastructure.persistence;

import com.system.domain.order.IOverseasOrderRepository;
import com.system.domain.order.OverseasOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OverseasOrderRepositoryImpl implements IOverseasOrderRepository {
    @Override
    public OverseasOrder findById(String id) {
        int orderId = parseOrderId(id);
        if (orderId <= 0) {
            return null;
        }

        String sql = "SELECT id, site_code, merchandise_code, qty, shipping_method, status FROM international_orders WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                OverseasOrder order = new OverseasOrder(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("site_code"),
                    rs.getString("shipping_method"),
                    rs.getString("status"),
                    rs.getInt("qty")
                );
                order.setMerchandiseId(rs.getString("merchandise_code"));
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OverseasOrder> getLinkedOrders(String requestId) {
        List<OverseasOrder> orders = new ArrayList<>();
        String sql = "SELECT id, site_code, merchandise_code, qty, shipping_method, status FROM international_orders WHERE ycnh_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OverseasOrder order = new OverseasOrder(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("site_code"),
                    rs.getString("shipping_method"),
                    rs.getString("status"),
                    rs.getInt("qty")
                );
                order.setMerchandiseId(rs.getString("merchandise_code"));
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<OverseasOrder> fetchPendingOrders() {
        List<OverseasOrder> orders = new ArrayList<>();
        String sql = "SELECT id, site_code, merchandise_code, qty, shipping_method, status FROM international_orders WHERE status = 'Đã đặt hàng'";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OverseasOrder order = new OverseasOrder(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("site_code"),
                    rs.getString("shipping_method"),
                    rs.getString("status"),
                    rs.getInt("qty")
                );
                order.setMerchandiseId(rs.getString("merchandise_code"));
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public void save(OverseasOrder order) {
        int orderId = parseOrderId(order.getOrderId());
        if (orderId <= 0) {
            return;
        }
        String sql = "UPDATE international_orders SET status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getStatus());
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        int id = parseOrderId(orderId);
        if (id <= 0) {
            return;
        }
        String sql = "UPDATE international_orders SET status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseOrderId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return -1;
        }
    }
}
