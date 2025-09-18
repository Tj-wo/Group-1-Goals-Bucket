package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;

public interface StaffService extends GenericService<Staff> {

    Staff saveStaff(Staff staff) throws ValidationFailedException;

    User activateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException;

    void deactivateUserAccount(Staff staff) throws ValidationFailedException, OperationFailedException;
}