package com.system.application.warehouse;

import com.system.domain.warehouse.Inventory;
import com.system.domain.warehouse.TemporaryInventory;
import com.system.domain.warehouse.DiscrepancyReport;
import com.system.infrastructure.dtos.InboundConfirmDTO;
import com.system.infrastructure.dtos.DiscrepancyDTO;
import com.system.infrastructure.dtos.BufferStockDTO;
import com.system.infrastructure.persistence.OrderRepositoryImpl;
import com.system.infrastructure.persistence.WarehouseRepositoryImpl;

public class InboundController {
    private InboundVerificationUseCase inboundService;
    private OrderRepositoryImpl orderRepo;
    private WarehouseRepositoryImpl warehouseRepo;

    public InboundController() {
        this.inboundService = new InboundVerificationUseCase();
        this.orderRepo = new OrderRepositoryImpl();
        this.warehouseRepo = new WarehouseRepositoryImpl();
    }

    public void processInboundReceipt(String orderId, java.util.List<InboundConfirmDTO.Item> items) throws Exception {
        inboundService.processInbound(new InboundConfirmDTO(orderId, items));
        if (orderRepo.isOrderCancelled(orderId)) {
            throw new Exception("Đơn hàng đã bị hủy, không thể xác nhận nhập kho");
        }
        inboundService.compareQuantity(orderRepo.getOrderedQty(orderId), items.size());
        for (InboundConfirmDTO.Item item : items) {
            Inventory inv = new Inventory(item.sku, item.sku, item.qty);
            inv.increaseInventory(item.qty);
            warehouseRepo.saveInventory(inv);
        }
        orderRepo.updateOverseasOrderStatus(orderId, "COMPLETED");
    }

    public void handleDiscrepancy(String orderId, String description, String imageRef) {
        DiscrepancyDTO dto = new DiscrepancyDTO(orderId, description, imageRef);
        inboundService.handleDiscrepancy(dto);
        DiscrepancyReport report = new DiscrepancyReport("DR-" + orderId, orderId, description, imageRef);
        warehouseRepo.saveReport(report);
        orderRepo.updateOverseasOrderStatus(orderId, "PARTIAL_HOLDING");
    }

    public void handleBufferStorage(String orderId, int surplusQty) {
        BufferStockDTO dto = new BufferStockDTO(orderId, surplusQty);
        inboundService.handleBufferStorage(dto);
        TemporaryInventory temp = new TemporaryInventory("BUF-" + orderId, surplusQty, 7);
        temp.setOrderId(orderId);
        warehouseRepo.saveTemporary(temp);
    }

    public void processPartnerCompensation(String orderId, java.util.List<InboundConfirmDTO.Item> newItems) {
        inboundService.processInbound(new InboundConfirmDTO(orderId, newItems));
        for (InboundConfirmDTO.Item item : newItems) {
            Inventory inv = new Inventory(item.sku, item.sku, item.qty);
            inv.increaseInventory(item.qty);
            warehouseRepo.saveInventory(inv);
        }
        orderRepo.updateOverseasOrderStatus(orderId, "COMPLETED");
    }

    public void triggerForceCloseCron() {
        inboundService.executeAutomaticForceClose();
        java.util.List<TemporaryInventory> expired = warehouseRepo.findExpiredHoldings();
        for (TemporaryInventory t : expired) {
            t.markForDisposal();
            orderRepo.updateOverseasOrderStatus(t.getOrderId(), "FORCE_CLOSED");
        }
    }
}
