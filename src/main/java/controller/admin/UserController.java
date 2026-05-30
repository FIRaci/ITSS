package controller.admin;

import subsystem.admin.UserSubController;
import entity.chung.User;
import java.util.List;

public class UserController {
    private UserSubController subController;

    public UserController() {
        this.subController = new UserSubController();
    }

    public List<User> getAllUsers() throws Exception {
        return subController.getAllUsers();
    }

    public void addUser(User u) throws Exception {
        if (u.getUsername() == null || u.getUsername().trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống!");
        }
        if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }
        
        List<User> existing = subController.getAllUsers();
        for (User item : existing) {
            if (item.getUsername().equalsIgnoreCase(u.getUsername())) {
                throw new Exception("Tên đăng nhập đã tồn tại!");
            }
        }
        subController.addUser(u);
    }

    public void toggleActiveStatus(User u) throws Exception {
        boolean newState = !u.isActive();
        subController.updateUserActive(u.getId(), newState);
        u.setActive(newState);
    }
}
