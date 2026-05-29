package boundary.uc2;

import controller.uc2.EditRequestController;
import entity.uc2.EditRequestCommand;
import entity.uc2.EditRequestItem;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class EditRequestScreen {
    private Stage stage;
    private EditRequestController controller;
    private TextField txtRequestId;
    private TextArea txtReason;
    private VBox itemsContainer;
    private List<EditRequestItem> oldDetails;
    private List<EditRequestItem> newDetails;

    public EditRequestScreen(Stage stage) {
        this.stage = stage;
        this.controller = new EditRequestController();
        this.oldDetails = new ArrayList<>();
        this.newDetails = new ArrayList<>();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        txtRequestId = new TextField();
        txtRequestId.setPromptText("Mã yêu cầu cần sửa");

        Button btnLoad = new Button("Tải danh sách hiện tại");
        btnLoad.setOnAction(e -> loadCurrentItems());

        itemsContainer = new VBox(5);
        Button btnAddItem = new Button("+ Thêm mặt hàng mới");
        btnAddItem.setOnAction(e -> addNewItem());

        txtReason = new TextArea();
        txtReason.setPromptText("Lý do thay đổi");
        txtReason.setPrefRowCount(3);

        Button btnSave = new Button("Lưu thay đổi");
        btnSave.setOnAction(e -> save());

        root.getChildren().addAll(
            new Label("SỬA YÊU CẦU NHẬP HÀNG"),
            new Label("Mã yêu cầu:"), txtRequestId, btnLoad,
            new Label("Danh sách mặt hàng (bỏ qua = giữ nguyên, X = xóa):"), btnAddItem, itemsContainer,
            new Label("Lý do thay đổi:"), txtReason,
            btnSave
        );

        stage.setScene(new Scene(root, 650, 600));
        stage.setTitle("UC2 - Sửa yêu cầu nhập hàng");
        stage.show();
    }

    private void loadCurrentItems() {
        itemsContainer.getChildren().clear();
        oldDetails.clear();
        newDetails.clear();
    }

    private void addNewItem() {
        HBox row = new HBox(5);
        TextField txtCode = new TextField(); txtCode.setPromptText("Mã hàng");
        TextField txtQty = new TextField(); txtQty.setPromptText("SL");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("ĐVT");
        DatePicker dp = new DatePicker();
        Button btnRemove = new Button("Xóa");
        btnRemove.setOnAction(e -> {
            row.setStyle("-fx-background-color: #ffcccc;");
            row.setDisable(true);
        });
        row.getChildren().addAll(txtCode, txtQty, txtUnit, dp, btnRemove);
        itemsContainer.getChildren().add(row);
    }

    private void save() {
        try {
            newDetails.clear();
            for (var node : itemsContainer.getChildren()) {
                if (node instanceof HBox row) {
                    TextField code = (TextField) row.getChildren().get(0);
                    TextField qty = (TextField) row.getChildren().get(1);
                    TextField unit = (TextField) row.getChildren().get(2);
                    DatePicker dp = (DatePicker) row.getChildren().get(3);
                    if (code.getText().isBlank()) throw new Exception("Mã hàng không được để trống!");
                    if (dp.getValue() == null && !row.isDisable()) throw new Exception("Vui lòng chọn ngày nhận cho " + code.getText());
                    if (qty.getText().isBlank() && !row.isDisable()) throw new Exception("Số lượng cho " + code.getText() + " không được để trống!");
                    String dateStr = dp.getValue() != null ? dp.getValue().toString() : "";
                    EditRequestItem item = new EditRequestItem(0, txtRequestId.getText(), code.getText(),
                        qty.getText().isBlank() ? 0 : Integer.parseInt(qty.getText()), unit.getText(), dateStr);
                    item.setUiAction(row.isDisable() ? "Delete" : "Add");
                    newDetails.add(item);
                }
            }
            controller.execute(new EditRequestCommand(txtRequestId.getText(), oldDetails, newDetails, txtReason.getText(), "currentUser"));
            new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).showAndWait();
        }
    }
}
