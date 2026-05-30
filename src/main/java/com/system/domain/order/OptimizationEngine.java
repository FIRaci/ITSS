package com.system.domain.order;

import java.util.ArrayList;
import java.util.List;

public class OptimizationEngine {
    private List<Proposal> workingProposals;

    public List<Proposal> runOptimizationRules(OverseasOrder order) {
        workingProposals = new ArrayList<>();
        String proposalId = "P-" + order.getOrderId() + "-01";
        String details = "De xuat Site thay the cho don hang " + order.getOrderId();
        workingProposals.add(new Proposal(proposalId, details, 0.0));

        filterP1CostEfficiency();
        filterP2InventorySafety();
        filterP3MinimumPickups();

        return new ArrayList<>(workingProposals);
    }

    private void filterP1CostEfficiency() {
    }

    private void filterP2InventorySafety() {
    }

    private void filterP3MinimumPickups() {
    }
}
