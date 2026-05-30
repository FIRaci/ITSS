package subsystem.uc3.controllers;

import entity.uc3.RequestDetailViewModel;
import com.system.infrastructure.persistence.RequestRepositoryImpl;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import com.itss.ImportRequestHistory;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import java.util.List;

public class ViewDetailSubController {
    private RequestRepositoryImpl repository;

    public ViewDetailSubController() {
        this.repository = new RequestRepositoryImpl();
    }

    public RequestDetailViewModel findByKeyword(String keyword) {
        ObservableList<ImportRequest> list = repository.findAllMaster(keyword);
        if (list.isEmpty()) return null;
        ImportRequest first = list.get(0);
        entity.chung.ImportRequest req = new entity.chung.ImportRequest(
            first.getId(), first.getStatus(), first.isAccepted(), first.getCreatedBy(), first.getCreatedAt());
        return new RequestDetailViewModel(req, convertDetails(repository.findDetailsByRequestId(first.getId())), List.of());
    }

    public RequestDetailViewModel findByRequestId(String requestId) {
        return new RequestDetailViewModel(null, convertDetails(repository.findDetailsByRequestId(requestId)), List.of());
    }

    private List<entity.chung.ImportRequestDetail> convertDetails(ObservableList<ImportRequestDetail> oldList) {
        return oldList.stream().map(d -> new entity.chung.ImportRequestDetail(
            d.getId(), d.getRequestId(), d.getMerchandiseCode(), d.getQuantity(), d.getUnit(), d.getDesiredDeliveryDate()
        )).collect(Collectors.toList());
    }

    public void delete(String id) throws Exception {
        repository.deleteImportRequest(id);
    }
}
