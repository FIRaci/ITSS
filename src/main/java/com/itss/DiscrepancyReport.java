package com.itss;

public class DiscrepancyReport {
    private int id;
    private int orderId;
    private String requestId;
    private String siteCode;
    private String note;
    private String evidencePath;
    private String status;
    private String createdBy;
    private String createdAt;

    public DiscrepancyReport(int id, int orderId, String requestId, String siteCode, String note, String evidencePath, String status, String createdBy, String createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.requestId = requestId;
        this.siteCode = siteCode;
        this.note = note;
        this.evidencePath = evidencePath;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public String getYcnhId() { return requestId; }
    public String getSiteCode() { return siteCode; }
    public String getNote() { return note; }
    public String getEvidencePath() { return evidencePath; }
    public String getStatus() { return status; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
}

