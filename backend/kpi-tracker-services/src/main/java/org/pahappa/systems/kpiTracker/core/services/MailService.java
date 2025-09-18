package org.pahappa.systems.kpiTracker.core.services;

import java.util.List;

public interface MailService {
    /**
     * Sends an email to a single recipient.
     *
     * @param recipient   Email address of the recipient.
     * @param subject     Subject of the email.
     * @param messageBody Body of the email.
     */
    void sendMail(String recipient, String subject, String messageBody);

    /**
     * Sends an email to a list of recipients.
     *
     * @param recipients  List of email addresses for recipients.
     * @param subject     Subject of the email.
     * @param messageBody Body of the email.
     */
    void sendMail(List<String> recipients, String subject, String messageBody);
}
