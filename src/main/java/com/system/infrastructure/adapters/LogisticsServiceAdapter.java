package com.system.infrastructure.adapters;

import com.system.domain.order.ILogisticsService;
import com.system.domain.order.OverseasOrder;
import com.system.infrastructure.persistence.OverseasOrderRepositoryImpl;

public class LogisticsServiceAdapter implements ILogisticsService {
    private static final String STATUS_IN_TRANSIT = "Đang giao";

    private OverseasOrderRepositoryImpl orderRepository;

    public LogisticsServiceAdapter() {
        this.orderRepository = new OverseasOrderRepositoryImpl();
    }

    @Override
    public boolean checkOnBoardStatus(String orderId) {
        OverseasOrder order = orderRepository.findById(orderId);
        if (order == null || order.getOrderStatus() == null) {
            return false;
        }
        return STATUS_IN_TRANSIT.equalsIgnoreCase(order.getOrderStatus().trim());
    }
}
