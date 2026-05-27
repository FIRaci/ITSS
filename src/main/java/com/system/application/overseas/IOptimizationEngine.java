package com.system.application.overseas;

import com.itss.AllocationRow;
import java.util.List;

public interface IOptimizationEngine {
    List<AllocationRow> buildAllocationPlan(String requestId);
}
