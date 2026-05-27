package com.system.application.request;

import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequestDetail;
import java.util.List;

public class CreateImportRequestUseCase {
    private RequestRepositoryImpl repository;

    public CreateImportRequestUseCase() {
        this.repository = new RequestRepositoryImpl();
    }

    public void execute(String reqId, String user, List<ImportRequestDetail> detailsList) throws Exception {
        if (reqId == null || reqId.trim().isEmpty()) {
            throw new Exception("Mã ImportRequest không được để trống!");
        }
        if (detailsList == null || detailsList.isEmpty()) {
            throw new Exception("Danh sách mặt hàng trống!");
        }
        
        repository.insertNewRequest(reqId, user, detailsList);
    }
}
