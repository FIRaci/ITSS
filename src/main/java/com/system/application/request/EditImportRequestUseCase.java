package com.system.application.request;

import com.system.infrastructure.dtos.RequestDTO;
import com.system.domain.request.ImportRequest;
public class EditImportRequestUseCase {
    public void processRequestModification(String requestId, RequestDTO newData) { }
    public void calculateDiffTable(ImportRequest oldData, RequestDTO newData) { }
    public void discardDraftCache(String requestId) { }
}
