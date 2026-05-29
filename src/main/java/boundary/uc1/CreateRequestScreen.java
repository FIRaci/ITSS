package boundary.uc1;

import controller.uc1.CreateRequestController;
import entity.uc1.CreateRequestCommand;
import entity.chung.ImportRequest;
import entity.chung.ImportRequestDetail;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class CreateRequestScreen {
    private Stage stage;
    private CreateRequestController controller;
    private TextField txtRequestId;
    private TextField txtCreatedBy;
    private VBox itemsContainer;
    private List<ImportRequestDetail> details;

    public CreateRequestScreen(Stage stage) {
        this.stage = stage;
        this.controller = new CreateRequestController();
        this.details = new ArrayList<>();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        txtRequestId = new TextField();
        txtRequestId.setPromptText("Mã yêu cầu (VD: YCNH-20250601-001)");
        txtCreatedBy = new TextField();
        txtCreatedBy.setPromptText("Người tạo");

        itemsContainer = new VBox(5);
        Button btnAddItem = new Button("+ Thêm mặt hàng");
        btnAddItem.setOnAction(e -> addItemRow());

        Button btnSave = new Button("Lưu yêu cầu");
        btnSave.setOnAction(e -> save());

        root.getChildren().addAll(
            new Label("TẠO YÊU CẦU NHẬP HÀNG"),
            new Label("Mã yêu cầu:"), txtRequestId,
            new Label("Người tạo:"), txtCreatedBy,
            new Label("Danh sách mặt hàng:"), btnAddItem, itemsContainer,
            btnSave
        );

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("UC1 - Tạo yêu cầu nhập hàng");
        stage.show();
    }

    private void addItemRow() {
        HBox row = new HBox(5);
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã hàng");
        TextField txtQty = new TextField(); txtQty.setPromptText("SL");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("ĐVT");
        DatePicker dp = new DatePicker();
        Button btnRemove = new Button("X");
        btnRemove.setOnAction(e -> itemsContainer.getChildren().remove(row));
        row.getChildren().addAll(txtCode, txtQty, txtUnit, dp, btnRemove);
        itemsContainer.getChildren().add(row);
    }

    private void save() {
        try {
            details.clear();
            for (var node : itemsContainer.getChildren()) {
                if (node instanceof HBox row) {
                    TextField code = (TextField) row.getChildren().get(0);
                    TextField qty = (TextField) row.getChildren().get(1);
                    TextField unit = (TextField) row.getChildren().get(2);
                    DatePicker dp = (DatePicker) row.getChildren().get(3);
                    if (code.getText().isBlank()) throw new Exception("Mã hàng không được để trống!");
                    if (dp.getValue() == null) throw new Exception("Vui lòng chọn ngày nhận hàng cho " + code.getText());
                    if (qty.getText().isBlank()) throw new Exception("Số lượng cho " + code.getText() + " không được để trống!");
                    int id = details.size() + 1;
                    details.add(new ImportRequestDetail(id, txtRequestId.getText(), code.getText(),
                        Integer.parseInt(qty.getText()), unit.getText(), dp.getValue().toString()));
                }
            }
            ImportRequest request = new ImportRequest(txtRequestId.getText(), "Chờ duyệt", false, txtCreatedBy.getText(), java.time.LocalDate.now().toString());
            controller.execute(new CreateRequestCommand(request, details, txtCreatedBy.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Tạo yêu cầu thành công!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }
}
