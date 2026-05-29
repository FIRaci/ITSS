package com.itss;

public class Site {
    private int id;
    private String siteCode;
    private String name;
    private int daysShip;
    private int daysAir;
    private String otherInfo;

    public Site(int id, String siteCode, String name, int daysShip, int daysAir, String otherInfo) {
        this.id = id;
        this.siteCode = siteCode;
        this.name = name;
        this.daysShip = daysShip;
        this.daysAir = daysAir;
        this.otherInfo = otherInfo;
    }

    public Site(String siteCode, String name, int daysShip, int daysAir, String otherInfo) {
        this.siteCode = siteCode;
        this.name = name;
        this.daysShip = daysShip;
        this.daysAir = daysAir;
        this.otherInfo = otherInfo;
    }

    public int getId() { return id; }
    public String getSiteCode() { return siteCode; }
    public String getName() { return name; }
    public int getDaysShip() { return daysShip; }
    public int getDaysAir() { return daysAir; }
    public String getOtherInfo() { return otherInfo; }

    public void setDaysShip(int daysShip) { this.daysShip = daysShip; }
    public void setDaysAir(int daysAir) { this.daysAir = daysAir; }
    public void setOtherInfo(String otherInfo) { this.otherInfo = otherInfo; }
}

