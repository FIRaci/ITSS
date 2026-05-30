package com.system.domain.auth;

public class User {
    private String userId;
    private String username;
    private String password;
    private String role;
    private String siteCode;
    private boolean isActive;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    public User(String userId, String username, String role, String siteCode) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.siteCode = siteCode;
        this.isActive = true;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getSiteCode() { return siteCode; }
    public boolean isActive() { return isActive; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public void setActive(boolean active) { this.isActive = active; }
}
