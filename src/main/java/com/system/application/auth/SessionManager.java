package com.system.application.auth;
import com.itss.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            // 1. Try Hash
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, com.system.infrastructure.security.PasswordUtils.hashPassword(password));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentUser = new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"), rs.getString("site_code"));
                    return true;
                }
            }

            // 2. Try Plaintext (Migration fallback)
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Update to hash
                    try (PreparedStatement update = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
                        update.setString(1, com.system.infrastructure.security.PasswordUtils.hashPassword(password));
                        update.setInt(2, rs.getInt("id"));
                        update.executeUpdate();
                    }
                    currentUser = new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"), rs.getString("site_code"));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
    }
}
