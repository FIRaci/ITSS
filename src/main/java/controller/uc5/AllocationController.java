package controller.uc5;

import entity.chung.AllocationRow;
import entity.chung.SiteStock;
import subsystem.uc5.models.AllocationModel;
import subsystem.uc5.controllers.AllocationSubController;
import java.util.List;

public class AllocationController {
    private AllocationSubController subController;

    public AllocationController() {
        this.subController = new AllocationSubController();
    }

    public List<SiteStock> getSiteStocks(String merchandiseCode) {
        return subController.getAvailableStock(merchandiseCode);
    }

    public void allocate(List<AllocationRow> plan) {
        subController.optimize(plan);
    }
}
