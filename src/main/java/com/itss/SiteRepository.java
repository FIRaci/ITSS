package com.itss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SiteRepository {
    
    public List<SiteStock> getSiteStocks(String merchandiseCode, LocalDate desiredDate) {
        List<SiteStock> list = new ArrayList<>();
        String sql = "SELECT s.site_code, s.days_ship, s.days_air, inv.stock_qty FROM sites s " +
                "JOIN site_inventory inv ON s.site_code = inv.site_code WHERE inv.merchandise_code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchandiseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String siteCode = rs.getString("site_code");
                int daysShip = rs.getInt("days_ship");
                int daysAir = rs.getInt("days_air");
                int stock = rs.getInt("stock_qty");

                String shippingMethod = "";
                int prefRank = 2; // 0 = Ship, 1 = Air, 2 = Not possible
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
}
