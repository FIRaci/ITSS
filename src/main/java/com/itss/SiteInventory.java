package com.itss;

public class SiteInventory {
    private int id;
    private String siteCode;
    private String merchandiseCode;
    private int stockQty;

    public SiteInventory(int id, String siteCode, String merchandiseCode, int stockQty) {
        this.id = id;
        this.siteCode = siteCode;
        this.merchandiseCode = merchandiseCode;
        this.stockQty = stockQty;
    }

    public int getId() { return id; }
    public String getSiteCode() { return siteCode; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public int getStockQty() { return stockQty; }
}
