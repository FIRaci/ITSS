package com.system.domain.order;

import java.util.List;

public interface IPlanStrategy {
    List<Proposal> calculate(List<Site> sites, OverseasOrder order);
}
