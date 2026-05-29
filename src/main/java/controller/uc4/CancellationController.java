package controller.uc4;

import entity.uc4.CancellationProposal;
import subsystem.uc4.models.CancellationModel;
import subsystem.uc4.controllers.CancellationSubController;
import javafx.collections.ObservableList;

public class CancellationController {
    private CancellationSubController subController;
    private CancellationModel model;

    public CancellationController() {
        this.subController = new CancellationSubController();
        this.model = new CancellationModel();
    }

    public ObservableList<CancellationProposal> getPendingCancellations() {
        return subController.getPendingProposals();
    }

    public void approveCancellation(int cancelId, int orderId) throws Exception {
        boolean success = subController.approve(cancelId, orderId);
        if (!success) {
            throw new Exception("Lỗi khi duyệt hủy đơn hàng!");
        }
    }

    public void rejectCancellation(int cancelId, int orderId) throws Exception {
        boolean success = subController.reject(cancelId, orderId);
        if (!success) {
            throw new Exception("Lỗi khi từ chối yêu cầu hủy!");
        }
    }

    public CancellationModel getModel() { return model; }
}
