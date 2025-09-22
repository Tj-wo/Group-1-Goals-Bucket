package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.EmailTemplateService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of email template service for KPI notifications
 */
@Service("emailTemplateService")
public class EmailTemplateServiceImpl implements EmailTemplateService {

        private static final String COMPANY_NAME = "Pahappa Systems";
        private static final String APP_NAME = "KPI Tracker";

        @Override
        public String generateWelcomeEmail(String userName, String loginUrl, String temporaryPassword,
                        String emailAddress) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "<meta charset=\"UTF-8\">" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                                +
                                                "<title>Welcome to %s</title>" +
                                                "<style>" +
                                                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                                                +
                                                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                                                ".header { background-color:#07306C; color: white; padding: 20px; text-align: center; }"
                                                +
                                                ".content { padding: 20px; background-color: #f9f9f9; }" +
                                                ".button { display: inline-block; padding: 12px 24px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }"
                                                +
                                                ".credentials-box { background-color: #e8f4fd; border: 2px solid #3498db; border-radius: 5px; padding: 20px; margin: 15px 0; }"
                                                +
                                                ".credential-item { margin: 10px 0; padding: 8px; background-color: #f8f9fa; border-radius: 3px; }"
                                                +
                                                ".credential-label { font-weight: bold; color: #2c3e50; display: inline-block; width: 120px; }"
                                                +
                                                ".credential-value { font-family: 'Courier New', monospace; font-size: 16px; color: #2c3e50; }"
                                                +
                                                ".password-value { font-family: 'Courier New', monospace; font-size: 18px; font-weight: bold; color: #e74c3c; letter-spacing: 1px; }"
                                                +
                                                ".security-note { background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 10px; margin: 15px 0; color: #856404; }"
                                                +
                                                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
                                                +
                                                "</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "<div class=\"container\">" +
                                                "<div class=\"header\">" +
                                                "<h1>Welcome to %s</h1>" +
                                                "</div>" +
                                                "<div class=\"content\">" +
                                                "<h2>Hello %s!</h2>" +
                                                "<p>Welcome to %s! We're excited to have you on board.</p>" +
                                                "<p>Your account has been successfully created and you can now start tracking your KPIs and goals.</p>"
                                                +
                                                "<div class=\"credentials-box\">" +
                                                "<h3>🔐 Your Login Credentials</h3>" +
                                                "<div class=\"credential-item\">" +
                                                "<span class=\"credential-label\">Email:</span>" +
                                                "<span class=\"credential-value\">%s</span>" +
                                                "</div>" +
                                                "<div class=\"credential-item\">" +
                                                "<span class=\"credential-label\">Password:</span>" +
                                                "<span class=\"password-value\">%s</span>" +
                                                "</div>" +
                                                "</div>" +
                                                "<div class=\"security-note\">" +
                                                "<p><strong>🔒 Security Notice:</strong> This is a temporary password. Please change it immediately after your first login for security reasons.</p>"
                                                +
                                                "</div>" +
                                                "<p>To get started, please click the button below to log in to your account:</p>"
                                                +
                                                "<a href=\"%s\" class=\"button\">Login to %s</a>" +
                                                "<p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>"
                                                +
                                                "</div>" +
                                                "<div class=\"footer\">" +
                                                "<p>&copy; 2024 %s. All rights reserved.</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                APP_NAME, APP_NAME, userName, APP_NAME, emailAddress, temporaryPassword, loginUrl,
                                APP_NAME, COMPANY_NAME);
        }

        @Override
        public String generateKpiReminderEmail(String userName, String goalName, String dueDate,
                        double progressPercentage) {
                String progressColor = progressPercentage >= 80 ? "#27ae60"
                                : progressPercentage >= 50 ? "#f39c12" : "#e74c3c";

                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "<meta charset=\"UTF-8\">" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                                +
                                                "<title>KPI Goal Reminder</title>" +
                                                "<style>" +
                                                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                                                +
                                                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                                                ".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }"
                                                +
                                                ".content { padding: 20px; background-color: #f9f9f9; }" +
                                                ".progress-bar { width: 100%%; background-color: #ecf0f1; border-radius: 10px; overflow: hidden; margin: 10px 0; }"
                                                +
                                                ".progress-fill { height: 20px; background-color: %s; transition: width 0.3s ease; }"
                                                +
                                                ".progress-text { text-align: center; margin-top: 5px; font-weight: bold; }"
                                                +
                                                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
                                                +
                                                "</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "<div class=\"container\">" +
                                                "<div class=\"header\">" +
                                                "<h1>KPI Goal Reminder</h1>" +
                                                "</div>" +
                                                "<div class=\"content\">" +
                                                "<h2>Hello %s!</h2>" +
                                                "<p>This is a reminder about your KPI goal: <strong>%s</strong></p>" +
                                                "<p><strong>Due Date:</strong> %s</p>" +
                                                "<p><strong>Current Progress:</strong></p>" +
                                                "<div class=\"progress-bar\">" +
                                                "<div class=\"progress-fill\" style=\"width: %.1f%%;\"></div>" +
                                                "</div>" +
                                                "<div class=\"progress-text\">%.1f%% Complete</div>" +
                                                "<p>Keep up the great work! You're making good progress towards your goal.</p>"
                                                +
                                                "<p>If you need to update your progress or have any questions, please log in to your account.</p>"
                                                +
                                                "</div>" +
                                                "<div class=\"footer\">" +
                                                "<p>&copy; 2024 %s. All rights reserved.</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                progressColor, userName, goalName, dueDate, progressPercentage, progressPercentage,
                                COMPANY_NAME);
        }

        @Override
        public String generateKpiCompletionEmail(String userName, String goalName, String completionDate) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "<meta charset=\"UTF-8\">" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                                +
                                                "<title>KPI Goal Completed!</title>" +
                                                "<style>" +
                                                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                                                +
                                                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                                                ".header { background-color: #27ae60; color: white; padding: 20px; text-align: center; }"
                                                +
                                                ".content { padding: 20px; background-color: #f9f9f9; }" +
                                                ".celebration { text-align: center; font-size: 48px; margin: 20px 0; }"
                                                +
                                                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
                                                +
                                                "</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "<div class=\"container\">" +
                                                "<div class=\"header\">" +
                                                "<h1>🎉 Congratulations! 🎉</h1>" +
                                                "</div>" +
                                                "<div class=\"content\">" +
                                                "<div class=\"celebration\">🏆</div>" +
                                                "<h2>Hello %s!</h2>" +
                                                "<p>Congratulations! You have successfully completed your KPI goal:</p>"
                                                +
                                                "<p><strong>%s</strong></p>" +
                                                "<p><strong>Completion Date:</strong> %s</p>" +
                                                "<p>Your dedication and hard work have paid off! This achievement demonstrates your commitment to excellence.</p>"
                                                +
                                                "<p>Keep up the great work and continue setting new goals to drive your success forward.</p>"
                                                +
                                                "</div>" +
                                                "<div class=\"footer\">" +
                                                "<p>&copy; 2024 %s. All rights reserved.</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                userName, goalName, completionDate, COMPANY_NAME);
        }

        @Override
        public String generateKpiOverdueEmail(String userName, String goalName, String dueDate, int daysOverdue) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "<meta charset=\"UTF-8\">" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                                +
                                                "<title>KPI Goal Overdue</title>" +
                                                "<style>" +
                                                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                                                +
                                                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                                                ".header { background-color: #e74c3c; color: white; padding: 20px; text-align: center; }"
                                                +
                                                ".content { padding: 20px; background-color: #f9f9f9; }" +
                                                ".urgent { background-color: #fdf2f2; border-left: 4px solid #e74c3c; padding: 15px; margin: 15px 0; }"
                                                +
                                                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
                                                +
                                                "</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "<div class=\"container\">" +
                                                "<div class=\"header\">" +
                                                "<h1>KPI Goal Overdue</h1>" +
                                                "</div>" +
                                                "<div class=\"content\">" +
                                                "<h2>Hello %s!</h2>" +
                                                "<div class=\"urgent\">" +
                                                "<p><strong>URGENT:</strong> Your KPI goal is overdue!</p>" +
                                                "</div>" +
                                                "<p><strong>Goal:</strong> %s</p>" +
                                                "<p><strong>Original Due Date:</strong> %s</p>" +
                                                "<p><strong>Days Overdue:</strong> %d day(s)</p>" +
                                                "<p>Please review your progress and take immediate action to complete this goal or update the timeline if necessary.</p>"
                                                +
                                                "<p>If you need assistance or have any questions, please contact your supervisor or the support team.</p>"
                                                +
                                                "</div>" +
                                                "<div class=\"footer\">" +
                                                "<p>&copy; 2024 %s. All rights reserved.</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                userName, goalName, dueDate, daysOverdue, COMPANY_NAME);
        }

        @Override
        public String generatePasswordResetEmail(String userName, String resetUrl, String expirationTime) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "<meta charset=\"UTF-8\">" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                                +
                                                "<title>Password Reset Request</title>" +
                                                "<style>" +
                                                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                                                +
                                                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                                                ".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }"
                                                +
                                                ".content { padding: 20px; background-color: #f9f9f9; }" +
                                                ".button { display: inline-block; padding: 12px 24px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }"
                                                +
                                                ".warning { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; margin: 15px 0; border-radius: 5px; }"
                                                +
                                                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
                                                +
                                                "</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "<div class=\"container\">" +
                                                "<div class=\"header\">" +
                                                "<h1>Password Reset Request</h1>" +
                                                "</div>" +
                                                "<div class=\"content\">" +
                                                "<h2>Hello %s!</h2>" +
                                                "<p>We received a request to reset your password for your %s account.</p>"
                                                +
                                                "<p>To reset your password, please click the button below:</p>" +
                                                "<a href=\"%s\" class=\"button\">Reset Password</a>" +
                                                "<div class=\"warning\">" +
                                                "<p><strong>Important:</strong> This link will expire in %s. If you don't reset your password within this time, you'll need to request a new reset link.</p>"
                                                +
                                                "</div>" +
                                                "<p>If you didn't request this password reset, please ignore this email. Your password will remain unchanged.</p>"
                                                +
                                                "<p>For security reasons, this link can only be used once.</p>" +
                                                "</div>" +
                                                "<div class=\"footer\">" +
                                                "<p>&copy; 2024 %s. All rights reserved.</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                userName, APP_NAME, resetUrl, expirationTime, COMPANY_NAME);
        }

        @Override
        public String generateCustomEmail(String templateName, Map<String, String> placeholders) {
                // This is a basic implementation - in a real application, you might load
                // templates from files or database
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>").append(templateName)
                                .append("</title></head>");
                html.append("<body><div class=\"container\"><h1>").append(templateName).append("</h1>");

                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        html.append("<p><strong>").append(entry.getKey()).append(":</strong> ").append(entry.getValue())
                                        .append("</p>");
                }

                html.append("</div></body></html>");
                return html.toString();
        }
}
