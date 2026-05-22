package com.itss;

import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class ImportRequestController {
    
    private ImportRequestRepository repository;

    public ImportRequestController() {
        this.repository = new ImportRequestRepository();
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

    public void createNewRequest(String reqId, String user, List<ImportRequestDetail> detailsList) throws Exception {
        if (reqId == null || reqId.trim().isEmpty()) {
            throw new Exception("Mã ImportRequest không được để trống!");
        }
        if (detailsList == null || detailsList.isEmpty()) {
            throw new Exception("Danh sách mặt hàng trống!");
        }
        repository.insertNewRequest(reqId, user, detailsList);
    }

    public void updateRequest(String reqId, List<ImportRequestDetail> oldList, List<ImportRequestDetail> newList, String reason, String user) throws Exception {
        long remainCount = newList.stream().filter(n -> !n.getUiAction().equals("Delete")).count();
        if (remainCount == 0) {
            throw new Exception("Danh sách hàng không được để trống. Vui lòng hủy yêu cầu nếu không còn nhu cầu.");
        }

        StringBuilder diff = new StringBuilder();
        List<ImportRequestDetail> toInsert = new ArrayList<>();
        List<ImportRequestDetail> toUpdate = new ArrayList<>();
        List<ImportRequestDetail> toDelete = new ArrayList<>();

        for(ImportRequestDetail n : newList) {
            if(n.getUiAction().equals("Delete")) {
                diff.append("- Xóa: ").append(n.getMerchandiseCode()).append("\n");
                toDelete.add(n);
            } else if(n.getUiAction().equals("Edit")) {
                ImportRequestDetail old = oldList.stream().filter(o -> o.getId() == n.getId()).findFirst().orElse(null);
                if(old != null) {
                    if (old.getQuantity() != n.getQuantity() || !old.getDesiredDeliveryDate().equals(n.getDesiredDeliveryDate())) {
                         diff.append("~ Cập nhật ").append(n.getMerchandiseCode()).append(": ")
                             .append("SL(").append(old.getQuantity()).append("->").append(n.getQuantity()).append(") ")
                             .append("Ngày(").append(old.getDesiredDeliveryDate()).append("->").append(n.getDesiredDeliveryDate()).append(")\n");
                         toUpdate.add(n);
                    }
                }
            } else if(n.getUiAction().equals("Add")) {
                diff.append("+ Thêm: ").append(n.getMerchandiseCode()).append(" SL:").append(n.getQuantity()).append("\n");
                toInsert.add(n);
            }
        }

        if(diff.length() == 0) {
            throw new Exception("Không có thay đổi nào để lưu!");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new Exception("Phải nhập lý do thay đổi!");
        }

        repository.updateRequest(reqId, toInsert, toUpdate, toDelete, diff.toString(), reason, user);
    }

    public void deleteRequest(String id) throws Exception {
        repository.deleteImportRequest(id);
    }

    public String generateDiffText(List<ImportRequestDetail> oldList, List<ImportRequestDetail> newList) {
        StringBuilder diff = new StringBuilder();
        for(ImportRequestDetail n : newList) {
            if(n.getUiAction().equals("Delete")) {
                diff.append("- Xóa: ").append(n.getMerchandiseCode()).append("\n");
            } else if(n.getUiAction().equals("Edit")) {
                ImportRequestDetail old = oldList.stream().filter(o -> o.getId() == n.getId()).findFirst().orElse(null);
                if(old != null) {
                    if (old.getQuantity() != n.getQuantity() || !old.getDesiredDeliveryDate().equals(n.getDesiredDeliveryDate())) {
                         diff.append("~ Cập nhật ").append(n.getMerchandiseCode()).append(": ")
                             .append("SL(").append(old.getQuantity()).append("->").append(n.getQuantity()).append(") ")
                             .append("Ngày(").append(old.getDesiredDeliveryDate()).append("->").append(n.getDesiredDeliveryDate()).append(")\n");
                    }
                }
            } else if(n.getUiAction().equals("Add")) {
                diff.append("+ Thêm: ").append(n.getMerchandiseCode()).append(" SL:").append(n.getQuantity()).append("\n");
            }
        }
        return diff.toString();
    }
}
