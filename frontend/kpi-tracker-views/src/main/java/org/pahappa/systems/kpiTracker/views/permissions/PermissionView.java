package org.pahappa.systems.kpiTracker.views.permissions;


import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@ManagedBean(name = "permissionsView")
@SessionScoped
public class PermissionView implements Serializable {

    private List<RolePermissionMap> rolePermissionMapList = new ArrayList<>();
    private Long roleId;
    private Role selectedRole;
    private PermissionService permissionService;
    private RoleService roleService;

    private List<SelectItem> groupedPermissions;
    private Set<Permission> selectedPermissions = new HashSet<>();
    private Set<String> selectedPermissionIds = new HashSet<>();
    private Map<String, Boolean> selectAllCategories = new HashMap<>();
    private List<Permission> allPermissions;

    @PostConstruct
    public void init() {
        this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.allPermissions = permissionService.getPermissions();

    }

    private void generateGroupedPermissions(List<Permission> permissions) {
        Map<String, List<Permission>> categorizedPermissions = new HashMap<>();

        for (Permission permission : permissions) {
            String category = permission.getCustomPropOne();
            if (category == null || category.trim().isEmpty()) {
                category = "General";
            }
            categorizedPermissions.computeIfAbsent(category, k -> new ArrayList<>()).add(permission);
        }
        List<String> sortedCategories = new ArrayList<>(categorizedPermissions.keySet());
        Collections.sort(sortedCategories);

        for (String category : sortedCategories) {
            //get the permissions from the categorized permissions of a particular category
            List<Permission> permissionsInCategory = categorizedPermissions.get(category);
            //get permissions from the role that are in the permissionsInCategory
            List<Permission> selectedPermissionsInCategory = new ArrayList<>();
            for (Permission permission : selectedPermissions) {
                if (permissionsInCategory.contains(permission)) {
                    selectedPermissionsInCategory.add(permission);
                }
            }
            RolePermissionMap newRolePermissionMap = new RolePermissionMap(category, permissionsInCategory, selectedPermissionsInCategory);

            // Check if all permissions in the category are selected and set the flag
            newRolePermissionMap.setCategorySelected(permissionsInCategory.size() == selectedPermissionsInCategory.size() && !permissionsInCategory.isEmpty());

            rolePermissionMapList.add(newRolePermissionMap);
        }
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
        selectedPermissions.clear();
        rolePermissionMapList.clear();
        if (selectedRole != null)
            this.selectedPermissions = selectedRole.getPermissions();

        generateGroupedPermissions(this.allPermissions);
    }

    public void persist() {
        if (this.selectedRole == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "No Role Selected", "Please select a role first."));
            return;
        }
        try {
            Set<Permission> permissionsToSave = new HashSet<>();
            for (RolePermissionMap map : this.rolePermissionMapList) {
                if (map.getSelectedPermissions() != null) {
                    permissionsToSave.addAll(map.getSelectedPermissions());
                }
            }

            this.selectedRole.setPermissions(permissionsToSave);
            roleService.saveRole(this.selectedRole);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Permissions for role '" + this.selectedRole.getName() + "' updated successfully."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving permissions."));
            e.printStackTrace();
        }
    }

    public void selectAllForCategory(RolePermissionMap rolePermissionMap) {
        if (rolePermissionMap.isCategorySelected()) {
            rolePermissionMap.setSelectedPermissions(new ArrayList<>(rolePermissionMap.getPermissions()));
        } else {
            rolePermissionMap.getSelectedPermissions().clear();
        }
    }


    public String redirectToRolesView() throws IOException {
        return HyperLinks.ROLES_VIEW;
    }
    public void updateCategorySelection(RolePermissionMap map) {
        if (map.getSelectedPermissions() == null) {
            map.setCategorySelected(false);
            return;
        }
        map.setCategorySelected(map.getSelectedPermissions().size() == map.getPermissions().size());
    }

}
