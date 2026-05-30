package com.system.infrastructure.dtos;

import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import com.itss.InternationalOrder;
import javafx.collections.ObservableList;

public class FullDetailDTO {
    public ImportRequest requestInfo;
    public ObservableList<ImportRequestDetail> itemDetailsList;
    public ObservableList<InternationalOrder> linkedOrdersList;
    public String liveTrackingStatus;
    public java.util.Date estimatedTimeArrival;

    public FullDetailDTO() {}

    public FullDetailDTO(ImportRequest requestInfo, ObservableList<ImportRequestDetail> itemDetailsList, ObservableList<InternationalOrder> linkedOrdersList) {
        this.requestInfo = requestInfo;
        this.itemDetailsList = itemDetailsList;
        this.linkedOrdersList = linkedOrdersList;
    }
}
