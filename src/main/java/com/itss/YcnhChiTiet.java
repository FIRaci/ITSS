package com.itss;

public class YcnhChiTiet {
    private int id;
    private String ycnhId;
    private String merchandiseCode;
    private int quantity;
    private String unit;
    private String desiredDeliveryDate;

    // Used to track state in UI (Add/Edit/Delete)
    private String uiAction = "None"; // None, Add, Edit, Delete

    public YcnhChiTiet(int id, String ycnhId, String merchandiseCode, int quantity, String unit, String desiredDeliveryDate) {
        this.id = id;
        this.ycnhId = ycnhId;
        this.merchandiseCode = merchandiseCode;
        this.quantity = quantity;
        this.unit = unit;
        this.desiredDeliveryDate = desiredDeliveryDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getYcnhId() { return ycnhId; }
    public void setYcnhId(String ycnhId) { this.ycnhId = ycnhId; }
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

    public YcnhChiTiet clone() {
        return new YcnhChiTiet(this.id, this.ycnhId, this.merchandiseCode, this.quantity, this.unit, this.desiredDeliveryDate);
    }
}
