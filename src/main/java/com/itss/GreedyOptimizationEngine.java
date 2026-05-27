package com.itss;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.system.infrastructure.persistence.RequestRepositoryImpl;

public class GreedyOptimizationEngine implements IOptimizationEngine {

    private RequestRepositoryImpl repository;
    private SiteRepository siteRepository;

    public GreedyOptimizationEngine() {
        this.repository = new RequestRepositoryImpl();
        this.siteRepository = new SiteRepository();
    }

    @Override
    public List<AllocationRow> buildAllocationPlan(String requestId) {
        List<AllocationRow> plan = new ArrayList<>();
        List<ImportRequestDetail> details = repository.findDetailsByRequestId(requestId);

        for (ImportRequestDetail detail : details) {
            String code = detail.getMerchandiseCode();
            int qty = detail.getQuantity();
            LocalDate desired = LocalDate.parse(detail.getDesiredDeliveryDate());

            List<SiteStock> stocks = siteRepository.getSiteStocks(code, desired);
            
            // Implement Buffer Stock / Hàng tồn kho an toàn
            int BUFFER_STOCK = 50;
            for (SiteStock s : stocks) {
                s.stockQty = Math.max(0, s.stockQty - BUFFER_STOCK);
            }

            int total = stocks.stream().mapToInt(s -> s.stockQty).sum();
            if (total < qty) {
                continue;
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
            }

            if (remaining > 0) {
                plan.clear();
                return plan;
            }
        }
        return plan;
    }
}
