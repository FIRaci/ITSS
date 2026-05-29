package com.system.application.overseas;

import com.itss.AllocationRow;
import com.itss.ImportRequestDetail;
import com.itss.SiteStock;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.system.infrastructure.persistence.SiteRepositoryImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyOptimizationEngine implements IOptimizationEngine {

    private RequestRepositoryImpl repository;
    private SiteRepositoryImpl siteRepository;

    public GreedyOptimizationEngine() {
        this.repository = new RequestRepositoryImpl();
        this.siteRepository = new SiteRepositoryImpl();
    }

    @Override
    public List<AllocationRow> buildAllocationPlan(String requestId) {
        List<AllocationRow> plan = new ArrayList<>();
        List<ImportRequestDetail> details = repository.findDetailsByRequestId(requestId);
        java.util.Map<String, Integer> allocatedLedger = new java.util.HashMap<>();

        for (ImportRequestDetail detail : details) {
            String code = detail.getMerchandiseCode();
            int qty = detail.getQuantity();
            LocalDate desired = LocalDate.parse(detail.getDesiredDeliveryDate());

            List<SiteStock> stocks = siteRepository.getSiteStocks(code, desired);
            
            // Implement Buffer Stock / Hàng tồn kho an toàn
            int BUFFER_STOCK = 50;
            for (SiteStock s : stocks) {
                int previouslyAllocated = allocatedLedger.getOrDefault(s.siteCode + "_" + code, 0);
                s.stockQty = Math.max(0, s.stockQty - BUFFER_STOCK - previouslyAllocated);
            }

            int total = stocks.stream().mapToInt(s -> s.stockQty).sum();
            if (total < qty) {
                plan.clear();
                return plan;
            }

            boolean hasFeasible = stocks.stream().anyMatch(s -> s.prefRank != 2);
            if (!hasFeasible) {
                plan.clear();
                return plan;
            }

            stocks.sort(Comparator.comparing((SiteStock s) -> s.prefRank).thenComparing(s -> -s.stockQty));
            int remaining = qty;
            for (SiteStock s : stocks) {
                if (remaining <= 0) break;
                if (s.prefRank == 2) continue;

                int useQty = Math.min(remaining, s.stockQty);
                plan.add(new AllocationRow(code, s.siteCode, useQty, s.shippingMethod));
                remaining -= useQty;
                
                String ledgerKey = s.siteCode + "_" + code;
                allocatedLedger.put(ledgerKey, allocatedLedger.getOrDefault(ledgerKey, 0) + useQty);
            }

            if (remaining > 0) {
                plan.clear();
                return plan;
            }
        }
        return plan;
    }
}
