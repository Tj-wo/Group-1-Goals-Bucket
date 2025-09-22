package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.core.services.EmailNotificationService;
import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.utils.SecurePasswordGenerator;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // =====================
    // SAVE STAFF OPERATIONS
    // =====================

    @Override
    public Staff saveStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");

        Staff savedStaff = super.merge(staff);
        sendWelcomeEmailIfApplicable(savedStaff, null);

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

        userAccount.setRecordStatus(RecordStatus.ACTIVE);
        userAccount = userService.saveUser(userAccount);

        staff.setActive(true);
        staff.setStaffStatus(StaffStatus.ACTIVE);
        super.merge(staff);

        sendWelcomeEmailIfApplicable(staff, null);
        return userAccount;
    }

    // ==========================
    // USER ACCOUNT DEACTIVATION
    // ==========================

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
}