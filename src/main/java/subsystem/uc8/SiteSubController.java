package subsystem.uc8;

import com.system.infrastructure.persistence.Database;
import entity.chung.Site;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SiteSubController {
    public List<Site> getAllSites() throws Exception {
        List<Site> list = new ArrayList<>();
        String sql = "SELECT id, site_code, name, days_ship, days_air, other_info FROM sites ORDER BY site_code ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        }
        return list;
    }

    public void updateSiteTransport(int id, int daysShip, int daysAir, String otherInfo) throws Exception {
        String sql = "UPDATE sites SET days_ship = ?, days_air = ?, other_info = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, daysShip);
            ps.setInt(2, daysAir);
            ps.setString(3, otherInfo);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }
    
    public int getMinTransportDays() throws Exception {
        int minDays = Integer.MAX_VALUE;
        String sql = "SELECT MIN(days_air) AS m FROM sites WHERE days_air > 0";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                minDays = rs.getInt("m");
            }
        }
        return minDays == 0 ? 1 : minDays; // Mặc định 1 ngày nếu không có
    }
}
