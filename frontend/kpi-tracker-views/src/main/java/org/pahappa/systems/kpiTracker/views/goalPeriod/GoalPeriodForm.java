package org.pahappa.systems.kpiTracker.views.goalPeriod;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.models.constants.GoalPeriodStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.GoalPeriod;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import com.googlecode.genericdao.search.Search;
import org.sers.webutils.model.RecordStatus;
import java.util.List;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@Setter
@Getter
@ManagedBean(name = "goalPeriodForm")
@SessionScoped
@ViewPath(path = HyperLinks.GOAL_PERIOD_FORM)
public class GoalPeriodForm extends DialogForm<GoalPeriod> {

    private static final long serialVersionUID = 1L;
    private boolean edit;
    private List<GoalPeriodStatus> availableStatuses;

    private transient GoalPeriodService goalPeriodService;

    public GoalPeriodForm() {
        super(HyperLinks.GOAL_PERIOD_FORM, 700, 450);
    }

    @PostConstruct
    public void init() {
        this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);
        super.model = new GoalPeriod();
        // Initialize dates as null to keep datepicker empty
        super.model.setStartDate(null);
        super.model.setEndDate(null);
        this.availableStatuses = Arrays.asList(GoalPeriodStatus.values());
        System.out.println("Init - End Date: "
                + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null"));
    }

    @Override
    public void persist() throws Exception {
        // Validate required fields
        if (super.model.getPeriodName() == null || super.model.getPeriodName().trim().isEmpty()) {
            throw new ValidationFailedException("Period name is required");
        }

        if (super.model.getStartDate() == null) {
            throw new ValidationFailedException("Start date is required");
        }

        if (super.model.getEndDate() == null) {
            throw new ValidationFailedException("End date is required");
        }

        // Validate date logic
        if (super.model.getEndDate().before(super.model.getStartDate())) {
            throw new ValidationFailedException("End date must be after start date");
        }

        // Validate the sum of MBO and Org Fit weights as doubles
        double mboWeight = super.model.getBusinessGoalContribution(); // Defaults to 0.0 if not set
        double orgFitWeight = super.model.getOrganisationalFitScore(); // Defaults to 0.0 if not set
        double totalWeight = mboWeight + orgFitWeight;

        // Use a small epsilon to handle floating-point precision
        final double EPSILON = 0.001;
        if (Math.abs(totalWeight - 100.0) > EPSILON && totalWeight > 100.0) {
            throw new ValidationFailedException(
                    String.format(
                            "The sum of MBO Weight and Org Fit Weight must not exceed 100.0%%. Current sum: %.2f%%. Please correct the values.",
                            totalWeight));
        }

        // Set default status to IN_ACTIVE for new goal periods
        if (super.model.getStatus() == null) {
            super.model.setStatus(GoalPeriodStatus.IN_ACTIVE);
        }

        // Check if this goal period is being set to ACTIVE
        if (super.model.getStatus() == GoalPeriodStatus.ACTIVE) {
            // Check if there's already an active goal period
            Search search = new Search(GoalPeriod.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterEqual("status", GoalPeriodStatus.ACTIVE);

            // If editing, exclude the current goal period from the search
            if (super.model.getId() != null) {
                search.addFilterNotEqual("id", super.model.getId());
            }

            List<GoalPeriod> existingActivePeriods = this.goalPeriodService.getInstances(search, 0, 1);
            if (!existingActivePeriods.isEmpty()) {
                GoalPeriod existingActive = existingActivePeriods.get(0);
                throw new ValidationFailedException(
                        String.format("Cannot activate this goal period. '%s' is currently active. " +
                                "Please deactivate the current active period first.",
                                existingActive.getPeriodName()));
            }
        }

        this.goalPeriodService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new GoalPeriod();
        // Keep dates null to ensure empty datepicker
        super.model.setStartDate(null);
        super.model.setEndDate(null);
        System.out.println("Reset - End Date: "
                + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null"));
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new GoalPeriod();
            }
            setEdit(false);
            // Ensure dates are null for new forms
            super.model.setStartDate(null);
            super.model.setEndDate(null);
        }
    }

    // Debugging method to check selected date
    public void printDate() {
        String message = "End Date: "
                + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null");
        System.out.println(message);
    }

    /**
     * Get the current active goal period
     */
    public GoalPeriod getCurrentActivePeriod() {
        try {
            Search search = new Search(GoalPeriod.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterEqual("status", GoalPeriodStatus.ACTIVE);

            List<GoalPeriod> activePeriods = this.goalPeriodService.getInstances(search, 0, 1);
            return activePeriods.isEmpty() ? null : activePeriods.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if there's already an active goal period
     */
    public boolean hasActivePeriod() {
        return getCurrentActivePeriod() != null;
    }

    /**
     * Get the name of the currently active period
     */
    public String getActivePeriodName() {
        GoalPeriod active = getCurrentActivePeriod();
        return active != null ? active.getPeriodName() : "None";
    }
}