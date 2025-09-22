package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.SmtpConfigService;
import org.pahappa.systems.kpiTracker.core.services.SmtpService;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Implementation of SMTP email service
 */
@Service("smtpService")
public class SmtpServiceImpl implements SmtpService {

    private final SmtpConfigService smtpConfigService;

    @Autowired
    public SmtpServiceImpl(SmtpConfigService smtpConfigService) {
        this.smtpConfigService = smtpConfigService;
    }

    @Override
    public boolean sendMail(String recipient, String subject, String messageBody) {
        try {
            Session session = smtpConfigService.getSmtpSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpConfigService.getSenderEmail()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "Email sent successfully via SMTP to: " + recipient);
            return true;
        } catch (MessagingException e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send email via SMTP to " + recipient + ". Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendHtmlMail(String recipient, String subject, String htmlBody) {
        try {
            Session session = smtpConfigService.getSmtpSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpConfigService.getSenderEmail()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            // Set content type to HTML
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "HTML email sent successfully via SMTP to: " + recipient);
            return true;
        } catch (MessagingException e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to send HTML email via SMTP to " + recipient + ". Error: " + e.getMessage());
            return false;
        }
    }
}