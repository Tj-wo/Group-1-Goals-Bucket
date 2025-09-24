package org.pahappa.systems.kpiTracker.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.pahappa.systems.kpiTracker.core.services.PasswordResetTokenService;
import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling forgot password functionality
 */
@ManagedBean(name = "forgotPasswordController")
@SessionScoped
public class ForgotPasswordController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    private String email;
    private boolean showSuccessMessage = false;
    private boolean showErrorMessage = false;
    private String errorMessage;

    @PostConstruct
    public void init() {
        // Initialize controller
        resetMessages();
    }

    /**
     * Send password reset email to the user
     */
    public String sendResetEmail() {
        try {
            resetMessages();

            if (email == null || email.trim().isEmpty()) {
                showErrorMessage("Please enter your email address.");
                return null;
            }

            // Get services
            UserService userService = ApplicationContextProvider.getBean(UserService.class);
            MailService mailService = ApplicationContextProvider.getBean(MailService.class);

            // Find user by email - try different approaches
            User user = null;

            // First try to find by username (email might be used as username)
            try {
                user = userService.getUserByUsername(email.trim());
            } catch (Exception e) {
                logger.debug("getUserByUsername failed, trying alternative approach");
            }

            // If not found, try to find by staff email
            if (user == null) {
                Staff staff = findStaffByEmail(email.trim());
                if (staff != null && staff.getUserAccount() != null) {
                    user = staff.getUserAccount();
                }
            }

            if (user == null) {
                showErrorMessage("No account found with this email address.");
                return null;
            }

            // Generate and save reset token using the proper service
            PasswordResetTokenService tokenService = ApplicationContextProvider
                    .getBean(PasswordResetTokenService.class);
            PasswordResetToken resetToken = tokenService.createToken(user);
            String tokenString = resetToken.getToken();
            logger.info("Generated and saved reset token: {}", tokenString);

            // Send reset email
            String resetLink = buildResetLink(tokenString);
            sendPasswordResetEmail(user.getUsername(), email.trim(), resetLink, mailService);

            showSuccessMessage = true;
            logger.info("Password reset email sent to: {}", email);

            // Clear the email field after successful submission
            email = null;

        } catch (Exception e) {
            logger.error("Error sending password reset email", e);
            showErrorMessage("An error occurred while sending the reset email. Please try again.");
        }

        return null;
    }

    /**
     * Find staff by email address
     */
    private Staff findStaffByEmail(String email) {
        try {
            // For now, we'll return null since there's no direct method to find staff by
            // email
            // In production, you should add a method to StaffService to find staff by email
            logger.debug("Looking for staff with email: {}", email);
            return null;
        } catch (Exception e) {
            logger.error("Error finding staff by email", e);
            return null;
        }
    }

    /**
     * Build the password reset link
     */
    private String buildResetLink(String resetToken) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        String baseUrl = request.getScheme() + "://" + request.getServerName();

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        baseUrl += request.getContextPath();

        return baseUrl + "/ExternalViews/ResetPassword.xhtml?token=" + resetToken;
    }

    /**
     * Send password reset email
     */
    private void sendPasswordResetEmail(String username, String email, String resetLink, MailService mailService) {
        try {
            String subject = "Password Reset Request - KPI Tracker";
            String htmlBody = buildEmailBody(username, resetLink);
            String textBody = buildTextEmailBody(username, resetLink);

            // Send HTML email using your mail service
            mailService.sendHtmlMail(email, subject, htmlBody, textBody);

        } catch (Exception e) {
            logger.error("Error sending email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Build the plain text email body
     */
    private String buildTextEmailBody(String username, String resetLink) {
        return String.format(
                "Password Reset Request\n\n" +
                        "Hello %s,\n\n" +
                        "You have requested to reset your password for the KPI Tracker system.\n\n" +
                        "To reset your password, please click the following link:\n" +
                        "%s\n\n" +
                        "Important: This link will expire in 24 hours for security reasons.\n\n" +
                        "If you didn't request this password reset, please ignore this email.\n\n" +
                        "This is an automated message from KPI Tracker System.\n" +
                        "Please do not reply to this email.",
                username, resetLink);
    }

    /**
     * Build the HTML email body
     */
    private String buildEmailBody(String username, String resetLink) {
        return String.format(
                "<html>" +
                        "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                        "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                        "<h2 style=\"color: #2c3e50;\">Password Reset Request</h2>" +
                        "<p>Hello %s,</p>" +
                        "<p>You have requested to reset your password for the KPI Tracker system.</p>" +
                        "<p>Click the button below to reset your password:</p>" +
                        "<div style=\"text-align: center; margin: 30px 0;\">" +
                        "<a href=\"%s\" style=\"background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; display: inline-block; font-weight: bold;\">"
                        +
                        "Reset Password" +
                        "</a>" +
                        "</div>" +
                        "<p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                        "<p style=\"word-break: break-all; color: #667eea;\">%s</p>" +
                        "<p><strong>Important:</strong> This link will expire in 24 hours for security reasons.</p>" +
                        "<p>If you didn't request this password reset, please ignore this email.</p>" +
                        "<hr style=\"border: none; border-top: 1px solid #eee; margin: 30px 0;\">" +
                        "<p style=\"color: #7f8c8d; font-size: 14px;\">" +
                        "This is an automated message from KPI Tracker System.<br>" +
                        "Please do not reply to this email." +
                        "</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                username, resetLink, resetLink);
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        this.errorMessage = message;
        this.showErrorMessage = true;
        this.showSuccessMessage = false;
    }

    /**
     * Reset all messages
     */
    private void resetMessages() {
        this.showSuccessMessage = false;
        this.showErrorMessage = false;
        this.errorMessage = null;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
