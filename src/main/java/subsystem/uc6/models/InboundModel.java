package subsystem.uc6.models;

import entity.uc6.InboundRecord;
import entity.uc6.DiscrepancyRecord;
import entity.chung.InternationalOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InboundModel {
    private ObservableList<InternationalOrder> incomingOrders;

    public InboundModel() {
        this.incomingOrders = FXCollections.observableArrayList();
    }

    public ObservableList<InternationalOrder> getIncomingOrders() { return incomingOrders; }
    public void setIncomingOrders(ObservableList<InternationalOrder> orders) { this.incomingOrders = orders; }
}
