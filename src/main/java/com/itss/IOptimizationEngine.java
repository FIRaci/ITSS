package com.itss;

import java.time.LocalDate;
import java.util.List;

public interface IOptimizationEngine {
    List<AllocationRow> buildAllocationPlan(String requestId);
}
