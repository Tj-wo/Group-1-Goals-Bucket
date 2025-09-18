package org.pahappa.systems.kpiTracker.views.staff;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.primefaces.model.DualListModel;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean(name = "staffFormDialog")
@ViewScoped
@Getter
@Setter
public class StaffFormDialog extends DialogForm<Staff> {

    private static final long serialVersionUID = 1L;
    private StaffService staffService;
    private UserService userService;
    private RoleService roleService;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private Gender gender;
    private List<Gender> listOfGenders;
    private List<Role> allRoles;
    private Set<Role> selectedRoles;
    private User savedUser;


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
        User user = new User() ;
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailAddress(emailAddress);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);
        user.setRecordStatus(RecordStatus.ACTIVE);
        user.setUsername(emailAddress);
        user.setClearTextPassword("password123");
        user.setRoles(selectedRoles);

        savedUser = userService.saveUser(user);

        System.out.println("Saved User: " + savedUser);

        model.setStaffStatus(StaffStatus.DEACTIVATED);

        model.setUserAccount(savedUser);

        this.staffService.saveStaff(model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Staff();
        this.allRoles = roleService.getRoles();

    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.allRoles = roleService.getRoles();

    }
}
