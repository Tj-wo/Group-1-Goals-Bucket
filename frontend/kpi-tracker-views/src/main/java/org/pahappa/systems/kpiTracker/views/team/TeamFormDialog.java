package org.pahappa.systems.kpiTracker.views.team;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
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

@ManagedBean(name = "teamFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path = HyperLinks.TEAM_FORM_DIALOG)
@SessionScoped
public class TeamFormDialog extends DialogForm<Team> {

    private static final long serialVersionUID = 1L;
    private TeamService teamService;
    private DepartmentService departmentService;
    private StaffService staffService;

    private List<Staff> allStaff;
    private List<Department> allDepartments;

    private boolean edit;

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);

            loadAvailableStaff();
            loadAvailableDepartments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all active staff members
     */
    private void loadAvailableStaff() {
        try {
            Search search = new Search(Staff.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            this.allStaff = this.staffService.getInstances(search, 0, Integer.MAX_VALUE);
            System.out.println("TeamFormDialog: Loaded " + this.allStaff.size() + " staff members");
        } catch (Exception e) {
            e.printStackTrace();
            this.allStaff = this.staffService.getAllInstances();
        }
    }

    /**
     * Loads all active departments
     */
    private void loadAvailableDepartments() {
        try {
            Search search = new Search(Department.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            this.allDepartments = this.departmentService.getInstances(search, 0, Integer.MAX_VALUE);
            System.out.println("TeamFormDialog: Loaded " + this.allDepartments.size() + " departments");
        } catch (Exception e) {
            e.printStackTrace();
            this.allDepartments = this.departmentService.getAllInstances();
        }
    }

    public TeamFormDialog() {
        super(HyperLinks.TEAM_FORM_DIALOG, 700, 500);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        System.out.println("TeamFormDialog: Saving team: " + super.model.getTeamName());
        System.out.println("TeamFormDialog: Department: "
                + (super.model.getDepartment() != null ? super.model.getDepartment().getName() : "No Department"));
        System.out.println("TeamFormDialog: Team Lead: " + (super.model.getTeamLead() != null
                ? super.model.getTeamLead().getFirstName() + " " + super.model.getTeamLead().getLastName()
                : "No Team Lead"));

        // Validate required fields
        if (super.model.getTeamName() == null || super.model.getTeamName().trim().isEmpty()) {
            throw new ValidationFailedException("Team name is required");
        }

        // Validate foreign key references if they are set
        if (super.model.getDepartment() != null && super.model.getDepartment().getId() == null) {
            throw new ValidationFailedException("Selected department is not valid");
        }

        if (super.model.getTeamLead() != null && super.model.getTeamLead().getId() == null) {
            throw new ValidationFailedException("Selected team lead is not valid");
        }

        // Check for duplicate team name
        try {
            Search search = new Search(Team.class);
            search.addFilterEqual("teamName", super.model.getTeamName());
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            if (super.model.getId() != null) {
                search.addFilterNotEqual("id", super.model.getId());
            }
            List<Team> existingTeams = this.teamService.getInstances(search, 0, 1);
            if (!existingTeams.isEmpty()) {
                throw new ValidationFailedException(
                        "A team with the name '" + super.model.getTeamName() + "' already exists");
            }
        } catch (Exception e) {
            if (e instanceof ValidationFailedException) {
                throw e;
            }
            System.out.println("TeamFormDialog: Error checking for duplicate team name: " + e.getMessage());
        }

        // Debug: Print all field values before saving
        System.out.println("TeamFormDialog: Debug - Team fields before save:");
        System.out.println("  - ID: " + super.model.getId());
        System.out.println("  - Team Name: " + super.model.getTeamName());
        System.out.println("  - Description: " + super.model.getDescription());
        System.out.println("  - Department: "
                + (super.model.getDepartment() != null ? super.model.getDepartment().getId() : "null"));
        System.out.println(
                "  - Team Lead: " + (super.model.getTeamLead() != null ? super.model.getTeamLead().getId() : "null"));
        System.out.println("  - Record Status: " + super.model.getRecordStatus());
        System.out.println("  - Created By: "
                + (super.model.getCreatedBy() != null ? super.model.getCreatedBy().getId() : "null"));
        System.out.println("  - Changed By: "
                + (super.model.getChangedBy() != null ? super.model.getChangedBy().getId() : "null"));
        System.out.println("  - Custom Prop One: " + super.model.getCustomPropOne());

        try {
            this.teamService.saveInstance(super.model);
            System.out.println("TeamFormDialog: Team saved successfully");
        } catch (Exception e) {
            System.err.println("TeamFormDialog: Error saving team: " + e.getMessage());
            e.printStackTrace();
            throw new ValidationFailedException("Failed to save team: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
        setEdit(false);
        loadAvailableStaff();
        loadAvailableDepartments();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        loadAvailableStaff();
        loadAvailableDepartments();

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new Team();
            }
            setEdit(false);
        }
    }
}
