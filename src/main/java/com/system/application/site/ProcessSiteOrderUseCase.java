package com.system.application.site;

import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.itss.InternationalOrder;
import javafx.collections.ObservableList;

public class ProcessSiteOrderUseCase {
    private OrderRepositoryImpl orderRepository;

    public ProcessSiteOrderUseCase() {
        this.orderRepository = new OrderRepositoryImpl();
    }

    public ObservableList<InternationalOrder> getOrdersForSite(String siteCode) {
        if (siteCode == null || siteCode.isEmpty()) {
            return javafx.collections.FXCollections.observableArrayList();
        }
        return orderRepository.findOrdersBySite(siteCode);
    }

    public void shipOrder(int orderId) throws Exception {
        boolean success = orderRepository.updateOrderStatus(orderId, "Đã giao hàng");
        if (!success) {
            throw new Exception("Lỗi hệ thống: Cập nhật trạng thái giao hàng thất bại.");
        }
    }
}
