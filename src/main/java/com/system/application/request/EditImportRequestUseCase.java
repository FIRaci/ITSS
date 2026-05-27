package com.system.application.request;

import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequestDetail;
import java.util.List;
import java.util.ArrayList;

public class EditImportRequestUseCase {
    private RequestRepositoryImpl repository;

    public EditImportRequestUseCase() {
        this.repository = new RequestRepositoryImpl();
    }

    public void execute(String reqId, List<ImportRequestDetail> oldList, List<ImportRequestDetail> newList, String reason, String user) throws Exception {
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
