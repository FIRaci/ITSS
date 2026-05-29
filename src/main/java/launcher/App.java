package launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("RETAILAPP — HỆ THỐNG QUẢN LÝ NHẬP HÀNG");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnUC1 = new Button("UC1 - Tạo yêu cầu (Đức Anh)");
        btnUC1.setOnAction(e -> new boundary.uc1.CreateRequestScreen(new Stage()).show());

        Button btnUC2 = new Button("UC2 - Sửa yêu cầu (Quốc Thành)");
        btnUC2.setOnAction(e -> new boundary.uc2.EditRequestScreen(new Stage()).show());

        Button btnUC3 = new Button("UC3 - Xem chi tiết (Phan Huy Hoàng)");
        btnUC3.setOnAction(e -> new boundary.uc3.RequestDetailScreen(new Stage()).show());

        Button btnUC4 = new Button("UC4 - Xử lý hủy (Thanh Liêm)");
        btnUC4.setOnAction(e -> new boundary.uc4.CancellationScreen(new Stage()).show());

        Button btnUC5 = new Button("UC5 - Phân bổ & xử lý (Thế Đạt)");
        btnUC5.setOnAction(e -> new boundary.uc5.OverseasDashboard(new Stage()).show());

        Button btnUC6 = new Button("UC6 - Nhập kho & sai lệch (Đức Trung)");
        btnUC6.setOnAction(e -> new boundary.uc6.WarehouseDashboard(new Stage()).show());

        root.getChildren().addAll(title, new Separator(), btnUC1, btnUC2, btnUC3, btnUC4, btnUC5, btnUC6);
        primaryStage.setScene(new Scene(root, 450, 400));
        primaryStage.setTitle("RetailApp");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
