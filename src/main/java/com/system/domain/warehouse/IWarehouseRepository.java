package com.system.domain.warehouse;

import java.util.List;

public interface IWarehouseRepository {
    Inventory findInventoryBySku(String sku);
    void saveInventory(Inventory inv);
    void saveTemporary(TemporaryInventory temp);
    void saveReport(DiscrepancyReport report);
    List<TemporaryInventory> findExpiredHoldings();
}
