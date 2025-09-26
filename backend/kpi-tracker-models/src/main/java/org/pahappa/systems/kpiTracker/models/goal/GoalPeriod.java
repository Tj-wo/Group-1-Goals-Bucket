package org.pahappa.systems.kpiTracker.models.goal;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "goal_periods")
public class GoalPeriod extends BaseEntity {
    private String periodName;
    private Date startDate;
    private Date endDate;
    private double  businessGoalContribution;
    private  double  organisationalFitScore;


    @Column(name="managementByObjective")
    public double getBusinessGoalContribution() {
        return businessGoalContribution;
    }
    @Column(name="organisationFit")
    public double getOrganisationalFitScore() {
        return organisationalFitScore;
    }

    @Column(name = "name", nullable = false)
    public String getPeriodName() {
        return periodName;
    }

    @Column(name = "end_date", nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    @Column(name = "start_date", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GoalPeriod that = (GoalPeriod) o;
        // Two GoalPeriods are equal only if their IDs are equal
        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        // The hashcode should be based on the ID
        return getId() != null ? getId().hashCode() : 0;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setBusinessGoalContribution(double businessGoalContribution) {
        this.businessGoalContribution = businessGoalContribution;
    }

    public void setOrganisationalFitScore(double organisationalFitScore) {
        this.organisationalFitScore = organisationalFitScore;
    }
}
