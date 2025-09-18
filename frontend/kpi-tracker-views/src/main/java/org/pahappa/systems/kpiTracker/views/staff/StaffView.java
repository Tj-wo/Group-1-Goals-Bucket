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
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
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
    private String searchTerm;

    @PostConstruct
    public void init() {
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        super.setMaximumresultsPerpage(10);
        this.reloadFilterReset();
    }

    private void buildSearch() {
        this.search = new Search();
        if (StringUtils.isNotBlank(searchTerm)) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.ilike("firstName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("lastName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("emailAddress", "%" + searchTerm + "%")
            );
        }
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
        super.setTotalRecords(staffService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activateAccount(Staff staff) {
        try {
            staffService.activateUserAccount(staff);
            UiUtils.showMessageBox("Success", "User account activated. An email with credentials has been sent.");
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
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public List<Staff> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }
}