package com.system.application.request;

import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import com.itss.ImportRequestHistory;
import javafx.collections.ObservableList;

public class ViewRequestDetailUseCase {
    private RequestRepositoryImpl repository;

    public ViewRequestDetailUseCase() {
        this.repository = new RequestRepositoryImpl();
    }

    public ObservableList<ImportRequest> getAllRequests(String keyword) {
        return repository.findAllMaster(keyword);
    }

    public ObservableList<ImportRequestDetail> getRequestDetails(String requestId) {
        return repository.findDetailsByRequestId(requestId);
    }

    public ObservableList<ImportRequestHistory> getAllHistory() {
        return repository.findAllHistory();
    }
    
    public void deleteRequest(String id) throws Exception {
        repository.deleteImportRequest(id);
    }
}
