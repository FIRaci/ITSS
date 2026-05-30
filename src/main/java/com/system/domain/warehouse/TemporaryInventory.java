package com.system.domain.warehouse;

import java.util.Date;

public class TemporaryInventory {
    private String batchId;
    private String orderId;
    private String merchandiseId;
    private int surplusQty;
    private Date startDate;
    private Date expiryDate;
    private String holdingStatus;

    public TemporaryInventory() {}

    public TemporaryInventory(String batchId, int surplusQty, int durationDays) {
        this.batchId = batchId;
        this.surplusQty = surplusQty;
        this.startDate = new Date();
        this.expiryDate = calculateExpiryDate(durationDays);
        this.holdingStatus = "PARTIAL_HOLDING";
    }

    public void isolateExcessGoods(int qty) { this.surplusQty = qty; this.holdingStatus = "PARTIAL_HOLDING"; }
    public Date calculateExpiryDate(int days) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(java.util.Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
    public boolean isExpired() { return new Date().after(expiryDate); }
    public void markForDisposal() { this.holdingStatus = "FORCE_CLOSED"; }

    public String getBatchId() { return batchId; }
    public String getOrderId() { return orderId; }
    public String getMerchandiseId() { return merchandiseId; }
    public int getSurplusQty() { return surplusQty; }
    public Date getStartDate() { return startDate; }
    public Date getExpiryDate() { return expiryDate; }
    public String getHoldingStatus() { return holdingStatus; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setMerchandiseId(String id) { this.merchandiseId = id; }
    public void setSurplusQty(int qty) { this.surplusQty = qty; }
    public void setHoldingStatus(String s) { this.holdingStatus = s; }
}
