package com.itss;

public class DiscrepancyItem {
    private int id;
    private int reportId;
    private String merchandiseCode;
    private int qtyReported;
    private String reason;

    public DiscrepancyItem(int id, int reportId, String merchandiseCode, int qtyReported, String reason) {
        this.id = id;
        this.reportId = reportId;
        this.merchandiseCode = merchandiseCode;
        this.qtyReported = qtyReported;
        this.reason = reason;
    }

    public int getId() { return id; }
    public int getReportId() { return reportId; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public int getQtyReported() { return qtyReported; }
    public String getReason() { return reason; }
}

