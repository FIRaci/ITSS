package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class GenerateData {
    public static void main(String[] args) {
        System.out.println("Bắt đầu khởi tạo dữ liệu mẫu số lượng lớn...");
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Xóa dữ liệu cũ theo thứ tự để tránh lỗi khóa ngoại (nếu có)
            String[] tablesToClear = {
                "discrepancy_items", "discrepancy_reports", "cancellation_requests", 
                "order_status_history", "international_orders", "ycnh_chitiet", 
                "ycnh_history", "ycnh", "site_inventory", "site_transport_log", 
                "sites", "users"
            };
            for (String tbl : tablesToClear) {
                stmt.executeUpdate("DELETE FROM " + tbl);
            }
            
            Random rand = new Random();

            // 1. Khởi tạo 10 Sites
            System.out.println("Tạo 10 Sites...");
            String sqlSite = "INSERT INTO sites (site_code, name, days_ship, days_air, other_info) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSite)) {
                for (int i = 1; i <= 10; i++) {
                    ps.setString(1, "SITE" + i);
                    ps.setString(2, "Trụ sở Site " + i);
                    ps.setInt(3, 15 + rand.nextInt(10)); // 15-24 ngày
                    ps.setInt(4, 3 + rand.nextInt(5));   // 3-7 ngày
                    ps.setString(5, "Khu vực " + (char)('A' + rand.nextInt(5)));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 2. Khởi tạo Users
            System.out.println("Tạo Users cơ bản và mở rộng...");
            String sqlUser = "INSERT INTO users (username, password, role, site_code) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                // Các user gốc để test
                String[][] baseUsers = {
                    {"banhang", "banhang123", "sales", null},
                    {"dathangquocte", "dathangquocte123", "overseas", null},
                    {"quanlykho", "quanlykho123", "warehouse", null},
                    {"site", "site123", "site", "SITE1"},
                    {"admin", "admin123", "admin", null}
                };
                for (String[] u : baseUsers) {
                    ps.setString(1, u[0]);
                    ps.setString(2, u[1]);
                    ps.setString(3, u[2]);
                    ps.setString(4, u[3]);
                    ps.addBatch();
                }
                // Thêm ~10 user cho mỗi role
                String[] roles = {"sales", "overseas", "warehouse", "admin"};
                for (String role : roles) {
                    for (int i = 1; i <= 10; i++) {
                        ps.setString(1, role + "_user" + i);
                        ps.setString(2, "pass123");
                        ps.setString(3, role);
                        ps.setString(4, null);
                        ps.addBatch();
                    }
                }
                // Thêm 10 user cho 10 sites
                for (int i = 1; i <= 10; i++) {
                    ps.setString(1, "site_user" + i);
                    ps.setString(2, "pass123");
                    ps.setString(3, "site");
                    ps.setString(4, "SITE" + i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Khởi tạo Site Inventory (Mỗi site có 3-5 mặt hàng)
            System.out.println("Tạo Site Inventory...");
            String[] merchandiseList = {"LAPTOP_DELL", "IPHONE_14", "MACBOOK_PRO", "SAMSUNG_S23", "IPAD_AIR", "MOUSE_LOGITECH", "KEYBOARD_AKKO", "MONITOR_LG", "HDD_1TB", "RAM_16GB"};
            String sqlInv = "INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                for (int i = 1; i <= 10; i++) {
                    int numItems = 3 + rand.nextInt(3);
                    for (int j = 0; j < numItems; j++) {
                        ps.setString(1, "SITE" + i);
                        ps.setString(2, merchandiseList[rand.nextInt(merchandiseList.length)]);
                        ps.setInt(3, rand.nextInt(100));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            // 4. Khởi tạo YCNH (Import Requests) và Chi tiết (YCNH_CHITIET)
            System.out.println("Tạo Yêu Cầu Nhập Hàng (YCNH)...");
            String sqlYcnh = "INSERT INTO ycnh (id, status, is_accepted, created_by, created_at) VALUES (?, ?, ?, ?, ?)";
            String sqlYcnhDetail = "INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)";
            String[] reqStatuses = {"PENDING", "APPROVED", "REJECTED", "PROCESSING"};
            
            try (PreparedStatement psReq = conn.prepareStatement(sqlYcnh);
                 PreparedStatement psDetail = conn.prepareStatement(sqlYcnhDetail)) {
                
                for (int i = 1; i <= 20; i++) {
                    String reqId = "YCNH_2026_" + String.format("%03d", i);
                    String status = reqStatuses[rand.nextInt(reqStatuses.length)];
                    boolean isAcc = status.equals("APPROVED") || status.equals("PROCESSING");
                    
                    psReq.setString(1, reqId);
                    psReq.setString(2, status);
                    psReq.setBoolean(3, isAcc);
                    psReq.setString(4, "banhang");
                    psReq.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().minusDays(rand.nextInt(30))));
                    psReq.addBatch();

                    // Thêm 1-4 chi tiết cho YCNH
                    int numDetails = 1 + rand.nextInt(4);
                    for (int j = 0; j < numDetails; j++) {
                        psDetail.setString(1, reqId);
                        psDetail.setString(2, merchandiseList[rand.nextInt(merchandiseList.length)]);
                        psDetail.setInt(3, 10 + rand.nextInt(90));
                        psDetail.setString(4, "Cái");
                        psDetail.setDate(5, Date.valueOf(LocalDate.now().plusDays(5 + rand.nextInt(20))));
                        psDetail.addBatch();
                    }
                }
                psReq.executeBatch();
                psDetail.executeBatch();
            }

            // 5. Khởi tạo International Orders
            System.out.println("Tạo Đơn Hàng Quốc Tế...");
            String sqlOrder = "INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES (?, ?, ?, ?, ?, ?)";
            String[] orderStatuses = {"ORDERED", "SHIPPED", "DELIVERED", "CANCELLED", "DISCREPANCY"};
            String[] shipMethods = {"SHIP", "AIR"};
            
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                for (int i = 1; i <= 25; i++) {
                    // Liên kết ngẫu nhiên tới YCNH đã tạo
                    String reqId = "YCNH_2026_" + String.format("%03d", 1 + rand.nextInt(20));
                    String site = "SITE" + (1 + rand.nextInt(10));
                    String merch = merchandiseList[rand.nextInt(merchandiseList.length)];
                    
                    ps.setString(1, reqId);
                    ps.setString(2, site);
                    ps.setString(3, merch);
                    ps.setInt(4, 20 + rand.nextInt(100));
                    ps.setString(5, shipMethods[rand.nextInt(2)]);
                    ps.setString(6, orderStatuses[rand.nextInt(orderStatuses.length)]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            System.out.println("Hoàn tất chèn dữ liệu mẫu quy mô lớn!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
