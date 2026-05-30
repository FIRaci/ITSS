package com.system.domain.order;

public class Proposal {
    private String proposalId;
    private String draftPlanDetails;
    private double estimatedCost;

    public Proposal() {}

    public Proposal(String proposalId, String draftPlanDetails, double estimatedCost) {
        this.proposalId = proposalId;
        this.draftPlanDetails = draftPlanDetails;
        this.estimatedCost = estimatedCost;
    }

    public String getProposalId() { return proposalId; }
    public String getDraftPlanDetails() { return draftPlanDetails; }
    public double getEstimatedCost() { return estimatedCost; }
    public void setProposalId(String proposalId) { this.proposalId = proposalId; }
    public void setDraftPlanDetails(String d) { this.draftPlanDetails = d; }
    public void setEstimatedCost(double c) { this.estimatedCost = c; }
}
