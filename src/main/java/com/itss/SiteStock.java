package com.itss;

public class SiteStock {
    public String siteCode;
    public int stockQty;
    public String shippingMethod;
    public int prefRank; // 0 = Ship, 1 = Air, 2 = Not possible
    public int daysShip;
    public int daysAir;

    public SiteStock(String siteCode, int stockQty, String shippingMethod, int prefRank, int daysShip, int daysAir) {
        this.siteCode = siteCode;
        this.stockQty = stockQty;
        this.shippingMethod = shippingMethod;
        this.prefRank = prefRank;
        this.daysShip = daysShip;
        this.daysAir = daysAir;
    }
}
