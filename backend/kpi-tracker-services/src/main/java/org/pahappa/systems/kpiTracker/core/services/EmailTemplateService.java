package org.pahappa.systems.kpiTracker.core.services;

import java.util.Map;

/**
 * Service for managing email templates for KPI notifications
 */
public interface EmailTemplateService {

    /**
     * Generates a welcome email template for new users
     *
     * @param userName          The name of the user
     * @param loginUrl          The login URL for the application
     * @param temporaryPassword The temporary password for first login
     * @param emailAddress      The email address for login
     * @return HTML email template
     */
    String generateWelcomeEmail(String userName, String loginUrl, String temporaryPassword, String emailAddress);

    /**
     * Generates a KPI goal reminder email template
     *
     * @param userName           The name of the user
     * @param goalName           The name of the KPI goal
     * @param dueDate            The due date of the goal
     * @param progressPercentage The current progress percentage
     * @return HTML email template
     */
    String generateKpiReminderEmail(String userName, String goalName, String dueDate, double progressPercentage);

    /**
     * Generates a KPI goal completion email template
     *
     * @param userName       The name of the user
     * @param goalName       The name of the completed KPI goal
     * @param completionDate The completion date
     * @return HTML email template
     */
    String generateKpiCompletionEmail(String userName, String goalName, String completionDate);

    /**
     * Generates a KPI goal overdue email template
     *
     * @param userName    The name of the user
     * @param goalName    The name of the overdue KPI goal
     * @param dueDate     The due date that was missed
     * @param daysOverdue The number of days overdue
     * @return HTML email template
     */
    String generateKpiOverdueEmail(String userName, String goalName, String dueDate, int daysOverdue);

    /**
     * Generates a password reset email template
     *
     * @param userName       The name of the user
     * @param resetUrl       The password reset URL
     * @param expirationTime The expiration time for the reset link
     * @return HTML email template
     */
    String generatePasswordResetEmail(String userName, String resetUrl, String expirationTime);

    /**
     * Generates a custom email template with placeholders
     *
     * @param templateName The name of the template
     * @param placeholders Map of placeholder values
     * @return HTML email template
     */
    String generateCustomEmail(String templateName, Map<String, String> placeholders);
}
