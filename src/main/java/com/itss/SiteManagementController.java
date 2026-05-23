package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SiteManagementController {
    private SiteRepository siteRepository;

    public SiteManagementController() {
        this.siteRepository = new SiteRepository();
    }

    public ObservableList<Site> getAllSites() {
        return FXCollections.observableArrayList(siteRepository.findAllSites());
    }

    public boolean addSite(String siteCode, String name, int daysShip, int daysAir, String otherInfo) {
        if (siteCode == null || siteCode.isEmpty() || name == null || name.isEmpty()) return false;
        Site s = new Site(siteCode, name, daysShip, daysAir, otherInfo);
        return siteRepository.insertSite(s);
    }

    public boolean updateSite(String siteCode, String name, int daysShip, int daysAir, String otherInfo) {
        if (siteCode == null || siteCode.isEmpty()) return false;
        Site s = new Site(siteCode, name, daysShip, daysAir, otherInfo);
        return siteRepository.updateSite(s);
    }

    public boolean deleteSite(String siteCode) {
        if (siteCode == null || siteCode.isEmpty()) return false;
        return siteRepository.deleteSite(siteCode);
    }
}
