package com.itss;

public class InternationalOrder {
    private int id;
    private String requestId;
    private String siteCode;
    private String merchandiseCode;
    private int qty;
    private String shippingMethod;
    private String status;
    private String createdAt;

    public InternationalOrder(int id, String requestId, String siteCode, String merchandiseCode, int qty, String shippingMethod, String status) {
        this.id = id;
        this.requestId = requestId;
        this.siteCode = siteCode;
        this.merchandiseCode = merchandiseCode;
        this.qty = qty;
        this.shippingMethod = shippingMethod;
        this.status = status;
        this.createdAt = "";
    }

    public InternationalOrder(int id, String requestId, String siteCode, String merchandiseCode, int qty, String shippingMethod, String status, String createdAt) {
        this.id = id;
        this.requestId = requestId;
        this.siteCode = siteCode;
        this.merchandiseCode = merchandiseCode;
        this.qty = qty;
        this.shippingMethod = shippingMethod;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getRequestId() { return requestId; }
    public String getYcnhId() { return requestId; }
    public String getSiteCode() { return siteCode; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public int getQty() { return qty; }
    public String getShippingMethod() { return shippingMethod; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }
}
