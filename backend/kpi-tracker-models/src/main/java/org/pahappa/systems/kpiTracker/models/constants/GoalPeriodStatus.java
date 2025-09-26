package org.pahappa.systems.kpiTracker.models.constants;

public enum GoalPeriodStatus {
    IN_ACTIVE("Not Active"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    private final String displayName;

  GoalPeriodStatus(String displayName) {
        this.displayName = displayName;
    }
}
