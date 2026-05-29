package entity.uc5;

import entity.chung.AllocationRow;
import java.util.List;

public class AllocationPlan {
    private String requestId;
    private List<AllocationRow> rows;

    public AllocationPlan(String requestId, List<AllocationRow> rows) {
        this.requestId = requestId;
        this.rows = rows;
    }

    public String getRequestId() { return requestId; }
    public List<AllocationRow> getRows() { return rows; }
    public boolean isEmpty() { return rows == null || rows.isEmpty(); }
}
