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

    /**
     * Retrieves a Staff entity using the associated User account.
     * @param user The user account to search with.
     * @return The matching Staff member, or null if not found.
     */
    Staff getStaffByUser(User user);
}