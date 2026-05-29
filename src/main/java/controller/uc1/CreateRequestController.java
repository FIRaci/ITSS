package controller.uc1;

import entity.uc1.CreateRequestCommand;
import subsystem.uc1.models.CreateRequestModel;
import subsystem.uc1.controllers.CreateRequestSubController;

public class CreateRequestController {
    private CreateRequestSubController subController;
    private CreateRequestModel model;

    public CreateRequestController() {
        this.subController = new CreateRequestSubController();
        this.model = new CreateRequestModel();
    }

    public void execute(CreateRequestCommand command) throws Exception {
        if (command.getRequest().getId() == null || command.getRequest().getId().trim().isEmpty()) {
            throw new Exception("Mã ImportRequest không được để trống!");
        }
        if (command.getDetails() == null || command.getDetails().isEmpty()) {
            throw new Exception("Phải có ít nhất 1 mặt hàng trong yêu cầu!");
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        for (entity.chung.ImportRequestDetail detail : command.getDetails()) {
            if (detail.getQuantity() <= 0) {
                throw new Exception("Số lượng của '" + detail.getMerchandiseCode() + "' phải lớn hơn 0.");
            }
            if (!java.time.LocalDate.parse(detail.getDesiredDeliveryDate()).isAfter(today)) {
                throw new Exception("Ngày nhận của '" + detail.getMerchandiseCode() + "' phải là ngày trong tương lai.");
            }
        }
        model.setCommand(command);
        subController.save(model);
    }

    public CreateRequestModel getModel() { return model; }
}
