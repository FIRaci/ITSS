package com.system.domain.warehouse;

import java.util.Date;

public class DiscrepancyReport {
    private String reportId;
    private String orderId;
    private String damageDescription;
    private String evidenceImage;
    private Date createdDate;
    private String issueType;
    private boolean isResolved;

    public DiscrepancyReport() {}

    public DiscrepancyReport(String reportId, String orderId, String damageDescription, String evidenceImage) {
        this.reportId = reportId;
        this.orderId = orderId;
        this.damageDescription = damageDescription;
        this.evidenceImage = evidenceImage;
        this.createdDate = new Date();
        this.isResolved = false;
    }

    public void createDiscrepancyReport(String orderId, String image, String description) {
        this.orderId = orderId;
        this.evidenceImage = image;
        this.damageDescription = description;
        this.createdDate = new Date();
    }

    public void generateReturnOrder() { this.isResolved = true; }

    public String getReportId() { return reportId; }
    public String getOrderId() { return orderId; }
    public String getDamageDescription() { return damageDescription; }
    public String getEvidenceImage() { return evidenceImage; }
    public Date getCreatedDate() { return createdDate; }
    public String getIssueType() { return issueType; }
    public boolean isResolved() { return isResolved; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setDamageDescription(String desc) { this.damageDescription = desc; }
    public void setIssueType(String t) { this.issueType = t; }
    public void setResolved(boolean r) { this.isResolved = r; }
}
