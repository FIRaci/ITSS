package com.system.domain.order;

public class ApprovalRequest {
    private String requestId;
    private boolean approved;
    private String rejectionReason;

    public ApprovalRequest(String requestId, boolean approved, String rejectionReason) {
        this.requestId = requestId;
        this.approved = approved;
        this.rejectionReason = rejectionReason;
    }

    public String getRequestId() { return requestId; }
    public boolean isApproved() { return approved; }
    public String getRejectionReason() { return rejectionReason; }
}
