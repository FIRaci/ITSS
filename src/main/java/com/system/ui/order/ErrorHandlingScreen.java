package com.system.ui.order;

import com.system.application.order.OrderActionController;
import com.system.application.order.ResponseEntity;
import com.system.domain.order.Proposal;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ErrorHandlingScreen {
	private Stage stage;
	private OrderActionController controller;
	private TableView<Proposal> proposalTable;
	private TextField txtOrderId;

	public ErrorHandlingScreen(Stage stage, String orderId) {
		this.stage = stage;
		this.controller = new OrderActionController();
		this.txtOrderId = new TextField(orderId);
	}

	public void show() {
		VBox root = new VBox(12);
		root.setPadding(new Insets(20));

		Label title = new Label("UC4 - Xu ly loi Site");

		txtOrderId.setPromptText("Nhap ma don hang");

		proposalTable = new TableView<>();
		TableColumn<Proposal, String> c1 = new TableColumn<>("Ma de xuat");
		c1.setCellValueFactory(new PropertyValueFactory<>("proposalId"));
		TableColumn<Proposal, String> c2 = new TableColumn<>("Noi dung");
		c2.setCellValueFactory(new PropertyValueFactory<>("generatedPlanDetails"));
		TableColumn<Proposal, Double> c3 = new TableColumn<>("Chi phi");
		c3.setCellValueFactory(new PropertyValueFactory<>("estimatedCost"));
		proposalTable.getColumns().addAll(c1, c2, c3);

		Button btnPlan = new Button("Tao phuong an thay the");
		btnPlan.setOnAction(e -> clickPlanReplacementButton());

		Button btnSubmit = new Button("Gui phe duyet");
		btnSubmit.setOnAction(e -> selectProposalAndSubmit());

		HBox actions = new HBox(10, btnPlan, btnSubmit);
		root.getChildren().addAll(title, txtOrderId, proposalTable, actions);

		stage.setScene(new Scene(root, 720, 420));
		stage.setTitle("UC4 - Xu ly loi Site");
		stage.show();
	}

	public void clickPlanReplacementButton() {
		String orderId = txtOrderId.getText() != null ? txtOrderId.getText().trim() : "";
		if (orderId.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Canh bao", "Vui long nhap ma don hang.");
			return;
		}
		ResponseEntity<List<Proposal>> response = controller.generateReplacementPlan(orderId);
		if (!response.isSuccess()) {
			showAlert(Alert.AlertType.ERROR, "Loi", response.getMessage());
			return;
		}
		displayDraftPlan(response.getPayload());
	}

	public void displayDraftPlan(List<Proposal> proposals) {
		proposalTable.getItems().setAll(proposals);
	}

	public void selectProposalAndSubmit() {
		Proposal selected = proposalTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert(Alert.AlertType.WARNING, "Canh bao", "Vui long chon 1 phuong an.");
			return;
		}
		ResponseEntity<Void> response = controller.submitApproval(selected.getProposalId());
		showApprovalStatus(response.isSuccess() ? "Da gui" : "That bai");
		if (!response.isSuccess()) {
			showAlert(Alert.AlertType.ERROR, "Loi", response.getMessage());
		}
	}

	public void showApprovalStatus(String status) {
		showAlert(Alert.AlertType.INFORMATION, "Trang thai", "Trang thai phe duyet: " + status);
	}

	private void showAlert(Alert.AlertType type, String title, String msg) {
		Alert alert = new Alert(type, msg);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
