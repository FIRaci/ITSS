package com.system.application.overseas;

import com.system.infrastructure.persistence.SiteRepositoryImpl;
import com.itss.Site;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ManageSiteUseCase {
    private SiteRepositoryImpl siteRepository;

    public ManageSiteUseCase() {
        this.siteRepository = new SiteRepositoryImpl();
    }

    public ObservableList<Site> getAllSites() {
        return FXCollections.observableArrayList(siteRepository.findAllSites());
    }

    public void addSite(String code, String name, int daysShip, int daysAir, String info) throws Exception {
        if (daysShip <= 0 || daysAir <= 0) {
            throw new Exception("Lỗi nghiệp vụ: Thời gian vận chuyển phải lớn hơn 0.");
        }
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new Exception("Lỗi nghiệp vụ: Mã và tên Site không được để trống.");
        }
        boolean success = siteRepository.insertSite(new Site(0, code, name, daysShip, daysAir, info));
        if (!success) {
            throw new Exception("Lỗi hệ thống: Không thể thêm Site (Có thể mã đã tồn tại).");
        }
    }

    public void updateSite(String code, String name, int daysShip, int daysAir, String info) throws Exception {
        if (daysShip <= 0 || daysAir <= 0) {
            throw new Exception("Lỗi nghiệp vụ: Thời gian vận chuyển phải lớn hơn 0.");
        }
        boolean success = siteRepository.updateSite(new Site(0, code, name, daysShip, daysAir, info));
        if (!success) {
            throw new Exception("Lỗi hệ thống: Cập nhật thất bại.");
        }
    }

    public void deleteSite(String code) throws Exception {
        boolean success = false;
        try {
            success = siteRepository.deleteSite(code);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        if (!success) {
            throw new Exception("Lỗi hệ thống: Xóa thất bại (Mã Site không tồn tại).");
        }
    }
}
