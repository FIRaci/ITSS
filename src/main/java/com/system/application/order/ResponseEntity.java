package com.system.application.order;

public class ResponseEntity<T> {
    private boolean success;
    private String message;
    private T payload;

    public ResponseEntity(boolean success, String message, T payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public static <T> ResponseEntity<T> success(String message) {
        return new ResponseEntity<>(true, message, null);
    }

    public static <T> ResponseEntity<T> success(String message, T payload) {
        return new ResponseEntity<>(true, message, payload);
    }

    public static <T> ResponseEntity<T> failure(String message) {
        return new ResponseEntity<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getPayload() { return payload; }
}
