package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.team.TeamMember;

import java.util.List;

public interface TeamService extends GenericService<Team> {
    
    /**
     * Get all staff members belonging to a specific team
     * @param team The team to get members for
     * @return List of staff members in the team
     */
    List<Staff> getTeamMembers(Team team);
    
    /**
     * Get all team memberships for a specific team
     * @param team The team to get memberships for
     * @return List of team memberships
     */
    List<TeamMember> getTeamMemberships(Team team);
    
    /**
     * Get all teams a staff member belongs to
     * @param staff The staff member
     * @return List of teams the staff belongs to
     */
    List<Team> getStaffTeams(Staff staff);
    
    /**
     * Add a staff member to a team
     * @param team The team to add the member to
     * @param staff The staff member to add
     * @param role Optional role within the team
     */
    void addTeamMember(Team team, Staff staff, String role);
    
    /**
     * Remove a staff member from a team
     * @param team The team to remove the member from
     * @param staff The staff member to remove
     */
    void removeTeamMember(Team team, Staff staff);
    
    /**
     * Check if a staff member is already in a team
     * @param team The team to check
     * @param staff The staff member to check
     * @return true if staff is already in the team
     */
    boolean isStaffInTeam(Team team, Staff staff);
}
