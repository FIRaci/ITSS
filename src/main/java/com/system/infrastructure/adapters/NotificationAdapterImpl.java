package com.system.infrastructure.adapters;

import com.system.domain.order.INotificationService;

public class NotificationAdapterImpl implements INotificationService {
    @Override
    public void sendCrossDepartmentAlert(String message) {
        System.out.println("[Notification] " + message);
    }
}
