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
        
        java.time.LocalDate today = java.time.LocalDate.now();
        for (ImportRequestDetail detail : detailsList) {
            if (detail.getQuantity() <= 0) {
                throw new Exception("Lỗi bảo mật/nghiệp vụ: Số lượng của " + detail.getMerchandiseCode() + " phải lớn hơn 0.");
            }
            if (!java.time.LocalDate.parse(detail.getDesiredDeliveryDate()).isAfter(today)) {
                throw new Exception("Lỗi bảo mật/nghiệp vụ: Ngày nhận của " + detail.getMerchandiseCode() + " phải nằm ở tương lai.");
            }
        }
        
        repository.insertNewRequest(reqId, user, detailsList);
    }
}
