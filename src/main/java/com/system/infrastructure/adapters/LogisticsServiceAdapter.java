package com.system.infrastructure.adapters;

import com.system.domain.order.ILogisticsService;
import com.system.domain.order.OverseasOrder;
import com.system.infrastructure.persistence.OverseasOrderRepositoryImpl;
import java.util.Date;

public class LogisticsServiceAdapter implements ILogisticsService {
    private OverseasOrderRepositoryImpl orderRepository;

    public LogisticsServiceAdapter() {
        this.orderRepository = new OverseasOrderRepositoryImpl();
    }

    @Override
    public boolean checkOnBoardStatus(String orderId) {
        OverseasOrder order = orderRepository.findById(orderId);
        if (order == null || order.getStatus() == null) {
            return false;
        }
        return "Đang giao".equalsIgnoreCase(order.getStatus().trim());
    }

    @Override
    public String getLiveStatus(String orderId) {
        OverseasOrder order = orderRepository.findById(orderId);
        if (order == null) {
            return "Unknown";
        }
        return order.getStatus();
    }

    @Override
    public Date calculateETA(String status) {
        return new Date();
    }

    @Override
    public Object getLiveTracking(String orderId) {
        return getLiveStatus(orderId);
    }
}
