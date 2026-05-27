package com.system.domain.order;

import java.util.List;
import java.util.ArrayList;
public class Site {
    private String siteId;
    private String siteName;
    private int daysShip;
    private int daysAir;
    public Site(String id, String name, int ship, int air) { this.siteId = id; this.siteName = name; this.daysShip = ship; this.daysAir = air; }
    public List<Site> findSupplyingSites() { return new ArrayList<>(); }
    public List<Site> filterSitesByDeliveryDate() { return new ArrayList<>(); }
    public String getSiteName() { return siteName; }
}
