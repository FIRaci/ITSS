package com.system.domain.order;

public class RiskReport {
    private String reportId;
    private String impactLevel;
    private String riskDescription;

    public RiskReport() {}

    public RiskReport(String reportId, String impactLevel, String riskDescription) {
        this.reportId = reportId;
        this.impactLevel = impactLevel;
        this.riskDescription = riskDescription;
    }

    public String getReportId() { return reportId; }
    public String getImpactLevel() { return impactLevel; }
    public String getRiskDescription() { return riskDescription; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public void setImpactLevel(String level) { this.impactLevel = level; }
    public void setRiskDescription(String desc) { this.riskDescription = desc; }
}
