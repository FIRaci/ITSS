package com.system.domain.auth;

public class Permission {
    private String permissionId;
    private String permissionName;

    public Permission() {}

    public Permission(String permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

    public String getPermissionId() { return permissionId; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
}
