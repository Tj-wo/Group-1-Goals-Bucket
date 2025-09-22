package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.pahappa.systems.kpiTracker.core.services.SmtpService;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service("mailService")
@Transactional
public class MailServiceImpl implements MailService {

    private final SmtpService smtpService;

    @Autowired
    public MailServiceImpl(SmtpService smtpService) {
        this.smtpService = smtpService;
    }

    @Override
    public void sendMail(String recipient, String subject, String messageBody) {
        sendMail(Collections.singletonList(recipient), subject, messageBody);
    }

    @Override
    public void sendMail(List<String> recipients, String subject, String messageBody) {
        try {
            // Send email to each recipient individually
            for (String recipient : recipients) {
                boolean success = smtpService.sendMail(recipient, subject, messageBody);
                if (!success) {
                    throw new RuntimeException("Failed to send email to: " + recipient);
                }
            }
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Email sent successfully via SMTP to: " + recipients);
        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send email via SMTP. Error: " + e.getMessage());
            throw new RuntimeException("Failed to send email via SMTP", e);
        }
    }

    @Override
    public void sendHtmlMail(String recipient, String subject, String htmlBody, String textBody) {
        sendHtmlMail(Collections.singletonList(recipient), subject, htmlBody, textBody);
    }

    @Override
    public void sendHtmlMail(List<String> recipients, String subject, String htmlBody, String textBody) {
        try {
            // Send HTML email to each recipient individually
            for (String recipient : recipients) {
                boolean success = smtpService.sendHtmlMail(recipient, subject, htmlBody);
                if (!success) {
                    throw new RuntimeException("Failed to send HTML email to: " + recipient);
                }
            }
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "HTML email sent successfully via SMTP to: " + recipients);
        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send HTML email via SMTP. Error: " + e.getMessage());
            throw new RuntimeException("Failed to send HTML email via SMTP", e);
        }
    }
}
