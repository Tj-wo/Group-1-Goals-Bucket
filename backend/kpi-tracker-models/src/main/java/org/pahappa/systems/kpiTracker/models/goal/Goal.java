package org.pahappa.systems.kpiTracker.models.goal;

import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "goals", indexes = {
        @Index(name = "idx_goal_level", columnList = "goalLevel"),
        @Index(name = "idx_parent_goal", columnList = "parentGoal_id")
})
public class Goal extends BaseEntity {
    private GoalLevel goalLevel;
    private String goalName;
    private String description;
    private double evaluationTarget;
    private double progress;
    private double weight; // contribution to parent
    private Goal parentGoal;
    private GoalPeriod goalPeriod;
    private GoalStatus goalStatus;
    private Department department;
    private Team team;
    private Staff owner;



    @Enumerated(EnumType.STRING)
    @Column(name = "goal_level", nullable = false)
    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    public void setGoalLevel(GoalLevel goalLevel) {
        this.goalLevel = goalLevel;
    }

    @Column(name = "goal_name", nullable = false)
    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "evaluation_target", precision = 5, scale = 2)
    public double getEvaluationTarget() {
        return evaluationTarget;
    }

    public void setEvaluationTarget(double evaluationTarget) {
        this.evaluationTarget = evaluationTarget;
    }

    @Column(name = "goal_progress")
    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Column(name = "weight", nullable = false)
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @ManyToOne
    @JoinColumn(name = "parent_goal_id")
    public Goal getParentGoal() {
        return parentGoal;
    }

    public void setParentGoal(Goal parentGoal) {
        this.parentGoal = parentGoal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_period_id", nullable = false)
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    public void setGoalPeriod(GoalPeriod goalPeriod) {
        this.goalPeriod = goalPeriod;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_status")
    public GoalStatus getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(GoalStatus goalStatus) {
        this.goalStatus = goalStatus;
    }

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    public Staff getOwner() {
        return owner;
    }

    public void setOwner(Staff owner) {
        this.owner = owner;
    }

    @Transient
    public Date getStartDate() {
        return goalPeriod != null ? goalPeriod.getStartDate() : null;
    }

    @Transient
    public Date getEndDate() {
        return goalPeriod != null ? goalPeriod.getEndDate() : null;
    }

    @Override
    public String toString() {
        return this.goalName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return super.getId() != null && Objects.equals(super.getId(), goal.getId());
    }
}