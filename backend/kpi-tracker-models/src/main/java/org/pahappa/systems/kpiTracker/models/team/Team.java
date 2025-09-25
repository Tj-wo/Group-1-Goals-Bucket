package org.pahappa.systems.kpiTracker.models.team;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;
import org.pahappa.systems.kpiTracker.models.staff.Staff;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String teamName;
    private Staff teamLead;
    private String description;
    private Department department;

    public Team() {
        // Initialize required BaseEntity fields
        this.setRecordStatus(org.sers.webutils.model.RecordStatus.ACTIVE);
    }

    @Column(name = "team_name", nullable = false, unique = true)
    public String getTeamName() {
        return teamName;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "team_lead_id")
    public Staff getTeamLead() {
        return teamLead;
    }

    @Column(name = "description", length = 500)
    public String getDescription() {
        return description;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return this.teamName;
    }

    @Override
    public int hashCode() {
        // Persisted entities are identified by their ID
        return Objects.hash(super.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Team team = (Team) o;
        return super.getId() != null && Objects.equals(super.getId(), team.getId());
    }
}
