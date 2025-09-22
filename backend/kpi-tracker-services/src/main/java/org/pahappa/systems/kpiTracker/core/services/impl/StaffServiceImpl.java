package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;


@Service("staffService")
@Transactional
public class StaffServiceImpl extends GenericServiceImpl<Staff> implements StaffService {

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;


    @Override
    public Staff saveStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");
        return super.merge(staff);
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
        Validate.notNull(staff.getUserAccount(), "Staff member does not have a user account.");
        User user = staff.getUserAccount();

        if (user.getRecordStatus() == RecordStatus.ACTIVE) {
            throw new OperationFailedException("This user account is already active.");
        }

        user.setRecordStatus(RecordStatus.ACTIVE);

        userService.saveUser(user);

        staff.setStaffStatus(StaffStatus.ACTIVE);
        this.staffDao.update(staff);

        return user;
    }

    public Role getNormalUserRole() {
        return (roleService.getRoleByName("Normal User"));
    }

    @Override
    public void deactivateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff member not specified");
        Validate.notNull(staff.getUserAccount(), "Staff member does not have a user account.");

        staff.setStaffStatus(StaffStatus.DEACTIVATED);
        this.staffDao.update(staff);

        User user = staff.getUserAccount();
        if (user != null) {
            user.setRecordStatus(RecordStatus.ACTIVE_LOCKED);
            userService.saveUser(user);
        }
    }

    @Override
    public boolean isDeletable(Staff instance) throws OperationFailedException {
        // You can add logic here to prevent deletion if the staff has other dependencies
        return true;
    }

    @Override
    public Staff saveInstance(Staff instance) throws ValidationFailedException, OperationFailedException {
        return saveStaff(instance);
    }
}