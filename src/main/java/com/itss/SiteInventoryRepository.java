package com.itss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SiteInventoryRepository {

    public List<SiteInventoryItem> getInventoryBySite(String siteCode) {
        List<SiteInventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM site_inventory WHERE site_code = ? ORDER BY id ASC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SiteInventoryItem(
                        rs.getInt("id"),
                        rs.getString("site_code"),
                        rs.getString("merchandise_code"),
                        rs.getInt("stock_qty")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insertInventory(SiteInventoryItem item) {
        String sql = "INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES (?, ?, ?)";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getSiteCode());
            ps.setString(2, item.getMerchandiseCode());
            ps.setInt(3, item.getStockQty());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateInventoryQty(int id, int stockQty) {
        String sql = "UPDATE site_inventory SET stock_qty = ? WHERE id = ?";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockQty);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteInventory(int id) {
        String sql = "DELETE FROM site_inventory WHERE id = ?";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
