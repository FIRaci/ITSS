package entity.chung;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String siteCode;
    private boolean isActive;

    public User(int id, String username, String password, String role, String siteCode) {
        this(id, username, password, role, siteCode, true);
    }
    
    public User(int id, String username, String password, String role, String siteCode, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.siteCode = siteCode;
        this.isActive = isActive;
    }

    public User(int id, String username, String role, String siteCode) {
        this(id, username, null, role, siteCode);
    }

    public User(String username, String role) {
        this(0, username, null, role, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
