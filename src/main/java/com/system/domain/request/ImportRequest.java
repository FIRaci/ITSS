package com.system.domain.request;

import java.util.Date;
public class ImportRequest {
    private String requestId;
    private Date creationDate;
    private String status;
    private boolean isAccepted;
    private int versionId;
    public void updateStatus(String status) { this.status = status; }
    public void incrementVersion() { this.versionId++; }
    public String getRequestId() { return requestId; }
    public String getStatus() { return status; }
}
