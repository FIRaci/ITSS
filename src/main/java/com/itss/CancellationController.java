package com.itss;

import javafx.collections.ObservableList;

public class CancellationController {

    private OrderRepository repository;

    public CancellationController() {
        this.repository = new OrderRepository();
    }

    public ObservableList<CancellationRequest> getPendingCancellations() {
        return repository.getPendingCancellations();
    }

    public boolean approveCancellation(int cancelId, int orderId) {
        return repository.approveCancellation(cancelId, orderId);
    }

    public boolean rejectCancellation(int cancelId, int orderId) {
        return repository.rejectCancellation(cancelId, orderId);
    }
}
