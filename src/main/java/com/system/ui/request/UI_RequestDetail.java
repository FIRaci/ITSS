package com.system.ui.request;

import com.system.application.request.ViewRequestDetailUseCase;
import com.itss.ImportRequest;
import com.itss.ImportRequestDetail;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UI_RequestDetail {
    private ViewRequestDetailUseCase useCase;

    public UI_RequestDetail() {
        this.useCase = new ViewRequestDetailUseCase();
    }

    public void show(ImportRequest importRequest) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chi tiết: " + importRequest.getId());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<ImportRequestDetail> table = new TableView<>();
        setupDetailTable(table);
        table.setItems(useCase.getRequestDetails(importRequest.getId()));

        Button btnClose = new Button("Đóng");
        btnClose.getStyleClass().add("btn-secondary");
        btnClose.setOnAction(e -> stage.close());

        layout.getChildren().addAll(new Label("Danh sách các mặt hàng:"), table, btnClose);
        Scene scene = new Scene(layout, 1200, 650);
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private void setupDetailTable(TableView<ImportRequestDetail> table) {
        TableColumn<ImportRequestDetail, String> colCode = new TableColumn<>("Mã hàng");
        colCode.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<ImportRequestDetail, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<ImportRequestDetail, String> colUnit = new TableColumn<>("Đơn vị");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<ImportRequestDetail, String> colDate = new TableColumn<>("Ngày nhận");
        colDate.setCellValueFactory(new PropertyValueFactory<>("desiredDeliveryDate"));
        TableColumn<ImportRequestDetail, String> colAction = new TableColumn<>("Thay đổi");
        colAction.setCellValueFactory(new PropertyValueFactory<>("uiAction"));
        table.getColumns().addAll(colCode, colQty, colUnit, colDate, colAction);
    }
}
