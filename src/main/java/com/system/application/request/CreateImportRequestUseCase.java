package com.system.application.request;

import com.system.infrastructure.dtos.RequestDTO;
public class CreateImportRequestUseCase {
    public boolean validateMerchandise(String code) { return true; }
    public void processTemporaryAddition(String code, int qty) { }
    public void submitNewRequest(RequestDTO requestData) { }
}
