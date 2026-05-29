package controller.uc6;

import entity.uc6.InboundRecord;
import entity.chung.InternationalOrder;
import subsystem.uc6.models.InboundModel;
import subsystem.uc6.controllers.InboundSubController;
import javafx.collections.ObservableList;

public class InboundController {
    private InboundSubController subController;
    private InboundModel model;

    public InboundController() {
        this.subController = new InboundSubController();
        this.model = new InboundModel();
    }

    public ObservableList<InternationalOrder> getIncomingOrders() {
        return subController.getIncomingOrders();
    }

    public void confirmFullInbound(InboundRecord record) throws Exception {
        subController.confirmFull(record);
    }

    public InboundModel getModel() { return model; }
}
