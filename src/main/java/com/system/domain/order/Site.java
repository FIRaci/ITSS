package com.system.domain.order;

public class Site {
    private String siteId;
    private String siteName;
    public Site(String id, String name) { this.siteId = id; this.siteName = name; }
    public String getSiteId() { return siteId; }
    public String getSiteName() { return siteName; }
}
