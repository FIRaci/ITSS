package com.system.domain.request;

import java.util.List;

public interface IImportRequestRepository {
    ImportRequest findById(String requestId);
    List<ImportRequest> findAll();
    List<ImportRequest> search(String keyword);
    void save(ImportRequest request);
    void updateStatus(String requestId, String status);
    void delete(String requestId);
}
