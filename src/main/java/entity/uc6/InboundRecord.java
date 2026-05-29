package entity.uc6;

import entity.chung.InternationalOrder;
import java.util.List;

public class InboundRecord {
    private InternationalOrder order;
    private int expectedQty;
    private int actualQty;
    private String receivedBy;
    private String receivedAt;
    private List<InboundLineItem> lineItems;

    public InboundRecord(InternationalOrder order, int expectedQty, int actualQty, String receivedBy) {
        this.order = order;
        this.expectedQty = expectedQty;
        this.actualQty = actualQty;
        this.receivedBy = receivedBy;
    }

    public InternationalOrder getOrder() { return order; }
    public int getExpectedQty() { return expectedQty; }
    public int getActualQty() { return actualQty; }
    public String getReceivedBy() { return receivedBy; }
    public String getReceivedAt() { return receivedAt; }
    public void setReceivedAt(String receivedAt) { this.receivedAt = receivedAt; }
    public List<InboundLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<InboundLineItem> lineItems) { this.lineItems = lineItems; }
    public boolean hasDiscrepancy() { return expectedQty != actualQty; }

    public static class InboundLineItem {
        private String merchandiseCode;
        private int expected;
        private int actual;

        public InboundLineItem(String merchandiseCode, int expected, int actual) {
            this.merchandiseCode = merchandiseCode;
            this.expected = expected;
            this.actual = actual;
        }

        public String getMerchandiseCode() { return merchandiseCode; }
        public int getExpected() { return expected; }
        public int getActual() { return actual; }
        public int getDifference() { return expected - actual; }
    }
}
