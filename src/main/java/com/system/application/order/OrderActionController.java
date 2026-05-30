package com.system.application.order;

import com.system.domain.order.OverseasOrder;
import com.system.domain.order.Proposal;
import com.system.infrastructure.persistence.OrderRepositoryImpl;
import java.util.List;

public class OrderActionController {
    private CancelAndErrorHandlingUseCase actionService;
    private OrderRepositoryImpl orderRepo;

    public OrderActionController() {
        this.actionService = new CancelAndErrorHandlingUseCase();
        this.orderRepo = new OrderRepositoryImpl();
    }

    public ResponseEntity<Void> cancelOrder(String orderId) {
        if (orderRepo.checkOnBoardStatus(orderId)) {
            return ResponseEntity.failure("In-transit cargo cannot be cancelled");
        }
        actionService.submitCancelRequest(orderId);
        orderRepo.updateOverseasOrderStatus(orderId, "Cancelled");
        return ResponseEntity.success("Order cancelled successfully");
    }

    public ResponseEntity<List<Proposal>> generateReplacementPlan(String orderId) {
        actionService.requestErrorProcessing(orderId);
        List<Proposal> proposals = orderRepo.findProposalsByOrderId(orderId);
        return ResponseEntity.success("Success", proposals);
    }

    public ResponseEntity<Void> submitApproval(String proposalId) {
        actionService.submitApprovalRequest(proposalId);
        return ResponseEntity.success("Approval submitted");
    }

    public ResponseEntity<Void> confirmOrderUpdate() {
        actionService.confirmOrderUpdate();
        return ResponseEntity.success("Order updated");
    }

    public ResponseEntity<Void> processRejection(String orderId, String reason) {
        actionService.processRejection(orderId, reason);
        orderRepo.updateOverseasOrderStatus(orderId, "Rejected");
        return ResponseEntity.success("Rejection processed");
    }
}
