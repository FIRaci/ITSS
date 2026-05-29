package com.system.application.order;

public class OrderAllocationUseCase {
    public void saveSelectedLogic(int logicId) { }
    public boolean validateSite(String siteName) { return true; }
    public void executeAllocationSequence(String requestId) { }
    public int applyLogicAndAllocate(int requiredQty, int stockQty) { return requiredQty - stockQty; }
}
