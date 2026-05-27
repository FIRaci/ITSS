package com.system.application.warehouse;

import com.system.infrastructure.persistence.WarehouseRepositoryImpl;
import com.itss.InternationalOrder;
import javafx.collections.ObservableList;

public class ReceiveOrderUseCase {
    private WarehouseRepositoryImpl repository;

    public ReceiveOrderUseCase() {
        this.repository = new WarehouseRepositoryImpl();
    }

    public ObservableList<InternationalOrder> getIncomingOrders() {
        return repository.getIncomingOrders();
    }

    public void receiveFullOrder(int orderId) throws Exception {
        boolean success = repository.receiveFullOrder(orderId);
        if (!success) {
            throw new Exception("Lỗi hệ thống: Không thể xác nhận nhập kho.");
        }
    }
}
