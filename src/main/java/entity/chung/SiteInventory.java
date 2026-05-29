package entity.chung;

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

    public SiteInventory(String siteCode, String merchandiseCode, int stockQty) {
        this(0, siteCode, merchandiseCode, stockQty);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public void setMerchandiseCode(String merchandiseCode) { this.merchandiseCode = merchandiseCode; }
    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }
}
