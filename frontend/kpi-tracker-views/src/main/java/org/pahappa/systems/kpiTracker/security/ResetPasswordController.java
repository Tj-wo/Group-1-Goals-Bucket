package org.pahappa.systems.kpiTracker.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.pahappa.systems.kpiTracker.core.services.PasswordResetTokenService;
import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling password reset functionality
 */
@ManagedBean(name = "resetPasswordController")
@SessionScoped
public class ResetPasswordController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);

    private String newPassword;
    private String confirmPassword;
    private String resetToken;
    private boolean showSuccessMessage = false;
    private boolean showErrorMessage = false;
    private boolean showInvalidTokenMessage = false;
    private String errorMessage;

    @PostConstruct
    public void init() {
        // Get reset token from URL parameter
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        this.resetToken = request.getParameter("token");

        // Validate token
        if (resetToken == null || resetToken.trim().isEmpty()) {
            showInvalidTokenMessage = true;
        } else {
            validateResetToken();
        }

        resetMessages();
    }

    /**
     * Reset the user's password
     */
    public String resetPassword() {
        try {
            resetMessages();

            // Validate inputs
            if (newPassword == null || newPassword.trim().isEmpty()) {
                showErrorMessage("Please enter a new password.");
                return null;
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                showErrorMessage("Please confirm your new password.");
                return null;
            }

            if (!newPassword.equals(confirmPassword)) {
                showErrorMessage("Passwords do not match.");
                return null;
            }

            if (newPassword.length() < 6) {
                showErrorMessage("Password must be at least 6 characters long.");
                return null;
            }

            // Validate token
            if (resetToken == null || resetToken.trim().isEmpty()) {
                showInvalidTokenMessage = true;
                return null;
            }

            // Find user by reset token
            User user = findUserByResetToken(resetToken);
            if (user == null) {
                showInvalidTokenMessage = true;
                return null;
            }

            // Update password using the proper method that handles hashing
            UserService userService = ApplicationContextProvider.getBean(UserService.class);
            user.setClearTextPassword(newPassword); // This will properly hash the password
            userService.saveUser(user);

            // Delete the used reset token
            PasswordResetTokenService tokenService = ApplicationContextProvider
                    .getBean(PasswordResetTokenService.class);
            PasswordResetToken usedToken = tokenService.findByToken(resetToken);
            if (usedToken != null) {
                tokenService.deleteToken(usedToken);
                logger.info("Deleted used reset token for user: {}", user.getUsername());
            }

            showSuccessMessage = true;
            logger.info("Password reset successful for user: {}", user.getUsername());

            // Clear form
            newPassword = null;
            confirmPassword = null;

        } catch (Exception e) {
            logger.error("Error resetting password", e);
            showErrorMessage("An error occurred while resetting your password. Please try again.");
        }

        return null;
    }

    /**
     * Validate the reset token
     */
    private void validateResetToken() {
        try {
            if (resetToken == null || resetToken.trim().isEmpty()) {
                showInvalidTokenMessage = true;
                return;
            }

            User user = findUserByResetToken(resetToken);
            if (user == null) {
                showInvalidTokenMessage = true;
            }

        } catch (Exception e) {
            logger.error("Error validating reset token", e);
            showInvalidTokenMessage = true;
        }
    }

    /**
     * Find user by reset token using the proper PasswordResetToken service
     */
    private User findUserByResetToken(String token) {
        try {
            PasswordResetTokenService tokenService = ApplicationContextProvider
                    .getBean(PasswordResetTokenService.class);

            // Validate token and get the associated user
            if (tokenService.isTokenValid(token)) {
                PasswordResetToken resetToken = tokenService.findByToken(token);
                if (resetToken != null) {
                    logger.debug("Found valid reset token for user: {}", resetToken.getUser().getUsername());
                    return resetToken.getUser();
                }
            }

            logger.debug("Invalid or expired reset token: {}", token);
            return null;

        } catch (Exception e) {
            logger.error("Error finding user by reset token", e);
            return null;
        }
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        this.errorMessage = message;
        this.showErrorMessage = true;
        this.showSuccessMessage = false;
        this.showInvalidTokenMessage = false;
    }

    /**
     * Reset all messages
     */
    private void resetMessages() {
        this.showSuccessMessage = false;
        this.showErrorMessage = false;
        this.showInvalidTokenMessage = false;
        this.errorMessage = null;
    }

    // Getters and Setters
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean isShowSuccessMessage() {
        return showSuccessMessage;
    }

    public void setShowSuccessMessage(boolean showSuccessMessage) {
        this.showSuccessMessage = showSuccessMessage;
    }

    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }

    public boolean isShowInvalidTokenMessage() {
        return showInvalidTokenMessage;
    }

    public void setShowInvalidTokenMessage(boolean showInvalidTokenMessage) {
        this.showInvalidTokenMessage = showInvalidTokenMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
