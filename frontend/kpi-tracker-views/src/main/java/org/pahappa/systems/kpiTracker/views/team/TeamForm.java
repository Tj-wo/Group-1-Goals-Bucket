package org.pahappa.systems.kpiTracker.views.team;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "teamForm")
@ViewScoped
@Getter
@Setter
public class TeamForm {

    private TeamService teamService;
    private DepartmentService departmentService;
    private StaffService staffService;
    
    private Team model;
    private List<Department> allDepartments;
    private List<Staff> allStaff;

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        
        loadAvailableDepartments();
        loadAvailableStaff();
    }

    public void setFormProperties(Team team) {
        this.model = team;
        loadAvailableDepartments();
        loadAvailableStaff();
    }

    public void resetModal() {
        this.model = new Team();
        loadAvailableDepartments();
        loadAvailableStaff();
    }

    private void loadAvailableDepartments() {
        Search search = new Search(Department.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.allDepartments = this.departmentService.getInstances(search, 0, Integer.MAX_VALUE);
        System.out.println("TeamForm: Loaded " + allDepartments.size() + " departments");
    }

    private void loadAvailableStaff() {
        Search search = new Search(Staff.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.allStaff = this.staffService.getInstances(search, 0, Integer.MAX_VALUE);
        System.out.println("TeamForm: Loaded " + allStaff.size() + " staff members");
    }

    public void persist() throws ValidationFailedException, OperationFailedException {
        System.out.println("TeamForm: persist() called for team: " + model.getTeamName());
        System.out.println("TeamForm: Department: " + (model.getDepartment() != null ? model.getDepartment().getName() : "No Department"));
        System.out.println("TeamForm: Team Lead: " + (model.getTeamLead() != null ? 
            model.getTeamLead().getFirstName() + " " + model.getTeamLead().getLastName() : "No Team Lead"));
        
        if (StringUtils.hasText(model.getTeamName())) {
            this.teamService.saveInstance(model);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team saved successfully"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Team name is required"));
        }
    }

    public void deleteInstance(Team team) throws OperationFailedException {
        try {
            this.teamService.deleteInstance(team);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Team cannot be deleted: " + e.getMessage()));
        }
    }
}
