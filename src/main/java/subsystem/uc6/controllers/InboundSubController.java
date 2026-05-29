package subsystem.uc6.controllers;

import entity.uc6.InboundRecord;
import entity.uc6.DiscrepancyRecord;
import entity.chung.InternationalOrder;
import com.system.infrastructure.persistence.WarehouseRepositoryImpl;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.stream.Collectors;

public class InboundSubController {
    private WarehouseRepositoryImpl repository;

    public InboundSubController() {
        this.repository = new WarehouseRepositoryImpl();
    }

    public ObservableList<InternationalOrder> getIncomingOrders() {
        ObservableList<com.itss.InternationalOrder> oldOrders = repository.getIncomingOrders();
        return FXCollections.observableArrayList(
            oldOrders.stream().map(o -> new InternationalOrder(
                o.getId(), o.getRequestId(), o.getSiteCode(), o.getMerchandiseCode(),
                o.getQty(), o.getShippingMethod(), o.getStatus()
            )).collect(Collectors.toList())
        );
    }

    public void confirmFull(InboundRecord record) throws Exception {
        boolean success = repository.receiveFullOrder(record.getOrder().getId());
        if (!success) {
            throw new Exception("Không thể xác nhận nhập kho.");
        }
    }
}
