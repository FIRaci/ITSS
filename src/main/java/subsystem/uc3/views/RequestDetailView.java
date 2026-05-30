package subsystem.uc3.views;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class RequestDetailView {
    private VBox root;

    public RequestDetailView() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() { return root; }
}
