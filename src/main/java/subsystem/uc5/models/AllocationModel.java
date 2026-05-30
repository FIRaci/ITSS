package subsystem.uc5.models;

import entity.chung.AllocationRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AllocationModel {
    private String requestId;
    private ObservableList<AllocationRow> plan;

    public AllocationModel() {
        this.plan = FXCollections.observableArrayList();
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public ObservableList<AllocationRow> getPlan() { return plan; }
    public void setPlan(ObservableList<AllocationRow> plan) { this.plan = plan; }
}
