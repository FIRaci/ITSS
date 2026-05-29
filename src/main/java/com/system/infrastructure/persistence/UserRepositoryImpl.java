package com.system.infrastructure.persistence;

import com.itss.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl {
    
    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id ASC")) {
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"), 
                    rs.getString("username"), 
                    rs.getString("role"), 
                    rs.getString("site_code")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteUser(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User findByUsername(String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"), 
                    rs.getString("username"), 
                    rs.getString("role"), 
                    rs.getString("site_code")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean updateUser(String username, String pass, String role) {
        try (Connection conn = Database.getConnection()) {
            String sql = pass.isEmpty() ? "UPDATE users SET role = ? WHERE username = ?" 
                                        : "UPDATE users SET role = ?, password = ? WHERE username = ?";
            PreparedStatement update = conn.prepareStatement(sql);
            update.setString(1, role);
            if (pass.isEmpty()) {
                update.setString(2, username);
            } else {
                update.setString(2, com.system.infrastructure.security.PasswordUtils.hashPassword(pass));
                update.setString(3, username);
            }
            return update.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertUser(String username, String pass, String role) {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            insert.setString(1, username);
            insert.setString(2, com.system.infrastructure.security.PasswordUtils.hashPassword(pass));
            insert.setString(3, role);
            return insert.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
