package controller.uc8;

import subsystem.uc8.SiteSubController;
import entity.chung.Site;
import java.util.List;

public class SiteController {
    private SiteSubController subController;

    public SiteController() {
        this.subController = new SiteSubController();
    }

    public List<Site> getAllSites() throws Exception {
        return subController.getAllSites();
    }

    public void updateSiteTransport(Site s) throws Exception {
        if (s.getDaysShip() < 0 || s.getDaysAir() < 0) {
            throw new Exception("Số ngày vận chuyển không được là số âm!");
        }
        subController.updateSiteTransport(s.getId(), s.getDaysShip(), s.getDaysAir(), s.getOtherInfo());
    }
}
