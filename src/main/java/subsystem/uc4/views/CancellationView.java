package subsystem.uc4.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class CancellationView {
    private VBox root;

    public CancellationView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
