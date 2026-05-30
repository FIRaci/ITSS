package subsystem.uc1.controllers;

import subsystem.uc1.models.CreateRequestModel;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequestDetail;
import java.util.ArrayList;
import java.util.List;

public class CreateRequestSubController {
    private RequestRepositoryImpl repository;

    public CreateRequestSubController() {
        this.repository = new RequestRepositoryImpl();
    }

    public void save(CreateRequestModel model) throws Exception {
        var cmd = model.getCommand();
        List<ImportRequestDetail> oldDetails = new ArrayList<>();
        for (var d : cmd.getDetails()) {
            oldDetails.add(new ImportRequestDetail(d.getId(), d.getRequestId(),
                d.getMerchandiseCode(), d.getQuantity(), d.getUnit(), d.getDesiredDeliveryDate()));
        }
        repository.insertNewRequest(cmd.getRequest().getId(), cmd.getCreatedBy(), oldDetails);
        model.setSaved(true);
    }
}
