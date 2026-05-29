package entity.uc2;

import entity.chung.ImportRequestDetail;

public class EditRequestItem extends ImportRequestDetail {
    public static final String ACTION_NONE = "None";
    public static final String ACTION_ADD = "Add";
    public static final String ACTION_EDIT = "Edit";
    public static final String ACTION_DELETE = "Delete";

    private String uiAction;
    private String merchandiseName;

    public EditRequestItem(int id, String requestId, String merchandiseCode, int quantity, String unit, String desiredDeliveryDate) {
        super(id, requestId, merchandiseCode, quantity, unit, desiredDeliveryDate);
        this.uiAction = ACTION_NONE;
    }

    public String getUiAction() { return uiAction; }
    public void setUiAction(String uiAction) { this.uiAction = uiAction; }
    public String getMerchandiseName() { return merchandiseName; }
    public void setMerchandiseName(String name) { this.merchandiseName = name; }
}
