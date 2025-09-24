package org.pahappa.systems.kpiTracker.views.department;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
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


@ManagedBean(name = "departmentFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path= HyperLinks.DEPARTMENT_FORM_DIALOG)
@SessionScoped
public class DepartmentForm  extends DialogForm<Department> {

    private static final long serialVersionUID = 1L;
    private DepartmentService departmentService;
    private StaffService staffService;

    private List<Staff> availableStaff; // Add a list to hold users for the dropdown
    private List<Staff> allStaff; // All staff members
    private List<Staff> assignedStaff; // Staff already assigned as department leads

    private boolean edit;

    @PostConstruct
    public void init() {
        try {

            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);
            // Load all staff and filter available ones
            this.loadAvailableStaff();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads available staff (excluding those already assigned as department leads)
     */
    private void loadAvailableStaff() {
        try {
            // Get all active staff
            this.allStaff = this.staffService.getAllInstances();
            
            // Get staff already assigned as department leads
            Search search = new Search(Department.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterNotNull("departmentLead");
            List<Department> departmentsWithLeads = this.departmentService.getInstances(search, 0, Integer.MAX_VALUE);
            
            // Extract assigned staff IDs
            List<String> assignedStaffIds = new java.util.ArrayList<>();
            for (Department dept : departmentsWithLeads) {
                if (dept.getDepartmentLead() != null) {
                    assignedStaffIds.add(dept.getDepartmentLead().getId());
                }
            }
            
            // Filter out assigned staff, but include current department's lead if editing
            this.availableStaff = new java.util.ArrayList<>();
            for (Staff staff : this.allStaff) {
                boolean isAssigned = assignedStaffIds.contains(staff.getId());
                boolean isCurrentLead = (super.model != null && super.model.getDepartmentLead() != null && 
                                       super.model.getDepartmentLead().getId().equals(staff.getId()));
                
                // Include staff if they're not assigned OR if they're the current lead (for editing)
                if (!isAssigned || isCurrentLead) {
                    this.availableStaff.add(staff);
                }
            }
            
            System.out.println("DepartmentForm: Loaded " + this.availableStaff.size() + " available staff out of " + this.allStaff.size() + " total staff");
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to all staff if filtering fails
            this.availableStaff = this.staffService.getAllInstances();
        }
    }
    /**
     * The constructor now initializes the model object immediately,
     * ensuring it is never null when the JSF page is rendered.
     */
    public DepartmentForm() {
        super(HyperLinks.DEPARTMENT_FORM_DIALOG, 600, 400);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        System.out.println("DepartmentForm: Saving department with lead: " + 
            (super.model.getDepartmentLead() != null ? super.model.getDepartmentLead().getFirstName() + " " + super.model.getDepartmentLead().getLastName() : "null"));
        this.departmentService.saveInstance(super.model);
        System.out.println("DepartmentForm: Department saved successfully");
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Department();
        setEdit(false);
        // Refresh available staff when resetting
        this.loadAvailableStaff();
    }

    /**
     * This method is called when you want to load an existing department for editing.
     * The model is set from outside (e.g., using f:setPropertyActionListener).
     * This logic will handle both creating a new model if one doesn't exist and
     * setting the edit flag if it does.
     */
    @Override
    public void setFormProperties()  {
        super.setFormProperties();
        try {
            // Refresh the available staff list to ensure we have the latest data
            this.loadAvailableStaff();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            // If for some reason the model is null, ensure a new one is created.
            if (super.model == null) {
                super.model = new Department();
            }
            setEdit(false);
        }
    }
    
    /**
     * Get all staff (for debugging or other purposes)
     */
    public List<Staff> getAllStaff() {
        return this.allStaff;
    }
    
    /**
     * Get assigned staff (for debugging purposes)
     */
    public List<Staff> getAssignedStaff() {
        return this.assignedStaff;
    }
}