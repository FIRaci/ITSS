package com.itss;

public class InternationalOrder {
    private int id;
    private String ycnhId;
    private String siteCode;
    private String merchandiseCode;
    private int qty;
    private String shippingMethod;
    private String status;

    public InternationalOrder(int id, String ycnhId, String siteCode, String merchandiseCode, int qty, String shippingMethod, String status) {
        this.id = id;
        this.ycnhId = ycnhId;
        this.siteCode = siteCode;
        this.merchandiseCode = merchandiseCode;
        this.qty = qty;
        this.shippingMethod = shippingMethod;
        this.status = status;
    }

    public int getId() { return id; }
    public String getYcnhId() { return ycnhId; }
    public String getSiteCode() { return siteCode; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public int getQty() { return qty; }
    public String getShippingMethod() { return shippingMethod; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
