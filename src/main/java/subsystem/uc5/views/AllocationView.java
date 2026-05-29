package subsystem.uc5.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class AllocationView {
    private VBox root;

    public AllocationView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
