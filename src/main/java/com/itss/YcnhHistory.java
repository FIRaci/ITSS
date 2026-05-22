package com.itss;

public class YcnhHistory {
    private int id;
    private String ycnhId;
    private String actionType;
    private String changedBy;
    private String diffText;
    private String reason;
    private String changedAt;

    public YcnhHistory(int id, String ycnhId, String actionType, String changedBy, String diffText, String reason, String changedAt) {
        this.id = id;
        this.ycnhId = ycnhId;
        this.actionType = actionType;
        this.changedBy = changedBy;
        this.diffText = diffText;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    // Getters
    public int getId() { return id; }
    public String getYcnhId() { return ycnhId; }
    public String getActionType() { return actionType; }
    public String getChangedBy() { return changedBy; }
    public String getDiffText() { return diffText; }
    public String getReason() { return reason; }
    public String getChangedAt() { return changedAt; }
}
