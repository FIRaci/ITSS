package com.system.infrastructure.dtos;

public class BufferStockDTO {
    public String orderId;
    public int excessQty;
    public String warehouseZone;

    public BufferStockDTO() {}

    public BufferStockDTO(String orderId, int excessQty) {
        this.orderId = orderId;
        this.excessQty = excessQty;
    }
}
