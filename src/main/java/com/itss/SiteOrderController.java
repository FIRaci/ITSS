package com.itss;

import javafx.collections.ObservableList;

public class SiteOrderController {
    private OrderRepository orderRepository;

    public SiteOrderController() {
        this.orderRepository = new OrderRepository();
    }

    public ObservableList<InternationalOrder> getOrdersForSite(String siteCode) {
        if (siteCode == null || siteCode.isEmpty()) {
            return javafx.collections.FXCollections.observableArrayList();
        }
        return orderRepository.findOrdersBySite(siteCode);
    }

    public boolean shipOrder(int orderId) {
        // Change status from "Đã đặt hàng" to "Đã giao hàng"
        return orderRepository.updateOrderStatus(orderId, "Đã giao hàng");
    }
}
