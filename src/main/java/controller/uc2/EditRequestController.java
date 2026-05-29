package controller.uc2;

import entity.uc2.EditRequestCommand;
import entity.uc2.EditRequestItem;
import subsystem.uc2.models.EditRequestModel;
import subsystem.uc2.controllers.EditRequestSubController;

public class EditRequestController {
    private EditRequestSubController subController;
    private EditRequestModel model;

    public EditRequestController() {
        this.subController = new EditRequestSubController();
        this.model = new EditRequestModel();
    }

    public void execute(EditRequestCommand command) throws Exception {
        long remainCount = command.getNewDetails().stream()
            .filter(n -> !"Delete".equals(n.getUiAction())).count();
        if (remainCount == 0) {
            throw new Exception("Danh sách hàng không được để trống. Vui lòng hủy yêu cầu nếu không còn nhu cầu.");
        }
        if (command.getReason() == null || command.getReason().trim().isEmpty()) {
            throw new Exception("Phải nhập lý do thay đổi!");
        }
        StringBuilder diff = new StringBuilder();
        for (EditRequestItem n : command.getNewDetails()) {
            if ("Delete".equals(n.getUiAction())) {
                diff.append("- Xóa: ").append(n.getMerchandiseCode()).append("\n");
            } else if ("Edit".equals(n.getUiAction())) {
                EditRequestItem old = command.getOldDetails().stream()
                    .filter(o -> o.getId() == n.getId()).findFirst().orElse(null);
                if (old != null && (old.getQuantity() != n.getQuantity() || !old.getDesiredDeliveryDate().equals(n.getDesiredDeliveryDate()))) {
                    diff.append("~ Cập nhật ").append(n.getMerchandiseCode()).append(": ")
                        .append("SL(").append(old.getQuantity()).append("->").append(n.getQuantity()).append(") ")
                        .append("Ngày(").append(old.getDesiredDeliveryDate()).append("->").append(n.getDesiredDeliveryDate()).append(")\n");
                }
            } else if ("Add".equals(n.getUiAction())) {
                diff.append("+ Thêm: ").append(n.getMerchandiseCode()).append(" SL:").append(n.getQuantity()).append("\n");
            }
        }
        if (diff.length() == 0) {
            throw new Exception("Không có thay đổi nào để lưu!");
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        for (EditRequestItem detail : command.getNewDetails()) {
            if (!"Delete".equals(detail.getUiAction())) {
                if (detail.getQuantity() <= 0) {
                    throw new Exception("Lỗi nghiệp vụ: Số lượng của " + detail.getMerchandiseCode() + " phải lớn hơn 0.");
                }
                if (!java.time.LocalDate.parse(detail.getDesiredDeliveryDate()).isAfter(today)) {
                    throw new Exception("Lỗi nghiệp vụ: Ngày nhận của " + detail.getMerchandiseCode() + " phải nằm ở tương lai.");
                }
            }
        }
        model.setCommand(command);
        model.setDiffText(diff.toString());
        subController.save(model);
    }

    public EditRequestModel getModel() { return model; }
}
