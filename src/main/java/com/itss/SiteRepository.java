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
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
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

    public List<Site> findAllSites() {
        List<Site> list = new ArrayList<>();
        String sql = "SELECT * FROM sites ORDER BY id ASC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Site(
                    rs.getInt("id"),
                    rs.getString("site_code"),
                    rs.getString("name"),
                    rs.getInt("days_ship"),
                    rs.getInt("days_air"),
                    rs.getString("other_info")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertSite(Site site) {
        String sql = "INSERT INTO sites (site_code, name, days_ship, days_air, other_info) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, site.getSiteCode());
            ps.setString(2, site.getName());
            ps.setInt(3, site.getDaysShip());
            ps.setInt(4, site.getDaysAir());
            ps.setString(5, site.getOtherInfo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSite(Site site) {
        String sql = "UPDATE sites SET name = ?, days_ship = ?, days_air = ?, other_info = ? WHERE site_code = ?";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, site.getName());
            ps.setInt(2, site.getDaysShip());
            ps.setInt(3, site.getDaysAir());
            ps.setString(4, site.getOtherInfo());
            ps.setString(5, site.getSiteCode());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSite(String siteCode) {
        String sql = "DELETE FROM sites WHERE site_code = ?";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
