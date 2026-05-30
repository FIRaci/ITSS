package com.system.application.order;

import com.system.domain.order.ILogisticsService;
import com.system.domain.order.INotificationService;
import com.system.domain.order.IOverseasOrderRepository;
import com.system.domain.order.OptimizationEngine;
import com.system.domain.order.OverseasOrder;
import com.system.domain.order.Proposal;
import com.system.infrastructure.adapters.LogisticsServiceAdapter;
import com.system.infrastructure.adapters.NotificationAdapterImpl;
import com.system.infrastructure.persistence.OverseasOrderRepositoryImpl;
import java.util.List;

public class CancelAndErrorHandlingUseCase {
    private IOverseasOrderRepository orderRepo;
    private ILogisticsService logisticsService;
    private OptimizationEngine optimizationEngine;
    private INotificationService notifyService;

    public CancelAndErrorHandlingUseCase() {
        this(new OverseasOrderRepositoryImpl(), new LogisticsServiceAdapter(), new OptimizationEngine(), new NotificationAdapterImpl());
    }

    public CancelAndErrorHandlingUseCase(
        IOverseasOrderRepository orderRepo,
        ILogisticsService logisticsService,
        OptimizationEngine optimizationEngine,
        INotificationService notifyService
    ) {
        this.orderRepo = orderRepo;
        this.logisticsService = logisticsService;
        this.optimizationEngine = optimizationEngine;
        this.notifyService = notifyService;
    }

    public void submitCancelRequest(String orderId) {
        processCancellation(orderId);
    }

    public void requestErrorProcessing(String orderId) {
        processSiteError(orderId);
    }

    public void submitApprovalRequest(Proposal proposal) {
        submitProposalForApproval(proposal.getProposalId());
    }

    public void confirmOrderUpdate() {
    }

    public void processCancellation(String orderId) {
        OverseasOrder order = orderRepo.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang.");
        }
        if (!order.isCancelable()) {
            throw new IllegalStateException("Trang thai hien tai khong cho phep huy.");
        }
        if (logisticsService.checkOnBoardStatus(orderId)) {
            throw new IllegalStateException("Don hang dang giao (on-board), khong the huy.");
        }
        order.changeStatus("Đã hủy");
        orderRepo.save(order);
    }

    public List<Proposal> processSiteError(String orderId) {
        OverseasOrder order = orderRepo.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang.");
        }
        return optimizationEngine.runOptimizationRules(order);
    }

    public void submitProposalForApproval(String proposalId) {
        notifyService.sendCrossDepartmentAlert("Yeu cau phe duyet phuong an: " + proposalId);
    }

    public void processRejection(String orderId, String reason) {
        OverseasOrder order = orderRepo.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang.");
        }
        order.changeStatus("Đã đặt hàng");
        orderRepo.save(order);
        notifyService.sendCrossDepartmentAlert("Tu choi phuong an thay the cho don hang " + orderId + ": " + reason);
    }
}
