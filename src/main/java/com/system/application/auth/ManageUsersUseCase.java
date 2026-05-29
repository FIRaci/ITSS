package com.system.application.auth;

import com.system.infrastructure.persistence.UserRepositoryImpl;
import com.itss.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ManageUsersUseCase {
    private UserRepositoryImpl userRepository;

    public ManageUsersUseCase() {
        this.userRepository = new UserRepositoryImpl();
    }

    public ObservableList<User> getAllUsers() {
        return FXCollections.observableArrayList(userRepository.findAllUsers());
    }

    public void deleteUser(int id) throws Exception {
        boolean success = userRepository.deleteUser(id);
        if (!success) {
            throw new Exception("Lỗi hệ thống: Xóa người dùng thất bại.");
        }
    }

    public boolean addOrUpdateUser(String username, String pass, String role) {
        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Tài khoản và vai trò không được để trống!");
        }
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            return userRepository.updateUser(username, pass, role);
        } else {
            if (pass == null || pass.isEmpty()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo mới người dùng!");
            }
            return userRepository.insertUser(username, pass, role);
        }
    }
}
