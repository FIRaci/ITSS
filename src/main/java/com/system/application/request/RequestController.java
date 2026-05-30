package com.system.application.request;

import com.system.infrastructure.dtos.RequestDTO;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequestDetail;
import java.util.List;

public class RequestController {
    private CreateImportRequestUseCase createService;
    private EditImportRequestUseCase editService;

    public RequestController() {
        this.createService = new CreateImportRequestUseCase();
        this.editService = new EditImportRequestUseCase();
    }

    public void createRequest(String reqId, String user, List<ImportRequestDetail> detailsList) throws Exception {
        createService.execute(reqId, user, detailsList);
    }

    public void updateRequest(String reqId, List<ImportRequestDetail> oldList, List<ImportRequestDetail> newList, String reason, String user) throws Exception {
        editService.execute(reqId, oldList, newList, reason, user);
    }

    public void discardCache(String id) {
        RequestRepositoryImpl repo = new RequestRepositoryImpl();
        repo.clearDraft(id);
    }

    public String generateDiffText(List<ImportRequestDetail> oldList, List<ImportRequestDetail> newList) {
        return editService.generateDiffText(oldList, newList);
    }
}
