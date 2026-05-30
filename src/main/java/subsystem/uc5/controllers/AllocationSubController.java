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
        // Áp dụng Logic 1 (Tối ưu chi phí - Ưu tiên Tàu) và Logic 3 (Ưu tiên thời gian - Bay) 
        // dựa trên điều kiện của BRD. Ở đây sắp xếp ưu tiên Tàu trước để giảm chi phí.
        plan.sort((a, b) -> {
            if (a.getShippingMethod().equals(b.getShippingMethod())) {
                return Integer.compare(b.getQty(), a.getQty()); // Logic 2/4: Ưu tiên Site có số lượng lớn hơn
            }
            return a.getShippingMethod().compareTo(b.getShippingMethod()); 
        });
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
