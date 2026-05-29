package entity.uc4;

import entity.chung.CancellationRequest;
import entity.chung.InternationalOrder;

public class CancellationProposal extends CancellationRequest {
    private InternationalOrder order;
    private String reviewedBy;
    private String reviewNote;

    public CancellationProposal(int id, int orderId, String reason, String status, String requestedAt) {
        super(id, orderId, reason, status, requestedAt);
    }

    public InternationalOrder getOrder() { return order; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
}
