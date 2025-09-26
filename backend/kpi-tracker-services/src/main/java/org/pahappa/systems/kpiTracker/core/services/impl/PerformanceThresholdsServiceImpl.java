package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.PerformanceThresholdsService;
import org.pahappa.systems.kpiTracker.models.systemSetup.PerformanceThresholds;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PerformanceThresholdsServiceImpl extends GenericServiceImpl<PerformanceThresholds> implements PerformanceThresholdsService {
    @Override
    public boolean isDeletable(PerformanceThresholds instance) throws OperationFailedException {
        return true;
    }

    @Override
    public PerformanceThresholds saveInstance(PerformanceThresholds entityInstance) throws ValidationFailedException, OperationFailedException {
        return  super.save(entityInstance);
    }
}
