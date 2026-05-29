package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.Statement;

public class FixSchema {
    public static void main(String[] args) {
        System.out.println("Đang sửa kích thước cột password...");
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Tăng độ dài cột password lên 255 để chứa được chuỗi SHA-256 (64 ký tự)
            stmt.executeUpdate("ALTER TABLE users ALTER COLUMN password TYPE VARCHAR(255)");
            System.out.println("Sửa cấu trúc bảng users thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
