package javafx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CalculatorView {
    private TextField firstNumber, secondNumber, calcSolution;

    private Button addButton, subButton, mulButton, divButton;
    private BorderPane root;

    public CalculatorView() {
        firstNumber = new TextField();
        secondNumber = new TextField();
        calcSolution = new TextField();
        calcSolution.setEditable(false);

        addButton = new Button("+");
        subButton = new Button("-");
        mulButton = new Button("*");
        divButton = new Button("/");

        root = new BorderPane();
        root.setPadding(new Insets(12));

        GridPane infoPanel = new GridPane();
        infoPanel.setHgap(10);
        infoPanel.setVgap(10);

        infoPanel.add(new Label("First Number:"), 0, 0);
        infoPanel.add(firstNumber, 1, 0);
        infoPanel.add(new Label("Second Number:"), 0, 1);
        infoPanel.add(secondNumber, 1, 1);
        infoPanel.add(new Label("Result:"), 0, 2);
        infoPanel.add(calcSolution, 1, 2);

        HBox operandPanel = new HBox(10);
        operandPanel.getChildren().addAll(addButton, subButton, mulButton, divButton);
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(subButton, Priority.ALWAYS);
        HBox.setHgrow(mulButton, Priority.ALWAYS);
        HBox.setHgrow(divButton, Priority.ALWAYS);
        addButton.setMaxWidth(Double.MAX_VALUE);
        subButton.setMaxWidth(Double.MAX_VALUE);
        mulButton.setMaxWidth(Double.MAX_VALUE);
        divButton.setMaxWidth(Double.MAX_VALUE);

        root.setCenter(infoPanel);
        root.setBottom(operandPanel);
    }

    public BorderPane getView() {
        return root;
    }

    public void addButtonListener(Runnable handlerAdd, Runnable handlerSub, Runnable handlerMul, Runnable handlerDiv) {
        addButton.setOnAction(e -> handlerAdd.run());
        subButton.setOnAction(e -> handlerSub.run());
        mulButton.setOnAction(e -> handlerMul.run());
        divButton.setOnAction(e -> handlerDiv.run());
    }

    public String getFirstNumber() {
        return firstNumber.getText();
    }

    public String getSecondNumber() {
        return secondNumber.getText();
    }

    public void setCalcSolution(String solution) {
        calcSolution.setText(solution);
    }
}
