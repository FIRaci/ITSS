package com.system.domain.order;

public interface INotificationService {
    void sendAlert(String message);
    void sendHighImpactAlert(String message);
    void sendCrossDepartmentAlert(String message);
    void sendShortageAlert(String message);
}
