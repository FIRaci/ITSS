package subsystem.uc1.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class CreateRequestView {
    private VBox root;

    public CreateRequestView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
