package com.system.domain.order;

public class OverseasOrder {
    private String orderId;
    private String orderStatus;
    private int totalQuantity;
    private String siteCode;

    public OverseasOrder(String orderId, String orderStatus, int totalQuantity, String siteCode) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalQuantity = totalQuantity;
        this.siteCode = siteCode;
    }

    public boolean isCancelable() {
        if (orderStatus == null) {
            return false;
        }
        String normalized = orderStatus.trim();
        return !normalized.equalsIgnoreCase("Đang giao")
            && !normalized.equalsIgnoreCase("Đã nhập kho");
    }

    public void changeStatus(String newStatus) { this.orderStatus = newStatus; }

    public String getOrderId() { return orderId; }
    public String getOrderStatus() { return orderStatus; }
    public int getTotalQuantity() { return totalQuantity; }
    public String getSiteCode() { return siteCode; }
}
