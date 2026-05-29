package entity.chung;

public class Permission {
    private String permissionId;
    private String permissionName;

    public Permission(String permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

    public String getPermissionId() { return permissionId; }
    public String getPermissionName() { return permissionName; }
}
