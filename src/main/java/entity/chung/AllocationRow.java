package entity.chung;

public class AllocationRow {
    private String merchandiseCode;
    private String siteCode;
    private int qty;
    private String shippingMethod;

    public AllocationRow(String merchandiseCode, String siteCode, int qty, String shippingMethod) {
        this.merchandiseCode = merchandiseCode;
        this.siteCode = siteCode;
        this.qty = qty;
        this.shippingMethod = shippingMethod;
    }

    public String getMerchandiseCode() { return merchandiseCode; }
    public String getSiteCode() { return siteCode; }
    public int getQty() { return qty; }
    public String getShippingMethod() { return shippingMethod; }
}
