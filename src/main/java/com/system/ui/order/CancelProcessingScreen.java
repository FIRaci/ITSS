package com.system.ui.order;

import com.system.application.order.OrderActionController;
import com.system.application.order.ResponseEntity;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CancelProcessingScreen {
	private Stage stage;
	private OrderActionController controller;
	private TextField txtOrderId;
	private Button btnPlanReplacement;
	private String lastOrderId;

	public CancelProcessingScreen(Stage stage) {
		this.stage = stage;
		this.controller = new OrderActionController();
	}

	public void show() {
		VBox root = new VBox(12);
		root.setPadding(new Insets(20));

		Label title = new Label("UC4 - Xu ly huy don");
		txtOrderId = new TextField();
		txtOrderId.setPromptText("Nhap ma don hang");

		Button btnCancel = new Button("Huy don hang");
		btnCancel.setOnAction(e -> clickCancelOrderButton());

		btnPlanReplacement = new Button("Lap phuong an thay the");
		btnPlanReplacement.setDisable(true);
		btnPlanReplacement.setOnAction(e -> openErrorHandlingScreen());

		HBox actions = new HBox(10, btnCancel, btnPlanReplacement);
		root.getChildren().addAll(title, txtOrderId, actions);

		stage.setScene(new Scene(root, 520, 220));
		stage.setTitle("UC4 - Xu ly huy don");
		stage.show();
	}

	public void clickCancelOrderButton() {
		String orderId = txtOrderId.getText() != null ? txtOrderId.getText().trim() : "";
		if (orderId.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Canh bao", "Vui long nhap ma don hang.");
			return;
		}
		lastOrderId = orderId;
		ResponseEntity<Void> response = controller.cancelOrder(orderId);
		if (response.isSuccess()) {
			showCancellationSuccess();
			btnPlanReplacement.setDisable(true);
		} else if (isOnBoardMessage(response.getMessage())) {
			displayOnBoardBlockError();
		} else {
			showAlert(Alert.AlertType.ERROR, "Loi", response.getMessage());
		}
	}

	public void showCancellationSuccess() {
		showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da huy don hang.");
	}

	public void displayOnBoardBlockError() {
		showAlert(Alert.AlertType.WARNING, "Khong the huy", "Don hang dang giao, can lap phuong an thay the.");
		btnPlanReplacement.setDisable(false);
	}

	private void openErrorHandlingScreen() {
		if (lastOrderId == null || lastOrderId.isEmpty()) {
			return;
		}
		ErrorHandlingScreen screen = new ErrorHandlingScreen(new Stage(), lastOrderId);
		screen.show();
	}

	private boolean isOnBoardMessage(String message) {
		if (message == null) {
			return false;
		}
		String normalized = message.toLowerCase();
		return normalized.contains("on-board") || normalized.contains("dang giao") || normalized.contains("đang giao");
	}

	private void showAlert(Alert.AlertType type, String title, String msg) {
		Alert alert = new Alert(type, msg);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
