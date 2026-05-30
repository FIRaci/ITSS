package com.system.domain.masterdata;

public class Merchandise {
    private String merchandiseId;
    private String merchandiseName;
    private String defaultUnit;
    private String businessStatus;

    public Merchandise() {}

    public Merchandise(String merchandiseId, String merchandiseName, String defaultUnit) {
        this.merchandiseId = merchandiseId;
        this.merchandiseName = merchandiseName;
        this.defaultUnit = defaultUnit;
        this.businessStatus = "Đang kinh doanh";
    }

    public boolean checkExistence(String merchandiseId) { return this.merchandiseId != null && this.merchandiseId.equals(merchandiseId); }
    public Merchandise getMerchandiseInfo(String merchandiseId) { return checkExistence(merchandiseId) ? this : null; }

    public String getMerchandiseId() { return merchandiseId; }
    public String getMerchandiseName() { return merchandiseName; }
    public String getDefaultUnit() { return defaultUnit; }
    public String getBusinessStatus() { return businessStatus; }
    public void setMerchandiseId(String id) { this.merchandiseId = id; }
    public void setMerchandiseName(String name) { this.merchandiseName = name; }
    public void setDefaultUnit(String u) { this.defaultUnit = u; }
    public void setBusinessStatus(String s) { this.businessStatus = s; }
}
