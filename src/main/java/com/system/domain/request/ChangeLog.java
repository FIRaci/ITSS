package com.system.domain.request;

import java.util.Date;

public class ChangeLog {
    private int logId;
    private String requestId;
    private Date editDate;
    private String editedBy;
    private String reason;
    private String diffContent;
    private int failedAttemptCount;

    public ChangeLog() {}

    public ChangeLog(int logId, String requestId, String editedBy, String reason, String diffContent) {
        this.logId = logId;
        this.requestId = requestId;
        this.editDate = new Date();
        this.editedBy = editedBy;
        this.reason = reason;
        this.diffContent = diffContent;
    }

    public void createEditLog() { this.editDate = new Date(); }

    public int getLogId() { return logId; }
    public String getRequestId() { return requestId; }
    public Date getEditDate() { return editDate; }
    public String getEditedBy() { return editedBy; }
    public String getReason() { return reason; }
    public String getDiffContent() { return diffContent; }
    public int getFailedAttemptCount() { return failedAttemptCount; }
    public void setFailedAttemptCount(int c) { this.failedAttemptCount = c; }
    public void setLogId(int logId) { this.logId = logId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setEditDate(Date editDate) { this.editDate = editDate; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }
    public void setReason(String reason) { this.reason = reason; }
    public void setDiffContent(String diffContent) { this.diffContent = diffContent; }
}
