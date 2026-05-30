package com.system.infrastructure.adapters;

import com.system.domain.order.INotificationService;

public class NotificationAdapterImpl implements INotificationService {
    @Override
    public void sendAlert(String message) {
        System.out.println("[Notification] " + message);
    }

    @Override
    public void sendHighImpactAlert(String message) {
        System.out.println("[Notification][HIGH] " + message);
    }

    @Override
    public void sendCrossDepartmentAlert(String message) {
        System.out.println("[Notification][CROSS-DEPT] " + message);
    }

    @Override
    public void sendShortageAlert(String message) {
        System.out.println("[Notification][SHORTAGE] " + message);
    }
}
