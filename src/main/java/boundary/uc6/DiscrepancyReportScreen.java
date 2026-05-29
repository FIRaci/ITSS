package boundary.uc6;

import controller.uc6.DiscrepancyController;
import entity.uc6.DiscrepancyRecord;
import entity.chung.InternationalOrder;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class DiscrepancyReportScreen {
    private Stage stage;
    private DiscrepancyController controller;
    private TableView<InternationalOrder> orderTable;
    private TextArea txtEvidence;
    private ComboBox<String> cbReason;
    private TextField txtQty;
    private TextArea txtNote;

    public DiscrepancyReportScreen(Stage stage) {
        this.stage = stage;
        this.controller = new DiscrepancyController();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        orderTable = new TableView<>();
        TableColumn<InternationalOrder, Integer> colId = new TableColumn<>("Mã đơn");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<InternationalOrder, String> colMc = new TableColumn<>("Mặt hàng");
        colMc.setCellValueFactory(new PropertyValueFactory<>("merchandiseCode"));
        TableColumn<InternationalOrder, Integer> colQty = new TableColumn<>("SL");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        orderTable.getColumns().addAll(colId, colMc, colQty);

        cbReason = new ComboBox<>();
        cbReason.getItems().addAll("Thiếu hàng", "Thừa hàng", "Hàng hỏng/vỡ", "Sai chủng loại");
        txtQty = new TextField(); txtQty.setPromptText("SL sai lệch");
        txtEvidence = new TextArea(); txtEvidence.setPromptText("Link ảnh minh chứng (nếu có)");
        txtEvidence.setPrefRowCount(2);
        txtNote = new TextArea(); txtNote.setPromptText("Ghi chú thêm");
        txtNote.setPrefRowCount(2);

        Button btnReport = new Button("Lập biên bản");
        btnReport.setOnAction(e -> report());

        root.getChildren().addAll(
            new Label("BÁO CÁO SAI LỆCH"),
            new Label("Chọn đơn hàng:"), orderTable,
            new Label("Lý do:"), cbReason,
            new Label("SL sai lệch:"), txtQty,
            new Label("Minh chứng:"), txtEvidence,
            new Label("Ghi chú:"), txtNote,
            btnReport
        );

        stage.setScene(new Scene(root, 500, 550));
        stage.setTitle("UC6 - Báo cáo sai lệch");
        stage.show();
    }

    private void report() {
        InternationalOrder sel = orderTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            DiscrepancyRecord record = new DiscrepancyRecord(
                sel.getId(), sel.getMerchandiseCode(),
                cbReason.getValue(), Integer.parseInt(txtQty.getText()),
                txtEvidence.getText(), txtNote.getText(), "warehouse1");
            controller.report(record, sel);
            new Alert(Alert.AlertType.INFORMATION, "Biên bản đã được lưu!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }
}
