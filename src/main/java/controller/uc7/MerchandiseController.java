package controller.uc7;

import subsystem.uc7.MerchandiseSubController;
import entity.chung.Merchandise;
import java.util.List;

public class MerchandiseController {
    private MerchandiseSubController subController;

    public MerchandiseController() {
        this.subController = new MerchandiseSubController();
    }

    public List<Merchandise> getAllMerchandises() throws Exception {
        return subController.getAllMerchandises();
    }

    public void addMerchandise(Merchandise m) throws Exception {
        if (m.getCode() == null || m.getCode().trim().isEmpty()) {
            throw new Exception("Mã mặt hàng không được để trống!");
        }
        if (m.getName() == null || m.getName().trim().isEmpty()) {
            throw new Exception("Tên mặt hàng không được để trống!");
        }
        
        List<Merchandise> existing = subController.getAllMerchandises();
        for (Merchandise item : existing) {
            if (item.getCode().equalsIgnoreCase(m.getCode())) {
                throw new Exception("Mã mặt hàng đã tồn tại!");
            }
        }
        subController.addMerchandise(m);
    }

    public void toggleStatus(Merchandise m) throws Exception {
        String newStatus = "Đang kinh doanh".equals(m.getStatus()) ? "Ngừng kinh doanh" : "Đang kinh doanh";
        subController.updateStatus(m.getCode(), newStatus);
        m.setStatus(newStatus);
    }
}
