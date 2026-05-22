package com.itss;

public class User {
    private int id;
    private String username;
    private String role;
    private String siteCode;

    public User(int id, String username, String role, String siteCode) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.siteCode = siteCode;
    }
    
    // For creating and updating in table view
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
}

