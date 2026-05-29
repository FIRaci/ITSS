package com.system.application.overseas;

import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.itss.AllocationRow;
import java.util.List;

public class ProcessImportRequestUseCase {
    private IOptimizationEngine engine;
    private OrderRepositoryImpl orderRepository;

    public ProcessImportRequestUseCase() {
        this.engine = new GreedyOptimizationEngine();
        this.orderRepository = new OrderRepositoryImpl();
    }

    public List<AllocationRow> calculatePlan(String requestId) {
        return engine.buildAllocationPlan(requestId);
    }

    public void submitOrders(String requestId, List<AllocationRow> plan) throws Exception {
        if (plan == null || plan.isEmpty()) {
            throw new Exception("Bản kế hoạch trống, không thể gửi đơn hàng!");
        }
        boolean success = orderRepository.insertOrders(requestId, plan);
        if (!success) {
            throw new Exception("Lỗi khi lưu đơn hàng vào hệ thống!");
        }
    }
}
