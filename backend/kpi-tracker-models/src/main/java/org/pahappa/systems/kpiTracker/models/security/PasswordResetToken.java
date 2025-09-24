package org.pahappa.systems.kpiTracker.models.security;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;
import lombok.Setter;

/**
 * Entity for storing password reset tokens
 */
@Setter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private static final int EXPIRATION = 60 * 24; // 24 hours in minutes

    private String token;
    private User user;
    private Date expiryDate;

    public PasswordResetToken() {
        super();
    }

    public PasswordResetToken(String token, User user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Date now = new Date();
        return new Date(now.getTime() + expiryTimeInMinutes * 60 * 1000);
    }

    @Column(name = "token", nullable = false, unique = true)
    public String getToken() {
        return token;
    }

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    public User getUser() {
        return user;
    }

    @Column(name = "expiry_date", nullable = false)
    public Date getExpiryDate() {
        return expiryDate;
    }

    @Transient
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        PasswordResetToken that = (PasswordResetToken) o;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
