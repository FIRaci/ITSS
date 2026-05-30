package com.system.domain.order;

import java.util.List;
import java.util.ArrayList;

public class Site {
    private String siteId;
    private String siteCode;
    private String siteName;
    private int seaDeliveryDays;
    private int airDeliveryDays;

    public Site() {}

    public Site(String siteCode, String siteName, int seaDeliveryDays, int airDeliveryDays) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.seaDeliveryDays = seaDeliveryDays;
        this.airDeliveryDays = airDeliveryDays;
        this.siteId = siteCode;
    }

    public void sendCancelCommand() {}
    public boolean validateSite(String siteName) { return this.siteName != null && this.siteName.equalsIgnoreCase(siteName); }
    public List<Site> findSupplyingSites(String merchandiseId) { return new ArrayList<>(); }
    public List<Site> filterSitesByDeliveryDate(String merchandiseId, java.util.Date date) { return new ArrayList<>(); }

    public String getSiteId() { return siteId; }
    public String getSiteCode() { return siteCode; }
    public String getSiteName() { return siteName; }
    public int getSeaDeliveryDays() { return seaDeliveryDays; }
    public int getAirDeliveryDays() { return airDeliveryDays; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public void setSeaDeliveryDays(int d) { this.seaDeliveryDays = d; }
    public void setAirDeliveryDays(int d) { this.airDeliveryDays = d; }
}
