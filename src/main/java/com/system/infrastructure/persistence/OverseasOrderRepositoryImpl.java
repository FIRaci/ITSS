package com.system.infrastructure.persistence;

import com.system.domain.order.IOverseasOrderRepository;
import com.system.domain.order.OverseasOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OverseasOrderRepositoryImpl implements IOverseasOrderRepository {
    @Override
    public OverseasOrder findById(String id) {
        int orderId = parseOrderId(id);
        if (orderId <= 0) {
            return null;
        }

        String sql = "SELECT id, status, qty, site_code FROM international_orders WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new OverseasOrder(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("status"),
                    rs.getInt("qty"),
                    rs.getString("site_code")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            ps.setString(1, order.getOrderStatus());
            ps.setInt(2, orderId);
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
