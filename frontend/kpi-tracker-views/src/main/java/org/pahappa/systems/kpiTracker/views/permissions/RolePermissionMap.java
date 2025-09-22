package org.pahappa.systems.kpiTracker.views.permissions;

import lombok.Getter;
import lombok.Setter;
import org.sers.webutils.model.security.Permission;

import java.util.List;

@Getter
@Setter
public class RolePermissionMap {
    private String category;
    private boolean categorySelected;
    private List<Permission> permissions;
    private List<Permission> selectedPermissions;

    public RolePermissionMap(String category, List<Permission> permissions) {
        this.permissions = permissions;
        this.category = category;
    }

    public RolePermissionMap(String category, List<Permission> permissions, List<Permission> selectedPermissions) {
        this.category = category;
        this.permissions = permissions;
        this.selectedPermissions = selectedPermissions;
    }
}
