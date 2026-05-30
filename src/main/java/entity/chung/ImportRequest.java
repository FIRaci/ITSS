package entity.chung;

public class ImportRequest {
    private String id;
    private String status;
    private boolean isAccepted;
    private String createdBy;
    private String createdAt;

    public ImportRequest(String id, String status, boolean isAccepted, String createdBy, String createdAt) {
        this.id = id;
        this.status = status;
        this.isAccepted = isAccepted;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isAccepted() { return isAccepted; }
    public void setAccepted(boolean accepted) { isAccepted = accepted; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
