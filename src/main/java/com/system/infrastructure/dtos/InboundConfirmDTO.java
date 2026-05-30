package com.system.infrastructure.dtos;

import java.util.List;
import java.util.ArrayList;

public class InboundConfirmDTO {
    public String orderId;
    public List<Item> itemsList;

    public InboundConfirmDTO() {
        this.itemsList = new ArrayList<>();
    }

    public InboundConfirmDTO(String orderId, List<Item> itemsList) {
        this.orderId = orderId;
        this.itemsList = itemsList;
    }

    public InboundConfirmDTO(String orderId) {
        this.orderId = orderId;
        this.itemsList = new ArrayList<>();
    }

    public static class Item {
        public String sku;
        public String merchandiseCode;
        public int qty;

        public Item() {}

        public Item(String sku, String merchandiseCode, int qty) {
            this.sku = sku;
            this.merchandiseCode = merchandiseCode;
            this.qty = qty;
        }
    }
}
