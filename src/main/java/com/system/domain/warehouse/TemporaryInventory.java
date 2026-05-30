package com.system.domain.warehouse;

import java.util.Date;
public class TemporaryInventory {
    private int bufferStockQty;
    private String holdingStatus;
    private Date expiryCountdownDate;
    public void isolateExcessGoods(int qty) { }
    public Date calculateExpiryDate(int days) { return new Date(); }
}
