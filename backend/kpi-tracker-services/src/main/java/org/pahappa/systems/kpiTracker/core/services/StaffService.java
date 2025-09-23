package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;

public interface StaffService extends GenericService<Staff> {

    Staff createNewStaff(Staff staff) throws ValidationFailedException;

    Staff saveStaff(Staff staff) throws ValidationFailedException;

    User activateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException;

    void deactivateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException;

    boolean canStaffAccessSystem(Staff staff);

    Staff getStaffByUser(User user);

    /**
     * Atomically updates a user's password, hashes it, and clears the first login flag on the associated staff profile.
     * @return The updated Staff entity.
     */
    Staff updatePasswordAndClearFirstLogin(Staff staff, String newPassword) throws ValidationFailedException, OperationFailedException;
}