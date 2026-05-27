package com.system.application.order;

public class CancelAndErrorHandlingUseCase {
    public void submitCancelRequest(String orderId) { }
    public void requestErrorProcessing(String orderId) { }
    public void processRejection(String orderId, String reason) { }
}
