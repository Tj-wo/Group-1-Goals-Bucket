package org.pahappa.systems.kpiTracker.views.department;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
// Removed unused SearchField import
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.DEPARTMENT_VIEW)
public class DepartmentView extends PaginatedTableView<Department, DepartmentService, DepartmentService> {
    public DepartmentService departmentService;
    private Department selectedDepartment;
    // Removed unused searchFields and selectedSearchFields variables
    private String searchTerm;

    @PostConstruct
    public void init() {
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

        this.reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search = buildSearch();
        search.setFirstResult(i).setMaxResults(i1);
        List<Department> departments = departmentService.getInstances(search, i, i1);
        
        // Debug: Check if departments have department leads
        for (Department dept : departments) {
            System.out.println("DepartmentView: Department '" + dept.getName() + "' has lead: " + 
                (dept.getDepartmentLead() != null ? dept.getDepartmentLead().getFirstName() + " " + dept.getDepartmentLead().getLastName() : "null"));
        }
        
        super.setDataModels(departments);
    }

    @Override
    public List<Department> load(int first, int pageSize, Map<String, SortMeta> sortBy,
                                 Map<String, FilterMeta> filterBy) {
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
        return getDataModels();
    }

    private Search buildSearch() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        
        // Add search term filter for department name and description
        if (StringUtils.isNotBlank(searchTerm)) {
            search.addFilterOr(
                com.googlecode.genericdao.search.Filter.ilike("name", "%" + searchTerm + "%"),
                com.googlecode.genericdao.search.Filter.ilike("description", "%" + searchTerm + "%")
            );
        }
        
        return search;
    }

    @Override
    public void reloadFilterReset() {
        Search search = buildSearch();
        super.setTotalRecords(departmentService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    /**
     * Deletes the specific Department passed from the UI.
     *
     * @param selectedDepartment The record selected by the user in the data table.
     */
    public void deleteSelectedDepartment(Department selectedDepartment) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedDepartment != null) {
                departmentService.deleteInstance(selectedDepartment);
                MessageComposer.info("Success",
                        "Department '" + selectedDepartment.getName() + "' has been deleted.");
                this.reloadFilterReset();
                // The table refresh will be handled by the 'update' attribute on the button.
            } else {
                // This is a safeguard. It should not happen if the UI is correct.
                MessageComposer.error("Error", "System Error: The record to delete was not provided.");
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    /**
     * Navigate to department detail view
     *
     * @param department The department to view details for
     * @return Navigation string to department detail view
     */
    public String navigateToDepartmentDetail(Department department) {
        if (department != null) {
            // Store the selected department in flash scope for the detail view
            javax.faces.context.FacesContext.getCurrentInstance()
                    .getExternalContext().getFlash().put("selectedDepartmentId", department.getId());

            return HyperLinks.DEPARTMENT_DETAIL_VIEW + "?faces-redirect=true";
        }
        return null;
    }
}