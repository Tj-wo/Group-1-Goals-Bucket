package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.sers.webutils.model.security.User;

/**
 * DAO interface for PasswordResetToken operations
 */
public interface PasswordResetTokenDao extends BaseDao<PasswordResetToken> {

    /**
     * Find a password reset token by token string
     */
    PasswordResetToken findByToken(String token);

    /**
     * Find a password reset token by user
     */
    PasswordResetToken findByUser(User user);

    /**
     * Delete expired tokens
     */
    void deleteExpiredTokens();
}
