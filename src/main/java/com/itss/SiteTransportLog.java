package com.itss;

public class SiteTransportLog {
    private int id;
    private String siteCode;
    private int oldDaysShip;
    private int newDaysShip;
    private int oldDaysAir;
    private int newDaysAir;
    private String note;
    private String changedBy;
    private String changedAt;

    public SiteTransportLog(int id, String siteCode, int oldDaysShip, int newDaysShip, int oldDaysAir, int newDaysAir, String note, String changedBy, String changedAt) {
        this.id = id;
        this.siteCode = siteCode;
        this.oldDaysShip = oldDaysShip;
        this.newDaysShip = newDaysShip;
        this.oldDaysAir = oldDaysAir;
        this.newDaysAir = newDaysAir;
        this.note = note;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public int getId() { return id; }
    public String getSiteCode() { return siteCode; }
    public int getOldDaysShip() { return oldDaysShip; }
    public int getNewDaysShip() { return newDaysShip; }
    public int getOldDaysAir() { return oldDaysAir; }
    public int getNewDaysAir() { return newDaysAir; }
    public String getNote() { return note; }
    public String getChangedBy() { return changedBy; }
    public String getChangedAt() { return changedAt; }
}
