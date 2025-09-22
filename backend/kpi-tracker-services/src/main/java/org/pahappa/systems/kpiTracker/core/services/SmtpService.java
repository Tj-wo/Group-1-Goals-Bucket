package org.pahappa.systems.kpiTracker.core.services;

/**
 * Service for sending emails via SMTP
 */
public interface SmtpService {

    /**
     * Sends a plain text email to a single recipient via SMTP.
     *
     * @param recipient   Email address of the recipient.
     * @param subject     Subject of the email.
     * @param messageBody Body of the email.
     * @return true if email was sent successfully, false otherwise.
     */
    boolean sendMail(String recipient, String subject, String messageBody);

    /**
     * Sends an HTML email to a single recipient via SMTP.
     *
     * @param recipient Email address of the recipient.
     * @param subject   Subject of the email.
     * @param htmlBody  HTML body of the email.
     * @return true if email was sent successfully, false otherwise.
     */
    boolean sendHtmlMail(String recipient, String subject, String htmlBody);
}