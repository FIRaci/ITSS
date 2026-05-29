package entity.uc5;

import entity.chung.Site;
import java.util.ArrayList;
import java.util.List;

public class SiteAllocation {
    private Site site;
    private List<AllocatedItem> items;

    public SiteAllocation(Site site) {
        this.site = site;
        this.items = new ArrayList<>();
    }

    public Site getSite() { return site; }
    public List<AllocatedItem> getItems() { return items; }
    public void addItem(AllocatedItem item) { items.add(item); }

    public static class AllocatedItem {
        private String merchandiseCode;
        private int qty;
        private String shippingMethod;

        public AllocatedItem(String merchandiseCode, int qty, String shippingMethod) {
            this.merchandiseCode = merchandiseCode;
            this.qty = qty;
            this.shippingMethod = shippingMethod;
        }

        public String getMerchandiseCode() { return merchandiseCode; }
        public int getQty() { return qty; }
        public String getShippingMethod() { return shippingMethod; }
    }
}
