package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.sers.webutils.model.security.User;

/**
 * Service interface for PasswordResetToken operations
 */
public interface PasswordResetTokenService {

    /**
     * Create a new password reset token for a user
     */
    PasswordResetToken createToken(User user);

    /**
     * Find a password reset token by token string
     */
    PasswordResetToken findByToken(String token);

    /**
     * Find a password reset token by user
     */
    PasswordResetToken findByUser(User user);

    /**
     * Validate if a token is valid and not expired
     */
    boolean isTokenValid(String token);

    /**
     * Delete a password reset token
     */
    void deleteToken(PasswordResetToken token);

    /**
     * Delete expired tokens
     */
    void deleteExpiredTokens();
}
