package subsystem.uc2.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class EditRequestView {
    private VBox root;

    public EditRequestView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
