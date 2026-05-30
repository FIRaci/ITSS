package com.system.infrastructure.dtos;

import java.util.List;

public class RequestDTO {
    public String requestId;
    public String merchandiseCode;
    public int quantity;
    public String unit;
    public String expectedDate;
    public String editReason;

    public RequestDTO() {}

    public RequestDTO(String requestId) { this.requestId = requestId; }
}
