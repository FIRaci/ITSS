package subsystem.uc2.models;

import entity.uc2.EditRequestCommand;
import entity.chung.ImportRequestDetail;
import java.util.List;

public class EditRequestModel {
    private EditRequestCommand command;
    private String diffText;
    private boolean saved;

    public EditRequestCommand getCommand() { return command; }
    public void setCommand(EditRequestCommand command) { this.command = command; }
    public String getDiffText() { return diffText; }
    public void setDiffText(String diffText) { this.diffText = diffText; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
}
