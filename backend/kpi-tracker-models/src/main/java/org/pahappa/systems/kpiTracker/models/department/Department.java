package org.pahappa.systems.kpiTracker.models.department;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;
import org.pahappa.systems.kpiTracker.models.staff.Staff;


import javax.persistence.*;


@Setter
@Entity
@Table(name="department" )
public class Department extends BaseEntity {
    //add the team numbers associated to this department
    private static final long serialVersionUID = 6095671201979163425L;
    private String name;
    private Staff departmentLead;
    private String description;

    @Column(length = 100, nullable = false, unique = true) // DB constraints
    public String getName() {
        return name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_lead_staff_id")
    public Staff getDepartmentLead() {
        return departmentLead;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        // If both entities are persisted, compare IDs
        if (!this.isNew() && !that.isNew()) {
            return getId().equals(that.getId());
        }

        // Fallback: compare names for new (transient) entities
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        if (!isNew()) {
            return getId().hashCode();
        }
        return name != null ? name.hashCode() : 0;
    }

}