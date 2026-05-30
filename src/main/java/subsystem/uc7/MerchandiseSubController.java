package subsystem.uc7;

import com.system.infrastructure.persistence.Database;
import entity.chung.Merchandise;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MerchandiseSubController {
    public List<Merchandise> getAllMerchandises() throws Exception {
        List<Merchandise> list = new ArrayList<>();
        String sql = "SELECT code, name, unit, description, status FROM merchandise_catalog ORDER BY code ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Merchandise(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getString("description"),
                    rs.getString("status") != null ? rs.getString("status") : "Đang kinh doanh"
                ));
            }
        }
        return list;
    }

    public void addMerchandise(Merchandise m) throws Exception {
        String sql = "INSERT INTO merchandise_catalog (code, name, unit, description, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getCode());
            ps.setString(2, m.getName());
            ps.setString(3, m.getUnit());
            ps.setString(4, m.getDescription());
            ps.setString(5, m.getStatus() != null ? m.getStatus() : "Đang kinh doanh");
            ps.executeUpdate();
        }
    }

    public void updateStatus(String code, String status) throws Exception {
        String sql = "UPDATE merchandise_catalog SET status = ? WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, code);
            ps.executeUpdate();
        }
    }
}
