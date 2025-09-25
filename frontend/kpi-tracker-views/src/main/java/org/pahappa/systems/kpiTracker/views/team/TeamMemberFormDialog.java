package org.pahappa.systems.kpiTracker.views.team;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamMemberService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.team.TeamMember;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import com.googlecode.genericdao.search.Search;
import org.sers.webutils.model.RecordStatus;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "teamMemberFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path = HyperLinks.TEAM_MEMBER_FORM_DIALOG)
@SessionScoped
public class TeamMemberFormDialog extends DialogForm<TeamMember> {

    private static final long serialVersionUID = 1L;
    private TeamMemberService teamMemberService;
    private TeamService teamService;
    private StaffService staffService;

    private List<Staff> availableStaff;
    private List<Team> allTeams;
    private Team selectedTeam;

    private boolean edit;

    @PostConstruct
    public void init() {
        try {
            this.teamMemberService = ApplicationContextProvider.getBean(TeamMemberService.class);
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);

            loadAvailableStaff();
            loadAvailableTeams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads available staff (excluding those already in the selected team)
     */
    private void loadAvailableStaff() {
        try {
            Search search = new Search(Staff.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            List<Staff> allStaff = this.staffService.getInstances(search, 0, Integer.MAX_VALUE);

            if (selectedTeam != null) {
                // Get staff already in this team
                List<Staff> teamMembers = this.teamService.getTeamMembers(selectedTeam);
                this.availableStaff = new java.util.ArrayList<>();

                for (Staff staff : allStaff) {
                    boolean isInTeam = teamMembers.contains(staff);
                    boolean isCurrentMember = (super.model != null && super.model.getStaff() != null &&
                            super.model.getStaff().getId().equals(staff.getId()));

                    // Include staff if they're not in the team OR if they're the current member
                    // (for editing)
                    if (!isInTeam || isCurrentMember) {
                        this.availableStaff.add(staff);
                    }
                }
            } else {
                this.availableStaff = allStaff;
            }

            System.out.println("TeamMemberFormDialog: Loaded " + this.availableStaff.size() + " available staff");
        } catch (Exception e) {
            e.printStackTrace();
            this.availableStaff = this.staffService.getAllInstances();
        }
    }

    /**
     * Loads all active teams
     */
    private void loadAvailableTeams() {
        try {
            Search search = new Search(Team.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            this.allTeams = this.teamService.getInstances(search, 0, Integer.MAX_VALUE);
            System.out.println("TeamMemberFormDialog: Loaded " + this.allTeams.size() + " teams");
        } catch (Exception e) {
            e.printStackTrace();
            this.allTeams = this.teamService.getAllInstances();
        }
    }

    public TeamMemberFormDialog() {
        super(HyperLinks.TEAM_MEMBER_FORM_DIALOG, 600, 400);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        System.out.println("TeamMemberFormDialog: Saving team member");
        System.out.println("TeamMemberFormDialog: Team: "
                + (super.model.getTeam() != null ? super.model.getTeam().getTeamName() : "No Team"));
        System.out.println("TeamMemberFormDialog: Staff: " + (super.model.getStaff() != null
                ? super.model.getStaff().getFirstName() + " " + super.model.getStaff().getLastName()
                : "No Staff"));
        System.out.println("TeamMemberFormDialog: Role: " + super.model.getRole());

        this.teamMemberService.saveInstance(super.model);
        System.out.println("TeamMemberFormDialog: Team member saved successfully");
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new TeamMember();
        setEdit(false);
        this.selectedTeam = null;
        loadAvailableStaff();
        loadAvailableTeams();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();

        if (super.model != null && super.model.getTeam() != null) {
            this.selectedTeam = super.model.getTeam();
        }

        loadAvailableStaff();
        loadAvailableTeams();

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new TeamMember();
            }
            setEdit(false);
        }
    }

    /**
     * Called when team selection changes
     */
    public void onTeamChange() {
        if (super.model != null && super.model.getTeam() != null) {
            this.selectedTeam = super.model.getTeam();
            loadAvailableStaff();
        }
    }
}
