package com.system.domain.warehouse;

public class Inventory {
    private String skuId;
    private String productName;
    private int currentQty;
    private String storageLocation;
    private String qualityStatus;

    public Inventory() {}

    public Inventory(String skuId, String productName, int currentQty) {
        this.skuId = skuId;
        this.productName = productName;
        this.currentQty = currentQty;
        this.qualityStatus = "Tốt";
    }

    public void increaseInventory(int validQty) { this.currentQty += validQty; }
    public void addOfficialStock(int qty) { this.currentQty += qty; }

    public String getSkuId() { return skuId; }
    public String getProductName() { return productName; }
    public int getCurrentQty() { return currentQty; }
    public String getStorageLocation() { return storageLocation; }
    public String getQualityStatus() { return qualityStatus; }
    public void setSkuId(String skuId) { this.skuId = skuId; }
    public void setProductName(String name) { this.productName = name; }
    public void setCurrentQty(int qty) { this.currentQty = qty; }
    public void setStorageLocation(String loc) { this.storageLocation = loc; }
    public void setQualityStatus(String s) { this.qualityStatus = s; }
}
