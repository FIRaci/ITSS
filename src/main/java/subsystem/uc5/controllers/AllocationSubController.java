package subsystem.uc5.controllers;

import entity.chung.AllocationRow;
import entity.chung.SiteStock;
import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.system.application.overseas.GreedyOptimizationEngine;
import java.util.List;
import java.util.stream.Collectors;

public class AllocationSubController {
    private GreedyOptimizationEngine engine;
    private OrderRepositoryImpl orderRepository;

    public AllocationSubController() {
        this.engine = new GreedyOptimizationEngine();
        this.orderRepository = new OrderRepositoryImpl();
    }

    public List<AllocationRow> buildPlan(String requestId) {
        List<com.itss.AllocationRow> oldPlan = engine.buildAllocationPlan(requestId);
        return oldPlan.stream()
            .map(r -> new AllocationRow(r.getMerchandiseCode(), r.getSiteCode(), r.getQty(), r.getShippingMethod()))
            .collect(Collectors.toList());
    }

    public List<SiteStock> getAvailableStock(String merchandiseCode) {
        return List.of();
    }

    public void optimize(List<AllocationRow> plan) {
    }

    public void submit(String requestId, List<AllocationRow> plan) throws Exception {
        List<com.itss.AllocationRow> oldPlan = plan.stream()
            .map(r -> new com.itss.AllocationRow(r.getMerchandiseCode(), r.getSiteCode(), r.getQty(), r.getShippingMethod()))
            .collect(Collectors.toList());
        boolean success = orderRepository.insertOrders(requestId, oldPlan);
        if (!success) {
            throw new Exception("Lỗi khi lưu đơn hàng vào hệ thống!");
        }
    }
}
