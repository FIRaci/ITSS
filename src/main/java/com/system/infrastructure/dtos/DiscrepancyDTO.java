package com.system.infrastructure.dtos;

public class DiscrepancyDTO {
    public String orderId;
    public String reason;
    public String description;
    public String imageRef;
    public int missingQty;
    public int damagedQty;

    public DiscrepancyDTO() {}

    public DiscrepancyDTO(String orderId, String description, String imageRef) {
        this.orderId = orderId;
        this.description = description;
        this.imageRef = imageRef;
    }
}
