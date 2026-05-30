package com.system.domain.order;

import java.util.List;

public interface IOverseasOrderRepository {
    OverseasOrder findById(String orderId);
    List<OverseasOrder> getLinkedOrders(String requestId);
    List<OverseasOrder> fetchPendingOrders();
    void save(OverseasOrder order);
    void updateOrderStatus(String orderId, String status);
}
