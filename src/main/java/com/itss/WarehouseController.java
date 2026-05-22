package com.itss;

import javafx.collections.ObservableList;

public class WarehouseController {

    private WarehouseRepository repository;

    public WarehouseController() {
        this.repository = new WarehouseRepository();
    }

    public ObservableList<InternationalOrder> getIncomingOrders() {
        return repository.getIncomingOrders();
    }

    public boolean receiveFullOrder(int orderId) {
        return repository.receiveFullOrder(orderId);
    }

    public boolean reportDiscrepancy(InternationalOrder order, String reason, int qty, String evidencePath, String note, String user) {
        return repository.reportDiscrepancy(order, reason, qty, evidencePath, note, user);
    }
}
