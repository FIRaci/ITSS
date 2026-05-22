package com.itss;

import java.util.List;

public class AllocationController {
    
    private IOptimizationEngine engine;
    private OrderRepository orderRepository;

    public AllocationController() {
        this.engine = new GreedyOptimizationEngine();
        this.orderRepository = new OrderRepository();
    }

    public List<AllocationRow> calculatePlan(String requestId) {
        return engine.buildAllocationPlan(requestId);
    }

    public boolean submitOrders(String requestId, List<AllocationRow> plan) {
        if (plan == null || plan.isEmpty()) return false;
        return orderRepository.insertOrders(requestId, plan);
    }
}
