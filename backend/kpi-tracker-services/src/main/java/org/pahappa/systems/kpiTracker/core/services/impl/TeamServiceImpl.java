package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.TeamMemberService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.team.TeamMember;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service("teamService")
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {
    
    @Override
    public boolean isDeletable(Team instance) throws OperationFailedException {
        // Add business logic to check if team can be deleted
        // For example, check if team has active members or goals
        return true;
    }

    @Override
    public Team saveInstance(Team entityInstance) throws ValidationFailedException, OperationFailedException {
        return super.save(entityInstance);
    }
    
    @Override
    public List<Staff> getTeamMembers(Team team) {
        List<TeamMember> teamMemberships = getTeamMemberships(team);
        List<Staff> staffMembers = new ArrayList<>();
        for (TeamMember membership : teamMemberships) {
            staffMembers.add(membership.getStaff());
        }
        return staffMembers;
    }
    
    @Override
    public List<TeamMember> getTeamMemberships(Team team) {
        TeamMemberService teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        Search search = new Search(TeamMember.class);
        search.addFilterEqual("team", team);
        return teamMemberService.getInstances(search, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public List<Team> getStaffTeams(Staff staff) {
        TeamMemberService teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        Search search = new Search(TeamMember.class);
        search.addFilterEqual("staff", staff);
        List<TeamMember> memberships = teamMemberService.getInstances(search, 0, Integer.MAX_VALUE);
        
        List<Team> teams = new ArrayList<>();
        for (TeamMember membership : memberships) {
            teams.add(membership.getTeam());
        }
        return teams;
    }
    
    @Override
    public void addTeamMember(Team team, Staff staff, String role) {
        // Check if staff is already in the team
        if (isStaffInTeam(team, staff)) {
            throw new RuntimeException("Staff member is already in this team");
        }
        
        TeamMemberService teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setStaff(staff);
        teamMember.setRole(role);
        
        try {
            teamMemberService.saveInstance(teamMember);
        } catch (ValidationFailedException | OperationFailedException e) {
            throw new RuntimeException("Failed to add team member", e);
        }
    }
    
    @Override
    public void removeTeamMember(Team team, Staff staff) {
        TeamMemberService teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        Search search = new Search(TeamMember.class);
        search.addFilterEqual("team", team);
        search.addFilterEqual("staff", staff);
        
        List<TeamMember> memberships = teamMemberService.getInstances(search, 0, Integer.MAX_VALUE);
        for (TeamMember membership : memberships) {
            try {
                teamMemberService.deleteInstance(membership);
            } catch (OperationFailedException e) {
                throw new RuntimeException("Failed to remove team member", e);
            }
        }
    }
    
    @Override
    public boolean isStaffInTeam(Team team, Staff staff) {
        TeamMemberService teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        Search search = new Search(TeamMember.class);
        search.addFilterEqual("team", team);
        search.addFilterEqual("staff", staff);
        
        return teamMemberService.countInstances(search) > 0;
    }
}
