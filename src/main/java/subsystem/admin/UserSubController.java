package subsystem.admin;

import com.system.infrastructure.persistence.Database;
import entity.chung.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserSubController {
    public List<User> getAllUsers() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password, role, site_code, is_active FROM users ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("site_code"),
                    rs.getBoolean("is_active")
                ));
            }
        }
        return list;
    }

    public void addUser(User u) throws Exception {
        String sql = "INSERT INTO users (username, password, role, site_code, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRole());
            ps.setString(4, u.getSiteCode());
            ps.setBoolean(5, u.isActive());
            ps.executeUpdate();
        }
    }

    public void updateUserActive(int id, boolean isActive) throws Exception {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
