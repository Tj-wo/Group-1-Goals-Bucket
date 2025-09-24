package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("departmentService")
@Transactional
public class DepartmentServiceImpl extends GenericServiceImpl<Department> implements DepartmentService {
    @Override
    public boolean isDeletable(Department instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Department saveInstance(Department entityInstance) throws ValidationFailedException, OperationFailedException {
        return super.save(entityInstance);    }
}
