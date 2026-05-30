package entity.uc6;

public class DiscrepancyRecord {
    private int orderId;
    private String merchandiseCode;
    private String reason;
    private int qtyDiscrepancy;
    private String evidencePath;
    private String note;
    private String reportedBy;
    private String reportedAt;

    public DiscrepancyRecord(int orderId, String merchandiseCode, String reason, int qtyDiscrepancy, String evidencePath, String note, String reportedBy) {
        this.orderId = orderId;
        this.merchandiseCode = merchandiseCode;
        this.reason = reason;
        this.qtyDiscrepancy = qtyDiscrepancy;
        this.evidencePath = evidencePath;
        this.note = note;
        this.reportedBy = reportedBy;
    }

    public int getOrderId() { return orderId; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public String getReason() { return reason; }
    public int getQtyDiscrepancy() { return qtyDiscrepancy; }
    public String getEvidencePath() { return evidencePath; }
    public String getNote() { return note; }
    public String getReportedBy() { return reportedBy; }
    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }
}
