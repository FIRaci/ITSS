package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SiteInventoryController {
    private SiteInventoryRepository repo;

    public SiteInventoryController() {
        this.repo = new SiteInventoryRepository();
    }

    public ObservableList<SiteInventoryItem> getInventory(String siteCode) {
        if(siteCode == null) return FXCollections.observableArrayList();
        return FXCollections.observableArrayList(repo.getInventoryBySite(siteCode));
    }

    public boolean addInventory(String siteCode, String merchandiseCode, int qty) {
        if(siteCode == null || merchandiseCode == null || merchandiseCode.isEmpty()) return false;
        return repo.insertInventory(new SiteInventoryItem(siteCode, merchandiseCode, qty));
    }

    public boolean updateInventory(int id, int qty) {
        return repo.updateInventoryQty(id, qty);
    }

    public boolean deleteInventory(int id) {
        return repo.deleteInventory(id);
    }
}
