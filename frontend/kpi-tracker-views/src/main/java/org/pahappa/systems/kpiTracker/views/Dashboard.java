package org.pahappa.systems.kpiTracker.views;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.controllers.WebAppExceptionHandler;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.io.IOException;
import java.io.Serializable;


@ManagedBean(name = "dashboard")
@ViewScoped
@ViewPath(path = HyperLinks.DASHBOARD)
public class Dashboard extends WebAppExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private User loggedinUser;

    Search search = new Search();
    private String searchTerm;
    private SortField selectedSortField;

    @SuppressWarnings("unused")
    private String viewPath;

    @PostConstruct
    public void init() {
        loggedinUser = SharedAppData.getLoggedInUser();
        if (loggedinUser != null) {
            // Get the StaffService from the application context
            StaffService staffService = ApplicationContextProvider.getBean(StaffService.class);

            // Find the staff record associated with the logged-in user
            Staff staff = staffService.getStaffByUser(loggedinUser);

            // Check if the staff member must change their password
            if (staff != null && staff.isFirstLogin()) {
                try {
                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                    ec.redirect(ec.getRequestContextPath() + HyperLinks.FORCE_PASSWORD_CHANGE_FORM);
                } catch (IOException e) {
                    // Log the exception for troubleshooting
                    e.printStackTrace();
                }
            }
        }
    }

    public User getLoggedinUser() {
        return loggedinUser;
    }

    public void setLoggedinUser(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }

    /**
     * @return the viewPath
     */
    public String getViewPath() {
        return Dashboard.class.getAnnotation(ViewPath.class).path();
    }

    /**
     * @param viewPath the viewPath to set
     */
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public SortField getSelectedSortField() {
        return selectedSortField;
    }

    public void setSelectedSortField(SortField selectedSortField) {
        this.selectedSortField = selectedSortField;
    }

}
