package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.PasswordResetTokenDao;
import org.pahappa.systems.kpiTracker.core.services.PasswordResetTokenService;
import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.sers.webutils.model.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for PasswordResetToken operations
 */
@Service("passwordResetTokenService")
@Transactional
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);

    @Autowired
    private PasswordResetTokenDao passwordResetTokenDao;

    @Override
    public PasswordResetToken createToken(User user) {
        try {
            // Delete any existing token for this user
            PasswordResetToken existingToken = findByUser(user);
            if (existingToken != null) {
                deleteToken(existingToken);
            }

            // Generate new token
            String token = UUID.randomUUID().toString().replace("-", "");
            PasswordResetToken resetToken = new PasswordResetToken(token, user);

            // Save the token
            PasswordResetToken savedToken = passwordResetTokenDao.save(resetToken);

            logger.info("Created password reset token for user: {}", user.getUsername());
            return savedToken;

        } catch (Exception e) {
            logger.error("Error creating password reset token for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to create password reset token", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetToken findByToken(String token) {
        try {
            return passwordResetTokenDao.findByToken(token);
        } catch (Exception e) {
            logger.error("Error finding password reset token", e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetToken findByUser(User user) {
        try {
            return passwordResetTokenDao.findByUser(user);
        } catch (Exception e) {
            logger.error("Error finding password reset token for user: {}", user.getUsername(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        try {
            PasswordResetToken resetToken = findByToken(token);
            if (resetToken == null) {
                logger.debug("Token not found: {}", token);
                return false;
            }

            if (resetToken.isExpired()) {
                logger.debug("Token expired: {}", token);
                // Delete expired token
                deleteToken(resetToken);
                return false;
            }

            logger.debug("Token is valid: {}", token);
            return true;

        } catch (Exception e) {
            logger.error("Error validating token: {}", token, e);
            return false;
        }
    }

    @Override
    public void deleteToken(PasswordResetToken token) {
        try {
            if (token != null) {
                passwordResetTokenDao.remove(token);
                logger.info("Deleted password reset token for user: {}", token.getUser().getUsername());
            }
        } catch (Exception e) {
            logger.error("Error deleting password reset token", e);
        }
    }

    @Override
    public void deleteExpiredTokens() {
        try {
            passwordResetTokenDao.deleteExpiredTokens();
            logger.info("Deleted expired password reset tokens");
        } catch (Exception e) {
            logger.error("Error deleting expired tokens", e);
        }
    }
}
