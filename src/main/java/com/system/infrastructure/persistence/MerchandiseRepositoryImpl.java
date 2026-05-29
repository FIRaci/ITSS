package com.system.infrastructure.persistence;

import com.itss.Merchandise;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repository truy cập bảng merchandise_catalog trong CSDL.
 * Cung cấp: CRUD + tìm kiếm gợi ý (autocomplete) theo mã/tên hàng.
 */
public class MerchandiseRepositoryImpl {

    /**
     * Tìm kiếm mặt hàng theo từ khóa (mã hoặc tên).
     * Dùng cho autocomplete khi nhập mã hàng trong form tạo yêu cầu.
     * Trả về tối đa 10 kết quả khớp nhất.
     */
    public List<Merchandise> searchByKeyword(String keyword) {
        List<Merchandise> list = new ArrayList<>();
        String sql = "SELECT code, name, unit, description FROM merchandise_catalog " +
                     "WHERE LOWER(code) LIKE ? OR LOWER(name) LIKE ? " +
                     "ORDER BY code ASC LIMIT 10";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Merchandise(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tìm một mặt hàng theo mã chính xác. Trả về null nếu không tồn tại. */
    public Merchandise findByCode(String code) {
        String sql = "SELECT code, name, unit, description FROM merchandise_catalog WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Merchandise(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getString("description")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lấy toàn bộ danh mục hàng hóa. */
    public ObservableList<Merchandise> findAll() {
        ObservableList<Merchandise> list = FXCollections.observableArrayList();
        String sql = "SELECT code, name, unit, description FROM merchandise_catalog ORDER BY code ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Merchandise(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Thêm mặt hàng mới vào danh mục. */
    public boolean insert(Merchandise m) throws Exception {
        String sql = "INSERT INTO merchandise_catalog (code, name, unit, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getCode());
            ps.setString(2, m.getName());
            ps.setString(3, m.getUnit());
            ps.setString(4, m.getDescription());
            return ps.executeUpdate() > 0;
        }
    }

    /** Cập nhật thông tin mặt hàng (không đổi mã). */
    public boolean update(Merchandise m) throws Exception {
        String sql = "UPDATE merchandise_catalog SET name = ?, unit = ?, description = ? WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getUnit());
            ps.setString(3, m.getDescription());
            ps.setString(4, m.getCode());
            return ps.executeUpdate() > 0;
        }
    }

    /** Xóa mặt hàng khỏi danh mục. Cảnh báo: không kiểm tra ràng buộc khóa ngoại. */
    public boolean delete(String code) throws Exception {
        String sql = "DELETE FROM merchandise_catalog WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Sinh mã yêu cầu nhập hàng mới dạng YCNH-YYYYMMDD-XXX (số thứ tự trong ngày).
     * Ví dụ: YCNH-20260527-001
     */
    public String generateNextRequestId() {
        String today = java.time.LocalDate.now().toString().replace("-", "");
        String prefix = "YCNH-" + today + "-";
        String sql = "SELECT id FROM ycnh WHERE id LIKE ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("id");
                // Lấy số thứ tự cuối cùng và tăng lên 1
                int seq = Integer.parseInt(lastId.substring(lastId.lastIndexOf("-") + 1)) + 1;
                return prefix + String.format("%03d", seq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prefix + "001";
    }
}
