package subsystem.uc1.models;

import entity.uc1.CreateRequestCommand;
import entity.chung.ImportRequest;
import entity.chung.ImportRequestDetail;
import java.util.List;

public class CreateRequestModel {
    private CreateRequestCommand command;
    private boolean saved;

    public CreateRequestCommand getCommand() { return command; }
    public void setCommand(CreateRequestCommand command) { this.command = command; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
}
