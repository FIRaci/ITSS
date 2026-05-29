package com.system.application.warehouse;

import com.system.infrastructure.persistence.WarehouseRepositoryImpl;
import com.itss.InternationalOrder;

public class ReportDiscrepancyUseCase {
    private WarehouseRepositoryImpl repository;

    public ReportDiscrepancyUseCase() {
        this.repository = new WarehouseRepositoryImpl();
    }

    public void reportDiscrepancy(InternationalOrder order, String reason, int qty, String evidencePath, String note, String user) throws Exception {
        if (reason == null || reason.trim().isEmpty()) {
            throw new Exception("Lỗi nghiệp vụ: Phải chọn lý do sai lệch.");
        }
        if (qty <= 0) {
            throw new Exception("Lỗi nghiệp vụ: Số lượng sai lệch phải lớn hơn 0.");
        }
        if (reason.equals("Hàng hỏng/vỡ") && (evidencePath == null || evidencePath.trim().isEmpty())) {
            throw new Exception("Lỗi nghiệp vụ: Hàng hỏng/vỡ bắt buộc phải có ảnh minh chứng.");
        }

        boolean success = repository.reportDiscrepancy(order, reason, qty, evidencePath, note, user);
        if (!success) {
            throw new Exception("Lỗi hệ thống: Có lỗi xảy ra khi lưu biên bản.");
        }
    }
}
