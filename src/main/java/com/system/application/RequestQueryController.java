package com.system.application.request;

import com.system.infrastructure.dtos.FullDetailDTO;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import com.itss.ImportRequestHistory;
import com.itss.InternationalOrder;
import javafx.collections.ObservableList;

public class RequestQueryController {
    private ViewRequestDetailUseCase viewService;

    public RequestQueryController() {
        this.viewService = new ViewRequestDetailUseCase();
    }

    public FullDetailDTO getFullDetails(String id) {
        RequestRepositoryImpl repo = new RequestRepositoryImpl();
        ImportRequest request = repo.findMasterById(id);
        ObservableList<ImportRequestDetail> details = viewService.getRequestDetails(id);
        ObservableList<InternationalOrder> orders = repo.findOrdersByRequestId(id);
        return new FullDetailDTO(request, details, orders);
    }

    public ObservableList<ImportRequest> getAllRequests(String keyword) {
        return viewService.getAllRequests(keyword);
    }

    public ObservableList<ImportRequestDetail> getRequestDetails(String requestId) {
        return viewService.getRequestDetails(requestId);
    }

    public ObservableList<ImportRequestHistory> getAllHistory() {
        return viewService.getAllHistory();
    }

    public void deleteRequest(String id) throws Exception {
        viewService.deleteRequest(id);
    }
}
