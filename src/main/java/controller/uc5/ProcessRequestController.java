package controller.uc5;

import entity.uc5.AllocationPlan;
import entity.chung.AllocationRow;
import subsystem.uc5.models.AllocationModel;
import subsystem.uc5.controllers.AllocationSubController;
import java.util.List;

public class ProcessRequestController {
    private AllocationSubController subController;
    private AllocationModel model;

    public ProcessRequestController() {
        this.subController = new AllocationSubController();
        this.model = new AllocationModel();
    }

    public List<AllocationRow> calculatePlan(String requestId) {
        return subController.buildPlan(requestId);
    }

    public void submitOrders(String requestId, List<AllocationRow> plan) throws Exception {
        if (plan == null || plan.isEmpty()) {
            throw new Exception("Bản kế hoạch trống, không thể gửi đơn hàng!");
        }
        subController.submit(requestId, plan);
    }

    public AllocationModel getModel() { return model; }
}
