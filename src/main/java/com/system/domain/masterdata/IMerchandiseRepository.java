package com.system.domain.masterdata;

import java.util.List;

public interface IMerchandiseRepository {
    Merchandise findById(String merchandiseId);
    List<Merchandise> findAll();
    List<Merchandise> search(String keyword);
    boolean checkExistence(String merchandiseId);
    void save(Merchandise merchandise);
}
