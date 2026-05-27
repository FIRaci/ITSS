package com.itss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    
    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
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

    public void deleteUser(int id) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
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

    public void updateUser(String username, String pass, String role) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            String sql = pass.isEmpty() ? "UPDATE users SET role = ? WHERE username = ?" 
                                        : "UPDATE users SET role = ?, password = ? WHERE username = ?";
            PreparedStatement update = conn.prepareStatement(sql);
            update.setString(1, role);
            if (pass.isEmpty()) {
                update.setString(2, username);
            } else {
                update.setString(2, pass);
                update.setString(3, username);
            }
            update.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String username, String pass, String role) {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            if(pass.isEmpty()) pass = username + "123";
            PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            insert.setString(1, username);
            insert.setString(2, pass);
            insert.setString(3, role);
            insert.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
