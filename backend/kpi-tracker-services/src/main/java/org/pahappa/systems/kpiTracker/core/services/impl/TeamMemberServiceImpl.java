package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.TeamMemberService;
import org.pahappa.systems.kpiTracker.models.team.TeamMember;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("teamMemberService")
@Transactional
public class TeamMemberServiceImpl extends GenericServiceImpl<TeamMember> implements TeamMemberService {
    
    @Override
    public boolean isDeletable(TeamMember instance) throws OperationFailedException {
        return true;
    }

    @Override
    public TeamMember saveInstance(TeamMember entityInstance) throws ValidationFailedException, OperationFailedException {
        return super.save(entityInstance);
    }
}

