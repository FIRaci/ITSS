package com.system.domain.order;

import java.util.Date;
import java.util.List;

public interface ISiteRepository {
    Site findById(String siteId);
    List<Site> findAllCapableSites(String merchandiseId);
    List<Site> findAvailable(String merchandiseId, Date time);
    void saveSiteState(Site site);
    boolean validateSite(String siteName);
}
