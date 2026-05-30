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

    public String cancelOrder(String orderId) {
        if (orderRepo.checkOnBoardStatus(orderId)) {
            return "In-transit cargo cannot be cancelled";
        }
        actionService.submitCancelRequest(orderId);
        orderRepo.updateOverseasOrderStatus(orderId, "Cancelled");
        return "Order cancelled successfully";
    }

    public List<Proposal> generateReplacementPlan(String orderId) {
        actionService.requestErrorProcessing(orderId);
        return orderRepo.findProposalsByOrderId(orderId);
    }

    public void submitApproval(String proposalId) {
        actionService.submitApprovalRequest(proposalId);
    }

    public void confirmOrderUpdate() {
        actionService.confirmOrderUpdate();
    }

    public String processRejection(String orderId, String reason) {
        actionService.processRejection(orderId, reason);
        orderRepo.updateOverseasOrderStatus(orderId, "Rejected");
        return "Rejection processed";
    }
}
