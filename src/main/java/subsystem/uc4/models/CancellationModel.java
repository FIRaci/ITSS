package subsystem.uc4.models;

import entity.uc4.CancellationProposal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CancellationModel {
    private ObservableList<CancellationProposal> proposals;

    public CancellationModel() {
        this.proposals = FXCollections.observableArrayList();
    }

    public ObservableList<CancellationProposal> getProposals() { return proposals; }
    public void setProposals(ObservableList<CancellationProposal> proposals) { this.proposals = proposals; }
}
