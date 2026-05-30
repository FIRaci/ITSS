package com.system.domain.masterdata;

public class Merchandise {
    private String merchandiseId;
    private String name;
    private String defaultUnit;
    private String businessStatus;
    public Merchandise(String id, String name) { this.merchandiseId = id; this.name = name; }
    public String getMerchandiseId() { return merchandiseId; }
    public String getName() { return name; }
}
