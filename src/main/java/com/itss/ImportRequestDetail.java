package com.itss;

public class ImportRequestDetail {
    private int id;
    private String requestId;
    private String merchandiseCode;
    private int quantity;
    private String unit;
    private String desiredDeliveryDate;

    // Used to track state in UI (Add/Edit/Delete)
    private String uiAction = "None"; // None, Add, Edit, Delete

    public ImportRequestDetail(int id, String requestId, String merchandiseCode, int quantity, String unit, String desiredDeliveryDate) {
        this.id = id;
        this.requestId = requestId;
        this.merchandiseCode = merchandiseCode;
        this.quantity = quantity;
        this.unit = unit;
        this.desiredDeliveryDate = desiredDeliveryDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getMerchandiseCode() { return merchandiseCode; }
    public void setMerchandiseCode(String merchandiseCode) { this.merchandiseCode = merchandiseCode; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDesiredDeliveryDate() { return desiredDeliveryDate; }
    public void setDesiredDeliveryDate(String desiredDeliveryDate) { this.desiredDeliveryDate = desiredDeliveryDate; }
    
    public String getUiAction() { return uiAction; }
    public void setUiAction(String uiAction) { this.uiAction = uiAction; }

    public ImportRequestDetail clone() {
        return new ImportRequestDetail(this.id, this.requestId, this.merchandiseCode, this.quantity, this.unit, this.desiredDeliveryDate);
    }
}

