package entity.chung;

public class SiteStock {
    public String siteCode;
    public int stockQty;
    public String shippingMethod;
    public int prefRank;
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
