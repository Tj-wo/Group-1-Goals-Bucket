package org.pahappa.systems.kpiTracker.views.staff;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean(name = "staffFormDialog")
@SessionScoped
@Getter
@Setter
public class StaffFormDialog extends DialogForm<Staff> {

    private static final long serialVersionUID = 1L;
    private StaffService staffService;
    private UserService userService;
    private RoleService roleService;
    private List<Gender> listOfGenders;
    private List<Role> allRoles;
    private Role selectedRole;
    private User savedUser;
    private boolean edit = false;


    public StaffFormDialog() {
        super(HyperLinks.STAFF_FORM_DIALOG, 700, 450);
    }

    @PostConstruct
    public void init() {
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);

        this.listOfGenders = Arrays.asList(Gender.values());
        this.allRoles = roleService.getRoles();
        resetModal();
    }

    @Override
    public void persist() throws Exception {

        model.setStaffStatus(StaffStatus.DEACTIVATED);

        this.staffService.saveStaff(model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Staff();
        this.allRoles = roleService.getRoles();
        this.edit = false;

    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.edit = true;
//        if(super.model != null){
////            selectedRoles = model.getUserAccount().getRoles();
//        }
    }
}
