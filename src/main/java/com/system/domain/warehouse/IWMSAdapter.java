package com.system.domain.warehouse;

public interface IWMSAdapter {
    void syncInbound(String orderId);
    void updateInventory(String skuId, int qty);
}
