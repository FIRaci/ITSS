package com.itss;

public class ImportRequestHistory {
    private int id;
    private String requestId;
    private String actionType;
    private String changedBy;
    private String diffText;
    private String reason;
    private String changedAt;

    public ImportRequestHistory(int id, String requestId, String actionType, String changedBy, String diffText, String reason, String changedAt) {
        this.id = id;
        this.requestId = requestId;
        this.actionType = actionType;
        this.changedBy = changedBy;
        this.diffText = diffText;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    // Getters
    public int getId() { return id; }
    public String getRequestId() { return requestId; }
    public String getActionType() { return actionType; }
    public String getChangedBy() { return changedBy; }
    public String getDiffText() { return diffText; }
    public String getReason() { return reason; }
    public String getChangedAt() { return changedAt; }
}

