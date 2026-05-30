package com.system.domain.order;

public class SiteInventory {
    private String siteCode;
    private String merchandiseId;
    private int inStockQuantity;

    public SiteInventory() {}

    public SiteInventory(String siteCode, String merchandiseId, int inStockQuantity) {
        this.siteCode = siteCode;
        this.merchandiseId = merchandiseId;
        this.inStockQuantity = inStockQuantity;
    }

    public int checkInventory(String merchandiseId) { return this.merchandiseId.equals(merchandiseId) ? inStockQuantity : 0; }

    public String getSiteCode() { return siteCode; }
    public String getMerchandiseId() { return merchandiseId; }
    public int getInStockQuantity() { return inStockQuantity; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public void setMerchandiseId(String merchandiseId) { this.merchandiseId = merchandiseId; }
    public void setInStockQuantity(int qty) { this.inStockQuantity = qty; }
}
