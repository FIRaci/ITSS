package subsystem.uc4.controllers;

import entity.uc4.CancellationProposal;
import com.system.infrastructure.persistence.OrderRepositoryImpl;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.stream.Collectors;

public class CancellationSubController {
    private OrderRepositoryImpl orderRepository;

    public CancellationSubController() {
        this.orderRepository = new OrderRepositoryImpl();
    }

    public ObservableList<CancellationProposal> getPendingProposals() {
        ObservableList<com.itss.CancellationRequest> raw = orderRepository.getPendingCancellations();
        return FXCollections.observableArrayList(
            raw.stream().map(c -> new CancellationProposal(
                c.getId(), c.getOrderId(), c.getReason(), c.getStatus(), c.getRequestedAt()
            )).collect(Collectors.toList())
        );
    }

    public boolean approve(int cancelId, int orderId) throws Exception {
        return orderRepository.approveCancellation(cancelId, orderId);
    }

    public boolean reject(int cancelId, int orderId) throws Exception {
        return orderRepository.rejectCancellation(cancelId, orderId);
    }
}
