package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.core.services.EmailNotificationService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.utils.SecurePasswordGenerator;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

@Service("staffService")
@Transactional
public class StaffServiceImpl extends GenericServiceImpl<Staff> implements StaffService {

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailService mailService;
    @Autowired
    private EmailNotificationService emailNotificationService;

    User savedUser;

    @Override
    public Staff saveStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");

        // Save the staff member
        Staff savedStaff = super.merge(staff);

        // Send welcome email if staff has a user account with email
        sendWelcomeEmailIfApplicable(savedStaff, null);

        return savedStaff;
    }

    /**
     * Saves a staff member with a specific generated password for welcome email
     *
     * @param staff             The staff member to save
     * @param generatedPassword The password that was generated for the user
     * @return The saved staff member
     * @throws ValidationFailedException if validation fails
     */
    public Staff saveStaff(Staff staff, String generatedPassword) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");

        // Save the staff member
        Staff savedStaff = super.merge(staff);

        // Send welcome email if staff has a user account with email
        sendWelcomeEmailIfApplicable(savedStaff, generatedPassword);

        return savedStaff;
    }

    /**
     * Sends a welcome email to the staff member if they have a user account with
     * email
     *
     * @param staff             The staff member to send welcome email to
     * @param generatedPassword The password that was generated for the user (can be
     *                          null)
     */
    private void sendWelcomeEmailIfApplicable(Staff staff, String generatedPassword) {

        try {
            if (staff != null && staff.getUserAccount() != null) {
                User userAccount = staff.getUserAccount();
                String email = userAccount.getEmailAddress();
                String firstName = userAccount.getFirstName();
                String lastName = userAccount.getLastName();

                // Check if we have the required information to send email
                if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(firstName)) {
                    String fullName = firstName;
                    if (StringUtils.isNotBlank(lastName)) {
                        fullName += " " + lastName;
                    }

                    // Use the provided password or generate a new one if not provided
                    String temporaryPassword = generatedPassword;
                    if (StringUtils.isBlank(temporaryPassword)) {
                        temporaryPassword = SecurePasswordGenerator.generateTemporaryPassword();
                        // Update the user with the new password
                        userAccount.setClearTextPassword(temporaryPassword);
                        userService.saveUser(userAccount);
                    }

                    // Generate login URL (you may need to adjust this based on your application
                    // URL)
                    String loginUrl = getApplicationBaseUrl();

                    // Send welcome email with the generated password
                    emailNotificationService.sendWelcomeEmail(email, fullName, loginUrl, temporaryPassword);

                    // Log the successful email send
                    System.out.println("Welcome email sent successfully to: " + email + " for staff: " + fullName);
                    System.out.println("Temporary password generated for user: " + userAccount.getUsername());
                } else {
                    System.out.println(
                            "Cannot send welcome email - missing email or name for staff ID: " + staff.getId());
                }
            } else {
                System.out.println("Cannot send welcome email - staff has no user account");
            }
        } catch (Exception e) {
            // Log the error but don't fail the staff save operation
            System.err.println(
                    "Failed to send welcome email for staff ID: " + (staff != null ? staff.getId() : "unknown") +
                            ". Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the application base URL for login links
     * You can configure this based on your environment
     *
     * @return The application base URL
     */
    private String getApplicationBaseUrl() {
        // You can make this configurable through application properties
        // For now, using localhost - update this for production
        return "http://localhost:8080/kpi-tracker";
    }

    private boolean isEmailTaken(String email, String staffId) {
        Search search = new Search(Staff.class);
        search.addFilterEqual("emailAddress", email);
        if (StringUtils.isNotBlank(staffId)) {
            search.addFilterNotEqual("id", staffId);
        }
        return staffDao.count(search) > 0;
    }

    @Override
    public User activateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff member not specified");

        if (staff.getUserAccount() == null) {
            throw new ValidationFailedException("Staff member does not have a user account to activate.");
        }

        User userAccount = staff.getUserAccount();

        // Set user as active
        userAccount.setRecordStatus(RecordStatus.ACTIVE);
        userAccount = userService.saveUser(userAccount);

        // Set staff as active
        staff.setActive(true);
        staff.setStaffStatus(StaffStatus.ACTIVE);
        super.merge(staff);

        // Send activation email with credentials
        sendWelcomeEmailIfApplicable(staff, null);

        return userAccount;
    }

    @Override
    public void deactivateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff member not specified");
        Validate.notNull(staff.getUserAccount(), "Staff member does not have a user account.");

        User userAccount = staff.getUserAccount();

        // Deactivate user account (don't delete)
        userAccount.setRecordStatus(RecordStatus.DELETED);
        userService.saveUser(userAccount);

        // Deactivate staff
        staff.setActive(false);
        staff.setStaffStatus(StaffStatus.DEACTIVATED);
        super.merge(staff);
    }

    @Override
    public boolean canStaffAccessSystem(Staff staff) {
        if (staff == null) {
            return false;
        }

        // Check if staff is active
        if (!staff.isActive()) {
            return false;
        }

        // Check if staff status is active
        if (staff.getStaffStatus() != StaffStatus.ACTIVE) {
            return false;
        }

        // Check if user account exists and is active
        if (staff.getUserAccount() == null) {
            return false;
        }

        if (staff.getUserAccount().getRecordStatus() != RecordStatus.ACTIVE) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isDeletable(Staff instance) throws OperationFailedException {
        // You can add logic here to prevent deletion if the staff has other
        // dependencies
        return true;
    }

    @Override
    public Staff saveInstance(Staff instance) throws ValidationFailedException, OperationFailedException {
        return saveStaff(instance);
    }
}