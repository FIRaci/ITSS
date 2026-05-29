package entity.chung;

public class CancellationRequest {
    private int id;
    private int orderId;
    private String reason;
    private String status;
    private String requestedAt;

    public CancellationRequest(int id, int orderId, String reason, String status, String requestedAt) {
        this.id = id;
        this.orderId = orderId;
        this.reason = reason;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getRequestedAt() { return requestedAt; }
}
