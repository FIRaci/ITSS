package com.system.application.order;

import com.system.domain.order.Proposal;
import java.util.List;

public class OrderActionController {
    private CancelAndErrorHandlingUseCase actionService;

    public OrderActionController() {
        this.actionService = new CancelAndErrorHandlingUseCase();
    }

    public ResponseEntity<Void> cancelOrder(String orderId) {
        try {
            actionService.processCancellation(orderId);
            return ResponseEntity.success("Da huy don hang.");
        } catch (Exception ex) {
            return ResponseEntity.failure(ex.getMessage());
        }
    }

    public ResponseEntity<List<Proposal>> generateReplacementPlan(String orderId) {
        try {
            List<Proposal> proposals = actionService.processSiteError(orderId);
            return ResponseEntity.success("Da tao phuong an thay the.", proposals);
        } catch (Exception ex) {
            return ResponseEntity.failure(ex.getMessage());
        }
    }

    public ResponseEntity<Void> submitApproval(String proposalId) {
        try {
            actionService.submitProposalForApproval(proposalId);
            return ResponseEntity.success("Da gui yeu cau phe duyet.");
        } catch (Exception ex) {
            return ResponseEntity.failure(ex.getMessage());
        }
    }
}
