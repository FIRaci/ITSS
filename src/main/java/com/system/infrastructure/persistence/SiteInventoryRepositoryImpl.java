package com.system.infrastructure.persistence;

import com.itss.SiteInventoryItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SiteInventoryRepositoryImpl {

    public List<SiteInventoryItem> getInventoryBySite(String siteCode) {
        List<SiteInventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM site_inventory WHERE site_code = ? ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
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
        String checkSql = "SELECT id, stock_qty FROM site_inventory WHERE site_code = ? AND merchandise_code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, item.getSiteCode());
            checkPs.setString(2, item.getMerchandiseCode());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int existingQty = rs.getInt("stock_qty");
                    String updateSql = "UPDATE site_inventory SET stock_qty = ? WHERE id = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setInt(1, existingQty + item.getStockQty());
                        updatePs.setInt(2, id);
                        return updatePs.executeUpdate() > 0;
                    }
                } else {
                    String sql = "INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, item.getSiteCode());
                        ps.setString(2, item.getMerchandiseCode());
                        ps.setInt(3, item.getStockQty());
                        return ps.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateInventoryQty(int id, int stockQty, int oldQty, String siteCode) {
        String sql = "UPDATE site_inventory SET stock_qty = ? WHERE id = ? AND stock_qty = ? AND site_code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockQty);
            ps.setInt(2, id);
            ps.setInt(3, oldQty);
            ps.setString(4, siteCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteInventory(int id, String siteCode) {
        String sql = "DELETE FROM site_inventory WHERE id = ? AND site_code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, siteCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
