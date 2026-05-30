package com.system.domain.request;

import java.util.Date;

public class RequestDetail {
    private int detailId;
    private String requestId;
    private String merchandiseId;
    private int quantity;
    private String unit;
    private Date desiredDeliveryDate;

    public RequestDetail() {}

    public RequestDetail(int detailId, String requestId, String merchandiseId, int quantity, String unit, Date desiredDeliveryDate) {
        this.detailId = detailId;
        this.requestId = requestId;
        this.merchandiseId = merchandiseId;
        this.quantity = quantity;
        this.unit = unit;
        this.desiredDeliveryDate = desiredDeliveryDate;
    }

    public void createDetail(String merchandiseId, int quantity, String unit, Date deliveryDate) {
        this.merchandiseId = merchandiseId;
        this.quantity = quantity;
        this.unit = unit;
        this.desiredDeliveryDate = deliveryDate;
    }

    public boolean validate() {
        return quantity > 0 && unit != null && !unit.isEmpty() && desiredDeliveryDate != null;
    }

    public void updateDetail(int quantity, Date date) {
        this.quantity = quantity;
        this.desiredDeliveryDate = date;
    }

    public void deleteDetail() { this.quantity = 0; }

    public int getDetailId() { return detailId; }
    public String getRequestId() { return requestId; }
    public String getMerchandiseId() { return merchandiseId; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public Date getDesiredDeliveryDate() { return desiredDeliveryDate; }
    public void setDetailId(int detailId) { this.detailId = detailId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setMerchandiseId(String merchandiseId) { this.merchandiseId = merchandiseId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setDesiredDeliveryDate(Date d) { this.desiredDeliveryDate = d; }
}
