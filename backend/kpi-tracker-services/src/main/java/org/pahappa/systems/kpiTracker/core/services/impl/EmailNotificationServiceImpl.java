package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.EmailNotificationService;
import org.pahappa.systems.kpiTracker.core.services.EmailTemplateService;
import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of email notification service for KPI tracking
 */
@Service("emailNotificationService")
@Transactional
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;

    @Autowired
    public EmailNotificationServiceImpl(MailService mailService, EmailTemplateService emailTemplateService) {
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public void sendWelcomeEmail(String recipientEmail, String userName, String loginUrl, String temporaryPassword) {
        try {
            String subject = "Welcome to KPI Tracker - Your Account is Ready!";
            String htmlBody = emailTemplateService.generateWelcomeEmail(userName, loginUrl, temporaryPassword,
                    recipientEmail);

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Welcome email sent successfully to: " + recipientEmail);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send welcome email to " + recipientEmail + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendKpiReminderEmail(String recipientEmail, String userName, String goalName, String dueDate,
            double progressPercentage) {
        try {
            String subject = "KPI Goal Reminder - " + goalName;
            String htmlBody = emailTemplateService.generateKpiReminderEmail(userName, goalName, dueDate,
                    progressPercentage);

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "KPI reminder email sent successfully to: " + recipientEmail + " for goal: " + goalName);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send KPI reminder email to " + recipientEmail + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send KPI reminder email", e);
        }
    }

    @Override
    public void sendKpiCompletionEmail(String recipientEmail, String userName, String goalName, String completionDate) {
        try {
            String subject = "🎉 Congratulations! KPI Goal Completed - " + goalName;
            String htmlBody = emailTemplateService.generateKpiCompletionEmail(userName, goalName, completionDate);

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "KPI completion email sent successfully to: " + recipientEmail + " for goal: " + goalName);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send KPI completion email to " + recipientEmail + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send KPI completion email", e);
        }
    }

    @Override
    public void sendKpiOverdueEmail(String recipientEmail, String userName, String goalName, String dueDate,
            int daysOverdue) {
        try {
            String subject = "⚠️ URGENT: KPI Goal Overdue - " + goalName;
            String htmlBody = emailTemplateService.generateKpiOverdueEmail(userName, goalName, dueDate, daysOverdue);

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "KPI overdue email sent successfully to: " + recipientEmail + " for goal: " + goalName);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send KPI overdue email to " + recipientEmail + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send KPI overdue email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String userName, String resetUrl, String expirationTime) {
        try {
            String subject = "Password Reset Request - KPI Tracker";
            String htmlBody = emailTemplateService.generatePasswordResetEmail(userName, resetUrl, expirationTime);

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Password reset email sent successfully to: " + recipientEmail);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send password reset email to " + recipientEmail + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendBulkKpiReminderEmails(List<String> recipientEmails, String userName, String goalName,
            String dueDate, double progressPercentage) {
        try {
            String subject = "KPI Goal Reminder - " + goalName;
            String htmlBody = emailTemplateService.generateKpiReminderEmail(userName, goalName, dueDate,
                    progressPercentage);

            mailService.sendHtmlMail(recipientEmails, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Bulk KPI reminder emails sent successfully to " + recipientEmails.size() + " recipients for goal: "
                            + goalName);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send bulk KPI reminder emails. Error: " + e.getMessage());
            throw new RuntimeException("Failed to send bulk KPI reminder emails", e);
        }
    }

    @Override
    public boolean sendTestEmail(String recipientEmail) {
        try {
            String subject = "Test Email - KPI Tracker Email Configuration";
            String htmlBody = String.format(
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset=\"UTF-8\">" +
                            "<title>Test Email</title>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                            ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                            ".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }" +
                            ".content { padding: 20px; background-color: #f9f9f9; }" +
                            ".success { background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; margin: 15px 0; border-radius: 5px; }"
                            +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class=\"container\">" +
                            "<div class=\"header\">" +
                            "<h1>✅ Test Email Successful</h1>" +
                            "</div>" +
                            "<div class=\"content\">" +
                            "<div class=\"success\">" +
                            "<p><strong>Congratulations!</strong> Your email configuration is working correctly.</p>" +
                            "</div>" +
                            "<p>This is a test email to verify that your KPI Tracker email system is properly configured.</p>"
                            +
                            "<p><strong>Email Provider:</strong> AWS SES</p>" +
                            "<p><strong>Timestamp:</strong> %s</p>" +
                            "<p>If you received this email, your email notifications are ready to use!</p>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>",
                    java.time.LocalDateTime.now());

            mailService.sendHtmlMail(recipientEmail, subject, htmlBody, null);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Test email sent successfully to: " + recipientEmail);
            return true;

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send test email to " + recipientEmail + ". Error: " + e.getMessage());
            return false;
        }
    }
}
