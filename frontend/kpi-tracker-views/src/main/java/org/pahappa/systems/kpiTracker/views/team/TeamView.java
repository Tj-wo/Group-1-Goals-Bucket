package org.pahappa.systems.kpiTracker.views.team;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.event.SelectEvent;
import org.pahappa.systems.kpiTracker.security.HyperLinks;

@ManagedBean(name = "teamView")
@ViewScoped
@Getter
@Setter
public class TeamView extends PaginatedTableView<Team, TeamService, TeamService> {

    private static final long serialVersionUID = 1L;

    private TeamService teamService;
    private String searchTerm;

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        System.out.println("TeamView: reloadFromDB called with offset=" + offset + ", limit=" + limit);

        Search search = buildSearch();
        search.setFirstResult(offset);
        search.setMaxResults(limit);

        List<Team> teams = this.teamService.getInstances(search, offset, limit);
        System.out.println("TeamView: Found " + teams.size() + " teams");

        for (Team team : teams) {
            System.out.println("TeamView: Team - " + team.getTeamName() + ", Lead: " +
                    (team.getTeamLead() != null
                            ? team.getTeamLead().getFirstName() + " " + team.getTeamLead().getLastName()
                            : "No Lead"));
        }

        super.setDataModels(teams);
    }

    @Override
    public void reloadFilterReset() {
        Search search = buildSearch();
        super.setTotalRecords(this.teamService.countInstances(search));

        // Reload the data to show new/updated records
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Team> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
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

        // Add search term filter for team name and description
        if (StringUtils.isNotBlank(searchTerm)) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.ilike("teamName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("description", "%" + searchTerm + "%"));
        }

        return search;
    }

    public void searchTeams() {
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
        reloadFilterReset();
        try {
            reloadFromDB(0, 10, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Team selectedTeam;

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    /**
     * Delete the selected team
     * 
     * @param selectedTeam The record selected by the user in the data table.
     */
    public void deleteSelectedTeam(Team selectedTeam) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedTeam != null) {
                teamService.deleteInstance(selectedTeam);
                javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_INFO, "Success",
                        "Team '" + selectedTeam.getTeamName() + "' has been deleted.");
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, message);
                this.reloadFilterReset();
                // The table refresh will be handled by the 'update' attribute on the button.
            } else {
                // This is a safeguard. It should not happen if the UI is correct.
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

    @Override
    public List<org.sers.webutils.server.core.service.excel.reports.ExcelReport> getExcelReportModels() {
        return new java.util.ArrayList<>();
    }

    @Override
    public String getFileName() {
        return "teams";
    }

    /**
     * Handle row selection event - navigate to team members view
     * 
     * @param event The SelectEvent containing the selected team
     */
    public void onRowSelect(SelectEvent event) {
        Team selectedTeam = (Team) event.getObject();
        if (selectedTeam != null) {
            try {
                // Store the selected team ID in session for the TeamMemberView to pick up
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                        .put("selectedTeamId", selectedTeam.getId());

                // Navigate to team members view using JSF navigation
                FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
                        .handleNavigation(FacesContext.getCurrentInstance(), null,
                                "/pages/team/TeamMemberView.xhtml?faces-redirect=true");
            } catch (Exception e) {
                javax.faces.application.FacesMessage message = new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_ERROR, "Navigation Error",
                        "Unable to navigate to team members view: " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                e.printStackTrace();
            }
        }
    }
}