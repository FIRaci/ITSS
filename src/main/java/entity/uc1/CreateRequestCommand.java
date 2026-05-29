package entity.uc1;

import entity.chung.ImportRequest;
import entity.chung.ImportRequestDetail;
import java.util.List;

public class CreateRequestCommand {
    private ImportRequest request;
    private List<ImportRequestDetail> details;
    private String createdBy;

    public CreateRequestCommand(ImportRequest request, List<ImportRequestDetail> details, String createdBy) {
        this.request = request;
        this.details = details;
        this.createdBy = createdBy;
    }

    public ImportRequest getRequest() { return request; }
    public List<ImportRequestDetail> getDetails() { return details; }
    public String getCreatedBy() { return createdBy; }
}
