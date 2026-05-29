package com.system.application.overseas;

import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.itss.CancellationRequest;
import javafx.collections.ObservableList;

public class ManageCancellationUseCase {
    private OrderRepositoryImpl orderRepository;

    public ManageCancellationUseCase() {
        this.orderRepository = new OrderRepositoryImpl();
    }

    public ObservableList<CancellationRequest> getPendingCancellations() {
        return orderRepository.getPendingCancellations();
    }

    public void approveCancellation(int cancelId, int orderId) throws Exception {
        boolean success = orderRepository.approveCancellation(cancelId, orderId);
        if (!success) {
            throw new Exception("Lỗi khi duyệt hủy đơn hàng!");
        }
    }

    public void rejectCancellation(int cancelId, int orderId) throws Exception {
        boolean success = orderRepository.rejectCancellation(cancelId, orderId);
        if (!success) {
            throw new Exception("Lỗi khi từ chối yêu cầu hủy!");
        }
    }
}
