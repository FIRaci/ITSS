package com.system.application.site;

import com.system.infrastructure.persistence.SiteInventoryRepositoryImpl;
import com.itss.SiteInventoryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ManageSiteInventoryUseCase {
    private SiteInventoryRepositoryImpl repository;

    public ManageSiteInventoryUseCase() {
        this.repository = new SiteInventoryRepositoryImpl();
    }

    public ObservableList<SiteInventoryItem> getInventory(String siteCode) {
        if (siteCode == null || siteCode.isEmpty()) {
            return FXCollections.observableArrayList();
        }
        return FXCollections.observableArrayList(repository.getInventoryBySite(siteCode));
    }

    public void addInventory(String siteCode, String merchandiseCode, int qty) throws Exception {
        if (siteCode == null || siteCode.isEmpty()) throw new Exception("Lỗi: Site code không hợp lệ.");
        if (merchandiseCode == null || merchandiseCode.trim().isEmpty()) throw new Exception("Lỗi: Mã hàng không được để trống.");
        if (qty < 0) throw new Exception("Lỗi: Số lượng không được âm.");
        
        boolean success = repository.insertInventory(new SiteInventoryItem(0, siteCode, merchandiseCode, qty));
        if (!success) {
            throw new Exception("Lỗi hệ thống: Thêm hàng thất bại.");
        }
    }

    public void updateInventory(int id, int qty, int oldQty) throws Exception {
        if (qty < 0) throw new Exception("Lỗi: Số lượng không được âm.");
        String siteCode = com.system.application.auth.SessionManager.getCurrentUser() != null ? com.system.application.auth.SessionManager.getCurrentUser().getSiteCode() : null;
        boolean success = repository.updateInventoryQty(id, qty, oldQty, siteCode);
        if (!success) {
            throw new Exception("Lỗi hệ thống: Cập nhật thất bại hoặc dữ liệu đã bị thay đổi bởi người khác (Race Condition). Vui lòng tải lại trang và thử lại!");
        }
    }

    public void deleteInventory(int id) throws Exception {
        String siteCode = com.system.application.auth.SessionManager.getCurrentUser() != null ? com.system.application.auth.SessionManager.getCurrentUser().getSiteCode() : null;
        boolean success = repository.deleteInventory(id, siteCode);
        if (!success) {
            throw new Exception("Lỗi hệ thống: Xóa thất bại.");
        }
    }
}
