package controller.uc3;

import entity.uc3.RequestDetailViewModel;
import subsystem.uc3.models.RequestDetailModel;
import subsystem.uc3.controllers.ViewDetailSubController;

public class ViewRequestDetailController {
    private ViewDetailSubController subController;
    private RequestDetailModel model;

    public ViewRequestDetailController() {
        this.subController = new ViewDetailSubController();
        this.model = new RequestDetailModel();
    }

    public RequestDetailViewModel search(String keyword) {
        return subController.findByKeyword(keyword);
    }

    public RequestDetailViewModel getDetail(String requestId) {
        return subController.findByRequestId(requestId);
    }

    public void deleteRequest(String id) throws Exception {
        subController.delete(id);
    }

    public RequestDetailModel getModel() { return model; }
}
