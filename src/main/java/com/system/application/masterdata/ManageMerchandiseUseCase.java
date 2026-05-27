package com.system.application.masterdata;

import com.itss.Merchandise;
import com.system.infrastructure.persistence.MerchandiseRepositoryImpl;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * UseCase quản lý Danh mục Mặt hàng (CRUD + search).
 * Cung cấp validation nghiệp vụ trước khi lưu vào DB.
 */
public class ManageMerchandiseUseCase {
    private final MerchandiseRepositoryImpl repository;

    public ManageMerchandiseUseCase() {
        this.repository = new MerchandiseRepositoryImpl();
    }

    /** Lấy toàn bộ danh mục. */
    public ObservableList<Merchandise> getAll() {
        return repository.findAll();
    }

    /**
     * Tìm kiếm mặt hàng theo từ khóa (mã hoặc tên) — dùng cho autocomplete.
     * Trả về tối đa 10 kết quả.
     */
    public List<Merchandise> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return repository.searchByKeyword(keyword.trim());
    }

    /**
     * Validate mã hàng: kiểm tra tồn tại trong danh mục.
     * Ném Exception nếu mã hàng không tồn tại.
     */
    public Merchandise validateAndGet(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new Exception("Mã hàng không được để trống!");
        }
        Merchandise m = repository.findByCode(code.trim().toUpperCase());
        if (m == null) {
            throw new Exception("Mã hàng '" + code + "' không tồn tại trong danh mục!");
        }
        return m;
    }

    /** Thêm mặt hàng mới vào danh mục với đầy đủ validation. */
    public void add(String code, String name, String unit, String description) throws Exception {
        if (code == null || code.trim().isEmpty()) throw new Exception("Mã hàng không được để trống!");
        if (name == null || name.trim().isEmpty()) throw new Exception("Tên hàng không được để trống!");
        if (unit == null || unit.trim().isEmpty()) throw new Exception("Đơn vị không được để trống!");
        // Kiểm tra mã chưa tồn tại
        if (repository.findByCode(code.trim().toUpperCase()) != null) {
            throw new Exception("Mã hàng '" + code + "' đã tồn tại trong danh mục!");
        }
        Merchandise m = new Merchandise(code.trim().toUpperCase(), name.trim(), unit.trim(),
                description != null ? description.trim() : "");
        repository.insert(m);
    }

    /** Cập nhật thông tin mặt hàng (không đổi mã). */
    public void update(String code, String name, String unit, String description) throws Exception {
        if (name == null || name.trim().isEmpty()) throw new Exception("Tên hàng không được để trống!");
        if (unit == null || unit.trim().isEmpty()) throw new Exception("Đơn vị không được để trống!");
        Merchandise m = new Merchandise(code, name.trim(), unit.trim(),
                description != null ? description.trim() : "");
        if (!repository.update(m)) {
            throw new Exception("Không tìm thấy mặt hàng để cập nhật!");
        }
    }

    /** Xóa mặt hàng khỏi danh mục. */
    public void delete(String code) throws Exception {
        if (!repository.delete(code)) {
            throw new Exception("Không tìm thấy mặt hàng để xóa!");
        }
    }

    /** Sinh mã yêu cầu nhập hàng mới theo định dạng serial tự tăng. */
    public String generateNextRequestId() {
        return repository.generateNextRequestId();
    }
}
