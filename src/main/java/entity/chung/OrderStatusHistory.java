package entity.chung;

public class OrderStatusHistory {
    private int id;
    private int orderId;
    private String oldStatus;
    private String newStatus;
    private String note;
    private String changedBy;
    private String changedAt;

    public OrderStatusHistory(int id, int orderId, String oldStatus, String newStatus, String note, String changedBy, String changedAt) {
        this.id = id;
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.note = note;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
    public String getNote() { return note; }
    public String getChangedBy() { return changedBy; }
    public String getChangedAt() { return changedAt; }
}
