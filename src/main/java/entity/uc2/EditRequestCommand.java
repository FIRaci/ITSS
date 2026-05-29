package entity.uc2;

import entity.uc2.EditRequestItem;
import java.util.List;

public class EditRequestCommand {
    private String requestId;
    private List<EditRequestItem> oldDetails;
    private List<EditRequestItem> newDetails;
    private String reason;
    private String changedBy;

    public EditRequestCommand(String requestId, List<EditRequestItem> oldDetails, List<EditRequestItem> newDetails, String reason, String changedBy) {
        this.requestId = requestId;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
        this.reason = reason;
        this.changedBy = changedBy;
    }

    public String getRequestId() { return requestId; }
    public List<EditRequestItem> getOldDetails() { return oldDetails; }
    public List<EditRequestItem> getNewDetails() { return newDetails; }
    public String getReason() { return reason; }
    public String getChangedBy() { return changedBy; }
}
