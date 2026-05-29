package subsystem.uc6.controllers;

import entity.uc6.DiscrepancyRecord;
import entity.chung.InternationalOrder;
import com.system.infrastructure.persistence.WarehouseRepositoryImpl;

public class DiscrepancySubController {
    private WarehouseRepositoryImpl repository;

    public DiscrepancySubController() {
        this.repository = new WarehouseRepositoryImpl();
    }

    public void submit(DiscrepancyRecord record, InternationalOrder order) throws Exception {
        com.itss.InternationalOrder oldOrder = new com.itss.InternationalOrder(
            order.getId(), order.getRequestId(), order.getSiteCode(), order.getMerchandiseCode(),
            order.getQty(), order.getShippingMethod(), order.getStatus(), order.getCreatedAt());
        String reason = record.getReason();
        String evidence = record.getEvidencePath();
        String note = record.getNote();
        String user = record.getReportedBy();
        int qty = record.getQtyDiscrepancy();
        boolean success = repository.reportDiscrepancy(oldOrder, reason, qty, evidence, note, user);
        if (!success) {
            throw new Exception("Có lỗi xảy ra khi lưu biên bản.");
        }
    }
}
