package com.system.domain.order;

public class OverseasOrder {
    private String orderId;
    private String orderStatus;
    public void createOrUpdateOrder() { }
    public void updateOrderStatus(String status) { this.orderStatus = status; }
    public String getOrderId() { return orderId; }
    public String getOrderStatus() { return orderStatus; }
}
