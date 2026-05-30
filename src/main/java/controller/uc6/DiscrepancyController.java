package controller.uc6;

import entity.uc6.DiscrepancyRecord;
import entity.chung.InternationalOrder;
import subsystem.uc6.models.DiscrepancyModel;
import subsystem.uc6.controllers.DiscrepancySubController;

public class DiscrepancyController {
    private DiscrepancySubController subController;
    private DiscrepancyModel model;

    public DiscrepancyController() {
        this.subController = new DiscrepancySubController();
        this.model = new DiscrepancyModel();
    }

    public void report(DiscrepancyRecord record, InternationalOrder order) throws Exception {
        if (record.getReason() == null || record.getReason().trim().isEmpty()) {
            throw new Exception("Phải chọn lý do sai lệch.");
        }
        if (record.getQtyDiscrepancy() <= 0) {
            throw new Exception("Số lượng sai lệch phải lớn hơn 0.");
        }
        if ("Hàng hỏng/vỡ".equals(record.getReason()) && (record.getEvidencePath() == null || record.getEvidencePath().trim().isEmpty())) {
            throw new Exception("Hàng hỏng/vỡ bắt buộc phải có ảnh minh chứng.");
        }
        subController.submit(record, order);
    }

    public DiscrepancyModel getModel() { return model; }
}
