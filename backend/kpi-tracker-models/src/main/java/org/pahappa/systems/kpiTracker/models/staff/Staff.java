package org.pahappa.systems.kpiTracker.models.staff;

import org.pahappa.systems.kpiTracker.constants.StaffStatus;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.security.User;
import javax.persistence.*;
import org.sers.webutils.model.BaseEntity;

import java.util.Objects;

@Entity
@Table(name = "staff")
public class Staff extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private StaffStatus staffStatus;
    private User userAccount;
    private boolean isActive = true; // Default to active

    // These fields seem redundant if User already has them,
    // but we'll keep them if they are required separately.
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private Gender gender;

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

    @Column(name = "is_active")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email_address", unique = true)
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }


    @Column(name = "is_first_login")
    private Boolean firstLogin = true;

    public Boolean isFirstLogin() {
        // Defensive check to prevent NullPointerException, defaulting to false if null.
        return firstLogin != null && firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    @Transient
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            fullName.append(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }
        return fullName.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return staffStatus == staff.staffStatus &&
               Objects.equals(userAccount, staff.userAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffStatus, userAccount);
    }
}
