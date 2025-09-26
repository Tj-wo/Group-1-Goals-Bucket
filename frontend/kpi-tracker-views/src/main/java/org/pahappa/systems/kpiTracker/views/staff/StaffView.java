package org.pahappa.systems.kpiTracker.views.staff;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus; // Import the RecordStatus enum
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "staffView")
@ViewScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.STAFF_VIEW)
public class StaffView extends PaginatedTableView<Staff, StaffService, StaffService> {

    private static final long serialVersionUID = 1L;
    private StaffService staffService;
    private Search search;

    // Filters Properties
    private String searchTerm;
    private Gender selectedGender;
    private Date createdFrom;
    private Date createdTo;
    private List<Gender> genders;

    // Statistics Properties
    private long totalStaff;
    private long maleStaff;
    private long femaleStaff;
    private long activeStaff;

    @PostConstruct
    public void init() {
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        super.setMaximumresultsPerpage(10);
        this.genders = Arrays.asList(Gender.values());
        this.reloadFilterReset();
    }

    private void buildSearch() {
        this.search = new Search();
        // *** FIX 1: Use the RecordStatus enum instead of the string "ACTIVE" ***
        this.search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (StringUtils.isNotBlank(searchTerm)) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.ilike("firstName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("lastName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("emailAddress", "%" + searchTerm + "%")
            );
        }

        if (this.selectedGender != null) {
            this.search.addFilterEqual("gender", this.selectedGender);
        }

        if (this.createdFrom != null) {
            this.search.addFilterGreaterOrEqual("dateCreated", this.createdFrom);
        }
        if (this.createdTo != null) {
            this.search.addFilterLessOrEqual("dateCreated", this.createdTo);
        }
    }

    private void updateStatistics() {
        // *** FIX 2: Corrected the logic to calculate stats for all active staff ***
        Search statsSearch = new Search(Staff.class);
        statsSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.totalStaff = staffService.countInstances(statsSearch);

        statsSearch.clearFilters(); // Clear previous filters before adding new ones
        statsSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        statsSearch.addFilterEqual("gender", Gender.MALE);
        this.maleStaff = staffService.countInstances(statsSearch);

        statsSearch.clearFilters();
        statsSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        statsSearch.addFilterEqual("gender", Gender.FEMALE);
        this.femaleStaff = staffService.countInstances(statsSearch);
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        buildSearch();
        this.search.setFirstResult(offset).setMaxResults(limit);
        super.setDataModels(staffService.getInstances(search, offset, limit));
    }

    @Override
    public void reloadFilterReset() {
        buildSearch();
        updateStatistics();
        super.setTotalRecords(staffService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStaff(Staff staff) {
        try {
            // Assuming your service has a method to properly delete or mark as deleted
            this.staffService.deleteInstance(staff);
            UiUtils.showMessageBox("Success", "Staff member has been deleted.");
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    public void activateAccount(Staff staff) {
        try {
            staffService.activateUserAccount(staff);
            UiUtils.showMessageBox("Success", "User account activated.");
            reloadFilterReset();
        } catch (ValidationFailedException | OperationFailedException e) {
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    public void deactivateAccount(Staff staff) {
        try {
            staffService.deactivateUserAccount(staff);
            UiUtils.showMessageBox("Success", "User account has been deactivated.");
            reloadFilterReset();
        } catch (ValidationFailedException | OperationFailedException e) {
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return Collections.emptyList(); }

    @Override
    public String getFileName() { return null; }

    @Override
    public List<Staff> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // This should delegate to reloadFromDB for lazy loading to work correctly
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDataModels();
    }
}