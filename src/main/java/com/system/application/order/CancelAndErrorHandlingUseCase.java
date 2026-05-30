package com.system.application.order;

import com.system.domain.order.Proposal;

public class CancelAndErrorHandlingUseCase {
    public void submitCancelRequest(String orderId) { }
    public void requestErrorProcessing(String orderId) { }
    public void processRejection(String orderId, String reason) { }

    public boolean submitApprovalRequest(int proposalId) {
        return true;
    }

    public boolean submitApprovalRequest(String proposalId) {
        try { return submitApprovalRequest(Integer.parseInt(proposalId)); }
        catch (NumberFormatException e) { return false; }
    }

    public boolean confirmOrderUpdate() {
        return true;
    }
}
