package com.itss;

public class CancellationRequest {
    private int id;
    private int orderId;
    private String reason;
    private String status;
    private String createdBy;
    private String createdAt;
    private String handledBy;
    private String handledAt;

    public CancellationRequest(int id, int orderId, String reason, String status, String createdBy, String createdAt, String handledBy, String handledAt) {
        this.id = id;
        this.orderId = orderId;
        this.reason = reason;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.handledBy = handledBy;
        this.handledAt = handledAt;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getHandledBy() { return handledBy; }
    public String getHandledAt() { return handledAt; }
}
