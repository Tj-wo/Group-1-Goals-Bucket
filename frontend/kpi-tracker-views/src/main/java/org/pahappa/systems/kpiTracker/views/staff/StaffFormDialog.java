package org.pahappa.systems.kpiTracker.views.staff;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
    private String generatedPassword;
    private boolean edit;

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
        if (edit) {
            userService.saveUser(super.getModel().getUserAccount());
            staffService.saveStaff(super.getModel());
        } else {
            createNewStaff();
        }
    }

    /**
     * Creates a new staff record and associated user account.
     */
    private void createNewStaff() throws Exception {
        if (model.getUserAccount() == null || StringUtils.isBlank(model.getUserAccount().getEmailAddress())) {
            throw new ValidationFailedException("User email is required for staff creation.");
        }

        User user = model.getUserAccount();
        user.setUsername(user.getEmailAddress());

        if (this.selectedRole != null) {
            user.setRoles(new HashSet<>(Collections.singletonList(this.selectedRole)));
        } else {
            throw new ValidationFailedException("A role must be selected for the new staff.");
        }

        this.staffService.createNewStaff(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Staff();
        this.edit = false;
        this.selectedRole = null;
        this.allRoles = roleService.getRoles();

        // Create a new default user for the staff
        User newUser = new User();
        super.model.setUserAccount(newUser);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.allRoles = roleService.getRoles();

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
            if (super.model.getUserAccount() != null && !super.model.getUserAccount().getRoles().isEmpty()) {
                this.selectedRole = super.model.getUserAccount().getRoles().iterator().next();
            }
        } else {
            if (super.model == null) {
                super.model = new Staff();
            }
            if (super.model.getUserAccount() == null) {
                User newUser = new User();
                super.model.setUserAccount(newUser);
            }
            setEdit(false);
        }
    }
}