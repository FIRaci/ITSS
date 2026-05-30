package com.system.application.order;

import com.system.domain.request.ImportRequest;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.system.infrastructure.persistence.OrderRepositoryImpl;

public class AllocationController {
    private OrderAllocationUseCase allocationService;
    private RequestRepositoryImpl requestRepo;
    private OrderRepositoryImpl orderRepo;

    public AllocationController() {
        this.allocationService = new OrderAllocationUseCase();
        this.requestRepo = new RequestRepositoryImpl();
        this.orderRepo = new OrderRepositoryImpl();
    }

    public void runAllocation(String requestId, int logicId, String preferredSite) throws Exception {
        allocationService.saveSelectedLogic(logicId);
        if (preferredSite != null && !preferredSite.isEmpty()) {
            if (!allocationService.validateSite(preferredSite)) {
                throw new Exception("Site không tồn tại: " + preferredSite);
            }
        }
        requestRepo.updateImportRequestStatus(requestId, "Đang xử lý");
        allocationService.executeAllocationSequence(requestId);
        orderRepo.createOrdersForAllocation(requestId);
        requestRepo.updateImportRequestStatus(requestId, "Đã xử lý");
    }

    public void selectLogic(int logicId) {
        allocationService.saveSelectedLogic(logicId);
    }

    public boolean validateSite(String siteName) {
        return allocationService.validateSite(siteName);
    }

    public int applyLogicAndAllocate(int requiredQty, int stockQty) {
        return allocationService.applyLogicAndAllocate(requiredQty, stockQty);
    }
}
