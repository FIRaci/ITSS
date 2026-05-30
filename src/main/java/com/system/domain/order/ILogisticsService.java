package com.system.domain.order;

import java.util.Date;

public interface ILogisticsService {
    boolean checkOnBoardStatus(String orderId);
    String getLiveStatus(String orderId);
    Date calculateETA(String status);
    Object getLiveTracking(String orderId);
}
