package org.pahappa.systems.kpiTracker.models.staff;

import org.pahappa.systems.kpiTracker.models.constants.StaffStatus;
import org.sers.webutils.model.security.User;
import javax.persistence.*;
import org.sers.webutils.model.BaseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "staff")
public class Staff extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private StaffStatus staffStatus;
    private User userAccount;


    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "user_id")
    public User getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(User userAccount) {
        this.userAccount = userAccount;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_status")
    public StaffStatus getStaffStatus() {
        return staffStatus;
    }

    public void setStaffStatus(StaffStatus staffStatus) {
        this.staffStatus = staffStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return staffStatus == staff.staffStatus && Objects.equals(userAccount, staff.userAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffStatus, userAccount);
    }
}