package com.system.domain.order;

public class Proposal {
    private String proposalId;
    private String generatedPlanDetails;
    private double estimatedCost;

    public Proposal(String proposalId, String generatedPlanDetails, double estimatedCost) {
        this.proposalId = proposalId;
        this.generatedPlanDetails = generatedPlanDetails;
        this.estimatedCost = estimatedCost;
    }

    public String getProposalId() { return proposalId; }
    public String getGeneratedPlanDetails() { return generatedPlanDetails; }
    public double getEstimatedCost() { return estimatedCost; }
}
