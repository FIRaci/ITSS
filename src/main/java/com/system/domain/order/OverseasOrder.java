package com.system.domain.order;

import java.util.Date;

public class OverseasOrder {
    private String orderId;
    private String siteName;
    private String deliveryMethod;
    private String status;
    private int quantityOrdered;
    private Date receivedDate;
    private String merchandiseId;

    public OverseasOrder() {}

    public OverseasOrder(String orderId, String siteName, String deliveryMethod, String status, int quantityOrdered) {
        this.orderId = orderId;
        this.siteName = siteName;
        this.deliveryMethod = deliveryMethod;
        this.status = status;
        this.quantityOrdered = quantityOrdered;
    }

    public void createOrUpdateOrder() { this.status = "Đang xử lý"; }
    public void updateOrderStatus(String status) { this.status = status; }

    public String getOrderId() { return orderId; }
    public String getSiteName() { return siteName; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public String getStatus() { return status; }
    public int getQuantityOrdered() { return quantityOrdered; }
    public Date getReceivedDate() { return receivedDate; }
    public String getMerchandiseId() { return merchandiseId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public void setQuantityOrdered(int qty) { this.quantityOrdered = qty; }
    public void setReceivedDate(Date d) { this.receivedDate = d; }
    public void setMerchandiseId(String id) { this.merchandiseId = id; }
}
