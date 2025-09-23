package org.pahappa.systems.kpiTracker.views.auth;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name = "changePasswordView")
@ViewScoped
@Getter
@Setter
public class ChangePasswordView implements Serializable {

    private static final long serialVersionUID = 1L;
    private String newPassword;
    private String confirmPassword;

    private UserService userService;
    private StaffService staffService;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
    }

    public void saveNewPassword() {
        try {
            if (StringUtils.isBlank(newPassword) || !newPassword.equals(confirmPassword)) {
                throw new ValidationFailedException("Passwords do not match or are blank.");
            }

            User loggedInUser = SharedAppData.getLoggedInUser();
            if (loggedInUser == null) {
                throw new ValidationFailedException("Your session has expired. Please log in again.");
            }

            // Find the staff record associated with the logged-in user
            Staff staff = staffService.getStaffByUser(loggedInUser);
            if (staff == null) {
                throw new OperationFailedException("Could not find an associated staff profile.");
            }

            // 1. Set the new password on the User object
            loggedInUser.setClearTextPassword(this.newPassword);

            // 2. Clear the firstLogin flag on the Staff object
            staff.setFirstLogin(false);

            // 3. Save both entities
            userService.saveUser(loggedInUser);
            staffService.saveStaff(staff);

            // 4. Show success and redirect
            UiUtils.showMessageBox("Password successfully updated. Redirecting to dashboard...", "Success");
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + HyperLinks.DASHBOARD);

        } catch (ValidationFailedException | OperationFailedException e) {
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        } catch (IOException e) {
            UiUtils.ComposeFailure("Redirection Failed", "Could not redirect to the dashboard.");
            e.printStackTrace();
        } catch (Exception e) {
            UiUtils.ComposeFailure("An unexpected error occurred", e.getMessage());
            e.printStackTrace();
        }
    }
}