package org.pahappa.systems.kpiTracker.views.team;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamMemberService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.team.TeamMember;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

@ManagedBean(name = "teamMemberView")
@ViewScoped
@Getter
@Setter
public class TeamMemberView extends PaginatedTableView<TeamMember, TeamMemberService, TeamMemberService> {

    private static final long serialVersionUID = 1L;

    private TeamMemberService teamMemberService;
    private TeamService teamService;
    private String searchTerm;
    private Team selectedTeam;

    @PostConstruct
    public void init() {
        this.teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        // Check for teamId in session (set by TeamView navigation)
        String teamIdParam = (String) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("selectedTeamId");

        if (StringUtils.hasText(teamIdParam)) {
            try {
                // Load the selected team based on the ID
                this.selectedTeam = this.teamService.getInstanceByID(teamIdParam);
                // Clear the session parameter after using it
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                        .remove("selectedTeamId");
            } catch (Exception e) {
                javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_WARN, "Warning",
                        "Could not load team with ID: " + teamIdParam);
                FacesContext.getCurrentInstance().addMessage(null, message);
                e.printStackTrace();
            }
        }

        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        System.out.println("TeamMemberView: reloadFromDB called with offset=" + offset + ", limit=" + limit);

        Search search = buildSearch();
        search.setFirstResult(offset);
        search.setMaxResults(limit);

        List<TeamMember> teamMembers = this.teamMemberService.getInstances(search, offset, limit);
        System.out.println("TeamMemberView: Found " + teamMembers.size() + " team members");

        super.setDataModels(teamMembers);
    }

    @Override
    public void reloadFilterReset() {
        Search search = buildSearch();
        super.setTotalRecords(this.teamMemberService.countInstances(search));

        // Reload the data to show new/updated records
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TeamMember> load(int first, int pageSize, Map<String, SortMeta> sortBy,
            Map<String, FilterMeta> filterBy) {
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDataModels();
    }

    private Search buildSearch() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // Filter by selected team if any
        if (selectedTeam != null) {
            search.addFilterEqual("team", selectedTeam);
        }

        // Add search term filter for staff name and role
        if (StringUtils.hasText(searchTerm)) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.ilike("staff.firstName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("staff.lastName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("role", "%" + searchTerm + "%"));
        }

        return search;
    }

    public void searchTeamMembers() {
        reloadFilterReset();
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Force refresh the data table - useful for ensuring data is up-to-date
     */
    public void refreshTable() {
        try {
            reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearSearch() {
        this.searchTerm = null;
        this.selectedTeam = null;
        reloadFilterReset();
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedTeamMember(TeamMember teamMember) {
        try {
            if (teamMember != null) {
                this.teamMemberService.deleteInstance(teamMember);
                javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_INFO, "Success",
                        "Team member '" + teamMember.getStaff().getFirstName() + " "
                                + teamMember.getStaff().getLastName() + "' has been removed from team '"
                                + teamMember.getTeam().getTeamName() + "'.");
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, message);
                this.reloadFilterReset();
            } else {
                javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_ERROR, "Error",
                        "System Error: The record to delete was not provided.");
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception e) {
            javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                    javax.faces.application.FacesMessage.SEVERITY_ERROR, "Deletion Failed", e.getMessage());
            javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, message);
            e.printStackTrace();
        }
    }

    private TeamMember selectedTeamMember;

    public TeamMember getSelectedTeamMember() {
        return selectedTeamMember;
    }

    public void setSelectedTeamMember(TeamMember selectedTeamMember) {
        this.selectedTeamMember = selectedTeamMember;
    }

    public List<Team> getAllTeams() {
        try {
            Search search = new Search(Team.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            return this.teamService.getInstances(search, 0, Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get the selected team name for display purposes
     * 
     * @return The team name or "All Teams" if no team is selected
     */
    public String getSelectedTeamName() {
        return selectedTeam != null ? selectedTeam.getTeamName() : "All Teams";
    }

    @Override
    public List<org.sers.webutils.server.core.service.excel.reports.ExcelReport> getExcelReportModels() {
        return new java.util.ArrayList<>();
    }

    @Override
    public String getFileName() {
        return "team_members";
    }
}
