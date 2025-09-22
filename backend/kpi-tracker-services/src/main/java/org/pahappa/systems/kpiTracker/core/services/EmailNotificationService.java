package org.pahappa.systems.kpiTracker.core.services;

import java.util.List;

/**
 * Service for sending email notifications related to KPI tracking
 */
public interface EmailNotificationService {

    /**
     * Sends a welcome email to a new user
     *
     * @param recipientEmail    The email address of the new user
     * @param userName          The name of the user
     * @param loginUrl          The login URL for the application
     * @param temporaryPassword The temporary password for first login
     */
    void sendWelcomeEmail(String recipientEmail, String userName, String loginUrl, String temporaryPassword);

    /**
     * Sends a KPI goal reminder email
     *
     * @param recipientEmail     The email address of the user
     * @param userName           The name of the user
     * @param goalName           The name of the KPI goal
     * @param dueDate            The due date of the goal
     * @param progressPercentage The current progress percentage
     */
    void sendKpiReminderEmail(String recipientEmail, String userName, String goalName, String dueDate,
                              double progressPercentage);

    /**
     * Sends a KPI goal completion email
     *
     * @param recipientEmail The email address of the user
     * @param userName       The name of the user
     * @param goalName       The name of the completed KPI goal
     * @param completionDate The completion date
     */
    void sendKpiCompletionEmail(String recipientEmail, String userName, String goalName, String completionDate);

    /**
     * Sends a KPI goal overdue email
     *
     * @param recipientEmail The email address of the user
     * @param userName       The name of the user
     * @param goalName       The name of the overdue KPI goal
     * @param dueDate        The due date that was missed
     * @param daysOverdue    The number of days overdue
     */
    void sendKpiOverdueEmail(String recipientEmail, String userName, String goalName, String dueDate, int daysOverdue);

    /**
     * Sends a password reset email
     *
     * @param recipientEmail The email address of the user
     * @param userName       The name of the user
     * @param resetUrl       The password reset URL
     * @param expirationTime The expiration time for the reset link
     */
    void sendPasswordResetEmail(String recipientEmail, String userName, String resetUrl, String expirationTime);

    /**
     * Sends bulk KPI reminder emails to multiple users
     *
     * @param recipientEmails    List of email addresses
     * @param userName           The name of the user (for personalization)
     * @param goalName           The name of the KPI goal
     * @param dueDate            The due date of the goal
     * @param progressPercentage The current progress percentage
     */
    void sendBulkKpiReminderEmails(List<String> recipientEmails, String userName, String goalName, String dueDate,
                                   double progressPercentage);

    /**
     * Sends a test email to verify email configuration
     *
     * @param recipientEmail The email address to send the test email to
     * @return true if email was sent successfully, false otherwise
     */
    boolean sendTestEmail(String recipientEmail);
}
