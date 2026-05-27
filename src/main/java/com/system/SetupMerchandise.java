package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Script tạo bảng merchandise_catalog nếu chưa tồn tại + chèn dữ liệu mẫu.
 * Chạy: mvn exec:java -Dexec.mainClass="com.system.SetupMerchandise"
 */
public class SetupMerchandise {
    public static void main(String[] args) throws Exception {
        try (Connection conn = Database.getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Tạo bảng
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS merchandise_catalog (" +
                "    code        VARCHAR(50)  PRIMARY KEY," +
                "    name        VARCHAR(200) NOT NULL," +
                "    unit        VARCHAR(50)  NOT NULL," +
                "    description TEXT         DEFAULT ''" +
                ")"
            );
            System.out.println("[OK] Table merchandise_catalog created (or already exists).");

            // 2. Chèn dữ liệu mẫu
            String[][] data = {
                {"MH001", "Áo thun nam cao cấp",      "cái",  "Áo thun nam cotton 100%, nhiều màu sắc"},
                {"MH002", "Quần jean nữ thời trang",   "cái",  "Quần jean nữ skinny, vải denim cao cấp"},
                {"MH003", "Giày thể thao unisex",      "đôi",  "Giày thể thao đa năng, đế cao su chống trượt"},
                {"MH004", "Túi xách tay da bò",        "cái",  "Túi xách da bò thật, nhiều ngăn tiện dụng"},
                {"MH005", "Áo khoác nỉ nữ",            "cái",  "Áo khoác nỉ lông cừu, giữ ấm tốt"},
                {"MH006", "Kính mát chống UV",         "cái",  "Kính mát phân cực, chống tia UV 400"},
                {"MH007", "Đồng hồ thời trang nam",    "cái",  "Đồng hồ cơ automatic, dây da bò"},
                {"MH008", "Balo học sinh đa năng",     "cái",  "Balo chống nước, nhiều ngăn, chống sốc laptop"},
                {"MH009", "Vớ cotton thể thao",        "đôi",  "Vớ cotton cao cổ, thấm hút tốt"},
                {"MH010", "Thắt lưng da nam",          "cái",  "Thắt lưng da bò thật, khóa inox"},
                {"MH011", "Áo sơ mi nam công sở",      "cái",  "Áo sơ mi vải lụa tơ tằm, không nhăn"},
                {"MH012", "Váy đầm dự tiệc",           "cái",  "Đầm voan phồng, thiết kế sang trọng"},
            };

            int inserted = 0;
            for (String[] row : data) {
                try (var ps = conn.prepareStatement(
                        "INSERT INTO merchandise_catalog (code, name, unit, description) " +
                        "VALUES (?, ?, ?, ?) ON CONFLICT (code) DO NOTHING")) {
                    ps.setString(1, row[0]);
                    ps.setString(2, row[1]);
                    ps.setString(3, row[2]);
                    ps.setString(4, row[3]);
                    inserted += ps.executeUpdate();
                }
            }
            System.out.println("[OK] Inserted " + inserted + " sample merchandise rows.");
            System.out.println("[DONE] Setup merchandise_catalog complete!");
        }
    }
}
