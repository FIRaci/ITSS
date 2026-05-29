package subsystem.uc2.controllers;

import subsystem.uc2.models.EditRequestModel;
import entity.uc2.EditRequestItem;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequestDetail;
import java.util.List;
import java.util.ArrayList;

public class EditRequestSubController {
    private RequestRepositoryImpl repository;

    public EditRequestSubController() {
        this.repository = new RequestRepositoryImpl();
    }

    public void save(EditRequestModel model) throws Exception {
        var cmd = model.getCommand();
        List<ImportRequestDetail> toInsert = new ArrayList<>();
        List<ImportRequestDetail> toUpdate = new ArrayList<>();
        List<ImportRequestDetail> toDelete = new ArrayList<>();

        for (EditRequestItem item : cmd.getNewDetails()) {
            ImportRequestDetail old = new ImportRequestDetail(item.getId(), item.getRequestId(),
                item.getMerchandiseCode(), item.getQuantity(), item.getUnit(), item.getDesiredDeliveryDate());
            old.setUiAction(item.getUiAction());
            if ("Delete".equals(item.getUiAction())) toDelete.add(old);
            else if ("Edit".equals(item.getUiAction())) toUpdate.add(old);
            else if ("Add".equals(item.getUiAction())) toInsert.add(old);
        }

        repository.updateRequest(cmd.getRequestId(), toInsert, toUpdate, toDelete, model.getDiffText(), cmd.getReason(), cmd.getChangedBy());
        model.setSaved(true);
    }
}
