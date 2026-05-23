package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserController {
    private UserRepository userRepository;

    public UserController() {
        this.userRepository = new UserRepository();
    }

    public ObservableList<User> getAllUsers() {
        return FXCollections.observableArrayList(userRepository.findAllUsers());
    }

    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    public void addOrUpdateUser(String username, String pass, String role) {
        if(username == null || username.trim().isEmpty()) return;
        
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            userRepository.updateUser(username, pass, role);
        } else {
            userRepository.insertUser(username, pass, role);
        }
    }
}
