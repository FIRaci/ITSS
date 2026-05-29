package subsystem.uc6.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class DiscrepancyView {
    private VBox root;

    public DiscrepancyView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
