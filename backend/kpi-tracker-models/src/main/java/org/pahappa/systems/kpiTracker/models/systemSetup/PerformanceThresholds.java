package org.pahappa.systems.kpiTracker.models.systemSetup;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;


@Setter
@Entity
@Table(name = "performance_thresholds")
@Inheritance(strategy = InheritanceType.JOINED)
public class PerformanceThresholds extends BaseEntity {

    private double exceptional;
    private double outstanding;
    private double meetsExpectations;
    private double needsImprovement;

    @Column(name = "exceptional", nullable = false)
    public double getExceptional() {
        return exceptional;
    }


    @Column(name = "outstanding", nullable = false)
    public double getOutstanding() {
        return outstanding;
    }

    @Column(name = "meets-expectation", nullable = false)
    public double getMeetsExpectations() {
        return meetsExpectations;
    }
    @Column(name = "need-improvement", nullable = false)
    public double getNeedsImprovement() {
        return needsImprovement;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PerformanceThresholds && (super.getId() != null)
                ? super.getId().equals(((PerformanceThresholds) object).getId())
                : (object == this);
    }

    @Override
    public int hashCode() {
        return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
    }
}