package com.system.domain.warehouse;

public interface IFinanceService {
    void triggerInvoiceDeduction(String orderId, int missingQty);
    void deductCost(String orderId);
}
