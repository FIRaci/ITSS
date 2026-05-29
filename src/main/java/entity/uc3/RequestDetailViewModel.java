package entity.uc3;

import entity.chung.ImportRequest;
import entity.chung.ImportRequestDetail;
import entity.chung.ImportRequestHistory;
import java.util.List;

public class RequestDetailViewModel {
    private ImportRequest request;
    private List<ImportRequestDetail> details;
    private List<ImportRequestHistory> history;
    private String keyword;

    public RequestDetailViewModel(ImportRequest request, List<ImportRequestDetail> details, List<ImportRequestHistory> history) {
        this.request = request;
        this.details = details;
        this.history = history;
    }

    public ImportRequest getRequest() { return request; }
    public List<ImportRequestDetail> getDetails() { return details; }
    public List<ImportRequestHistory> getHistory() { return history; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
