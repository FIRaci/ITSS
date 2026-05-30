package com.system.domain.order;

public interface IOverseasOrderRepository {
    OverseasOrder findById(String id);
    void save(OverseasOrder order);
}
