package com.system.application.warehouse;

import com.system.infrastructure.dtos.*;
public class InboundVerificationUseCase {
    public void processInbound(InboundConfirmDTO dto) { }
    public void compareQuantity(int expected, int actual) { }
    public void handleDiscrepancy(DiscrepancyDTO dto) { }
    public void handleBufferStorage(BufferStockDTO dto) { }
    public void executeAutomaticForceClose() { }
}
