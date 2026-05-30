package com.system.domain.order;

public class ApprovalRequest {
    private String approvalId;
    private String managerDecision;
    private String rejectionReason;

    public ApprovalRequest() {}

    public ApprovalRequest(String approvalId, String managerDecision) {
        this.approvalId = approvalId;
        this.managerDecision = managerDecision;
    }

    public String getApprovalId() { return approvalId; }
    public String getManagerDecision() { return managerDecision; }
    public String getRejectionReason() { return rejectionReason; }
    public void setApprovalId(String approvalId) { this.approvalId = approvalId; }
    public void setManagerDecision(String d) { this.managerDecision = d; }
    public void setRejectionReason(String r) { this.rejectionReason = r; }
}
