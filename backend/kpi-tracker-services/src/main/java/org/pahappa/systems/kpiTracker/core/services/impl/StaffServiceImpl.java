package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.core.services.EmailNotificationService;
import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.utils.SecurePasswordGenerator;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.security.util.CustomSecurityUtil;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.shared.SharedAppData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Service("staffService")
@Transactional
public class StaffServiceImpl extends GenericServiceImpl<Staff> implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Override
    public Staff createNewStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");
        Validate.notNull(staff.getUserAccount(), "Staff must have a user account");

        User user = staff.getUserAccount();
        User currentUser = SharedAppData.getLoggedInUser();

        if (userService.getUserByUsername(user.getUsername()) != null) {
            throw new ValidationFailedException("A user with the email '" + user.getUsername() + "' already exists.");
        }

        Date currentDate = new Date();
        user.setCreatedBy(currentUser);
        user.setDateCreated(currentDate);
        user.setChangedBy(currentUser);
        user.setDateChanged(currentDate);

        user.setClearTextPassword(UUID.randomUUID().toString());
        CustomSecurityUtil.prepUserCredentials(user);
        user.setRecordStatus(RecordStatus.ACTIVE_LOCKED);


        staff.setFirstName(user.getFirstName());
        staff.setLastName(user.getLastName());
        staff.setEmailAddress(user.getEmailAddress());
        staff.setPhoneNumber(user.getPhoneNumber());
        staff.setGender(user.getGender());
        staff.setUserAccount(user);
        staff.setStaffStatus(StaffStatus.DEACTIVATED);
        staff.setActive(false);
        staff.setFirstLogin(true); // A new staff member should always require a password change.

        return super.save(staff);
    }

    @Override
    public Staff saveStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");

        boolean isNewStaff = staff.getId() == null;

        Staff savedStaff = super.merge(staff);

        if (isNewStaff) {
            sendWelcomeEmailIfApplicable(savedStaff, null);
        }

        return savedStaff;
    }

    public Staff saveStaff(Staff staff, String generatedPassword) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");

        Staff savedStaff = super.merge(staff);
        sendWelcomeEmailIfApplicable(savedStaff, generatedPassword);

        return savedStaff;
    }

    /**
     * Helper method to send welcome email to the staff member
     */
    private void sendWelcomeEmailIfApplicable(Staff staff, String generatedPassword) {
        try {
            if (staff != null && staff.getUserAccount() != null) {
                User userAccount = staff.getUserAccount();
                String email = userAccount.getEmailAddress();
                String firstName = userAccount.getFirstName();
                String lastName = userAccount.getLastName();

                if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(firstName)) {
                    String fullName = firstName + (StringUtils.isNotBlank(lastName) ? " " + lastName : "");

                    // Generate password if not provided
                    String tempPassword = generatedPassword;
                    if (StringUtils.isBlank(tempPassword)) {
                        tempPassword = SecurePasswordGenerator.generateTemporaryPassword();
                        userAccount.setClearTextPassword(tempPassword);
                        CustomSecurityUtil.prepUserCredentials(userAccount); // CRITICAL: Hash the new password before saving.
                        userService.saveUser(userAccount);
                    }

                    // Send welcome email
                    emailNotificationService.sendWelcomeEmail(email, fullName, getApplicationBaseUrl(), tempPassword);
                    logger.info("Welcome email sent to: {}", email);
                } else {
                    logger.warn("Cannot send welcome email - missing email or name for staff ID: {}", staff.getId());
                }
            } else {
                logger.warn("Cannot send welcome email - staff has no user account");
            }
        } catch (Exception e) {
            logger.error("Failed to send welcome email for staff ID: {}",
                         (staff != null ? staff.getId() : "unknown"), e);
        }
    }

    private String getApplicationBaseUrl() {
        return "http://localhost:8080/kpi-tracker";
    }

    // ==========================
    // USER ACCOUNT ACTIVATION
    // ==========================

    @Override
    public User activateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff member not specified");
        Validate.notNull(staff.getUserAccount(), "Staff member does not have a user account.");

        User userAccount = staff.getUserAccount();

        if (userAccount.getRecordStatus() == RecordStatus.ACTIVE) {
            throw new OperationFailedException("This user account is already active.");
        }

        String tempPassword = SecurePasswordGenerator.generateTemporaryPassword();
        userAccount.setClearTextPassword(tempPassword);

        CustomSecurityUtil.prepUserCredentials(userAccount);

        userAccount.setRecordStatus(RecordStatus.ACTIVE);
        userService.saveUser(userAccount);

        staff.setActive(true);
        // staff.setFirstLogin(true); // This is now redundant as it's set on creation.
        staff.setStaffStatus(StaffStatus.ACTIVE);
        super.merge(staff);

        sendWelcomeEmail(staff, tempPassword);

        return userAccount;
    }

    private void sendWelcomeEmail(Staff staff, String password) {
        try {
            User userAccount = staff.getUserAccount();
            String email = userAccount.getEmailAddress();
            String firstName = userAccount.getFirstName();
            String lastName = userAccount.getLastName();

            if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(firstName)) {
                String fullName = firstName + (StringUtils.isNotBlank(lastName) ? " " + lastName : "");
                emailNotificationService.sendWelcomeEmail(email, fullName, getApplicationBaseUrl(), password);
                logger.info("Welcome email sent to newly activated user: {}", email);
            } else {
                logger.warn("Could not send welcome email - missing email or name for staff ID: {}", staff.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to send welcome email for staff ID: {}", staff.getId(), e);
        }
    }

    @Override
    public void deactivateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff member not specified");
        Validate.notNull(staff.getUserAccount(), "Staff member does not have a user account.");

        User userAccount = staff.getUserAccount();
        userAccount.setRecordStatus(RecordStatus.DELETED);
        userService.saveUser(userAccount);

        staff.setActive(false);
        staff.setStaffStatus(StaffStatus.DEACTIVATED);
        super.merge(staff);
    }

    // ==========================
    // CHECK IF STAFF CAN ACCESS
    // ==========================

    @Override
    public boolean canStaffAccessSystem(Staff staff) {
        if (staff == null) return false;

        return staff.isActive() &&
               staff.getStaffStatus() == StaffStatus.ACTIVE &&
               staff.getUserAccount() != null &&
               staff.getUserAccount().getRecordStatus() == RecordStatus.ACTIVE;
    }

    // ==========================
    // OTHER OPERATIONS
    // ==========================

    @Override
    public boolean isDeletable(Staff instance) throws OperationFailedException {
        return true; // Extend with business rules if needed
    }

    @Override
    public Staff saveInstance(Staff instance) throws ValidationFailedException, OperationFailedException {
        return saveStaff(instance);
    }

    private boolean isEmailTaken(String email, String staffId) {
        Search search = new Search(Staff.class);
        search.addFilterEqual("userAccount.emailAddress", email);
        search.addFilterEqual("userAccount.recordStatus", RecordStatus.ACTIVE); // Only active users
        if (StringUtils.isNotBlank(staffId)) {
            search.addFilterNotEqual("id", staffId);
        }
        return staffDao.count(search) > 0;
    }

    public Role getNormalUserRole() {
        return roleService.getRoleByName("Normal User");
    }

    @Override
    public Staff getStaffByUser(User user) {
        if (user == null) {
            return null;
        }
        // Use the searchUnique method from the parent GenericServiceImpl
        return super.searchUnique(new Search(Staff.class).addFilterEqual("userAccount", user));
    }

    @Override
    public Staff updatePasswordAndClearFirstLogin(Staff staff, String newPassword) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff cannot be null");
        Validate.notNull(staff.getUserAccount(), "Staff must have a user account");
        Validate.hasText(newPassword, "New password cannot be blank");

        // Re-fetch the user to ensure we are working with a managed entity
        User userToUpdate = userService.getUserByUsername(staff.getUserAccount().getUsername());
        if (userToUpdate == null) {
            throw new OperationFailedException("Could not reload the user profile.");
        }

        // Re-fetch the staff member to ensure it's also managed
        Staff staffToUpdate = this.getStaffByUser(userToUpdate);
        if (staffToUpdate == null) {
            throw new OperationFailedException("Could not find an associated staff profile.");
        }

        // 1. Set and hash the new password for the User
        userToUpdate.setClearTextPassword(newPassword);
        CustomSecurityUtil.prepUserCredentials(userToUpdate);

        // 2. Update the firstLogin flag on the Staff entity
        staffToUpdate.setFirstLogin(false);

        // The @Transactional annotation on the class will ensure both entities are saved
        // atomically when the method returns. Explicit saves are not needed here.
        return super.merge(staffToUpdate);
    }
}