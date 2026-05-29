package subsystem.uc6.models;

import entity.uc6.DiscrepancyRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DiscrepancyModel {
    private ObservableList<DiscrepancyRecord> records;

    public DiscrepancyModel() {
        this.records = FXCollections.observableArrayList();
    }

    public ObservableList<DiscrepancyRecord> getRecords() { return records; }
}
