package entity.uc1;

import entity.chung.ImportRequestDetail;

public class CreateRequestItem extends ImportRequestDetail {
    private String merchandiseName;
    private boolean isValid;

    public CreateRequestItem(int id, String requestId, String merchandiseCode, int quantity, String unit, String desiredDeliveryDate, String merchandiseName) {
        super(id, requestId, merchandiseCode, quantity, unit, desiredDeliveryDate);
        this.merchandiseName = merchandiseName;
        this.isValid = true;
    }

    public String getMerchandiseName() { return merchandiseName; }
    public void setMerchandiseName(String name) { this.merchandiseName = name; }
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
}
