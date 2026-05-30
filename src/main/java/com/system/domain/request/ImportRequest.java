package com.system.domain.request;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class ImportRequest {
    private String requestId;
    private Date creationDate;
    private Date desiredDeliveryDate;
    private String status;
    private int version;
    private boolean isAccepted;
    private String logicId;
    private List<RequestDetail> details;

    public ImportRequest(String requestId) {
        this.requestId = requestId;
        this.creationDate = new Date();
        this.status = "Chờ tiếp nhận";
        this.version = 1;
        this.isAccepted = false;
        this.details = new ArrayList<>();
    }

    public ImportRequest(String requestId, Date creationDate, String status, boolean isAccepted, int version) {
        this.requestId = requestId;
        this.creationDate = creationDate;
        this.status = status;
        this.isAccepted = isAccepted;
        this.version = version;
        this.details = new ArrayList<>();
    }

    public void addDetail(RequestDetail detail) { this.details.add(detail); }
    public void updateStatus(String status) { this.status = status; }
    public void submitRequest() { this.status = "Chờ tiếp nhận"; }
    public void incrementVersion() { this.version++; }
    public void setAccepted(boolean status) { this.isAccepted = status; }
    public void saveSelectedLogic(String logicId) { this.logicId = logicId; }

    public String getRequestId() { return requestId; }
    public Date getCreationDate() { return creationDate; }
    public Date getDesiredDeliveryDate() { return desiredDeliveryDate; }
    public void setDesiredDeliveryDate(Date d) { this.desiredDeliveryDate = d; }
    public String getStatus() { return status; }
    public int getVersion() { return version; }
    public boolean isAccepted() { return isAccepted; }
    public String getLogicId() { return logicId; }
    public List<RequestDetail> getDetails() { return details; }
}
