package org.pahappa.systems.kpiTracker.models.team;

import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "team_members")
@Setter
public class TeamMember extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Team team;
    private Staff staff;
    private String role; // Optional: role within the team (e.g., "Member", "Senior Member", "Lead")

    public TeamMember() {
        // Initialize required BaseEntity fields
        this.setRecordStatus(org.sers.webutils.model.RecordStatus.ACTIVE);
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    public Team getTeam() {
        return team;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false)
    public Staff getStaff() {
        return staff;
    }

    @Column(name = "role", length = 100)
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return staff != null ? staff.getFirstName() + " " + staff.getLastName() + " (" + team.getTeamName() + ")"
                : "TeamMember";
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
        TeamMember teamMember = (TeamMember) o;
        return super.getId() != null && Objects.equals(super.getId(), team.getId());
    }
}

