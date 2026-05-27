package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class SetupData {
    public static void main(String[] args) {
        System.out.println("Đang cài đặt dữ liệu mẫu cho Users...");
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Xóa hết user cũ (chú ý: nếu có khóa ngoại liên kết từ bảng khác, có thể lỗi. 
            // Nhưng trong thiết kế này bảng user thường độc lập)
            stmt.executeUpdate("DELETE FROM users");
            
            String sql = "INSERT INTO users (username, password, role, site_code) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                
                // 1. banhang
                ps.setString(1, "banhang");
                ps.setString(2, "banhang123");
                ps.setString(3, "sales");
                ps.setString(4, null);
                ps.addBatch();

                // 2. dathangquocte
                ps.setString(1, "dathangquocte");
                ps.setString(2, "dathangquocte123");
                ps.setString(3, "overseas");
                ps.setString(4, null);
                ps.addBatch();

                // 3. quanlykho
                ps.setString(1, "quanlykho");
                ps.setString(2, "quanlykho123");
                ps.setString(3, "warehouse");
                ps.setString(4, null);
                ps.addBatch();

                // 4. site
                ps.setString(1, "site");
                ps.setString(2, "site123");
                ps.setString(3, "site");
                ps.setString(4, "SITE1"); // Cần có site_code để test
                ps.addBatch();

                // 5. admin
                ps.setString(1, "admin");
                ps.setString(2, "admin123");
                ps.setString(3, "admin");
                ps.setString(4, null);
                ps.addBatch();

                int[] results = ps.executeBatch();
                System.out.println("Đã chèn thành công " + results.length + " users!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
