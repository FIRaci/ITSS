package entity.uc6;

public class BufferStockRecord {
    private int orderId;
    private String merchandiseCode;
    private int bufferQty;
    private String reason;
    private String storedAt;
    private String storedBy;

    public BufferStockRecord(int orderId, String merchandiseCode, int bufferQty, String reason, String storedBy) {
        this.orderId = orderId;
        this.merchandiseCode = merchandiseCode;
        this.bufferQty = bufferQty;
        this.reason = reason;
        this.storedBy = storedBy;
    }

    public int getOrderId() { return orderId; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public int getBufferQty() { return bufferQty; }
    public String getReason() { return reason; }
    public String getStoredAt() { return storedAt; }
    public void setStoredAt(String storedAt) { this.storedAt = storedAt; }
    public String getStoredBy() { return storedBy; }
}
