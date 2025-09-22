package org.pahappa.systems.kpiTracker.views.staff;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.utils.SecurePasswordGenerator;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
        if (super.model == null) {
            resetModal();
        }
    }

    @Override
    public void persist() throws Exception {
        if (edit) {
            // Update existing staff
            this.staffService.saveStaff(super.model);
        } else {
            // Create new staff with user account
            createNewStaff();
        }
    }

    private void createNewStaff() throws Exception {
        // Ensure we have a user account to work with
        if (model.getUserAccount() == null) {
            throw new ValidationFailedException("User account is required for staff creation");
        }

        User user = model.getUserAccount();

        // Generate a secure random password for the new user
        generatedPassword = SecurePasswordGenerator.generateTemporaryPassword();
        user.setClearTextPassword(generatedPassword);
        user.setRecordStatus(RecordStatus.ACTIVE);
        user.setUsername(user.getEmailAddress());

        savedUser = userService.saveUser(user);
        System.out.println("Saved User: " + savedUser);

        // Set up staff
        model.setStaffStatus(StaffStatus.DEACTIVATED);
        model.setActive(true);
        model.setUserAccount(savedUser);

        // Save staff with the generated password for welcome email
        this.staffService.saveStaff(super.model, generatedPassword);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Staff();
        setEdit(false);
        this.allRoles = roleService.getRoles();

        // Initialize a new user account for the staff
        User newUser = new User();
        newUser.setRecordStatus(RecordStatus.ACTIVE);
        super.model.setUserAccount(newUser);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.allRoles = roleService.getRoles();

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new Staff();
            }
            if (super.model.getUserAccount() == null) {
                User newUser = new User();
                newUser.setRecordStatus(RecordStatus.ACTIVE);
                super.model.setUserAccount(newUser);
            }
            setEdit(false);
        }
    }

}
