package com.system.domain.auth;

import java.util.List;

public interface IUserRepository {
    User findByUsername(String username);
    List<User> findAll();
    void save(User user);
    void updateStatus(String username, boolean isActive);
}
