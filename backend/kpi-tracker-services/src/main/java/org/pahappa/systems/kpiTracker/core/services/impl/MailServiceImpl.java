package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.MailService;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service("mailService")
@Transactional
public class MailServiceImpl implements MailService {

    private final MailSettingService mailSettingService;

    @Autowired
    public MailServiceImpl(MailSettingService mailSettingService) {
        this.mailSettingService = mailSettingService;
    }

    @Override
    public void sendMail(String recipient, String subject, String messageBody) {
        sendMail(Collections.singletonList(recipient), subject, messageBody);
    }

    @Override
    public void sendMail(List<String> recipients, String subject, String messageBody) {
        MailSetting mailSetting = mailSettingService.getMailSetting();
        if (mailSetting == null) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, "Mail settings not configured. Cannot send email.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", mailSetting.getSenderSmtpHost());
        props.put("mail.smtp.port", mailSetting.getSenderSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailSetting.getSenderAddress(), mailSetting.getSenderPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailSetting.getSenderAddress()));

            InternetAddress[] recipientAddresses = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                recipientAddresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO, "Email sent successfully to: " + recipients);

        } catch (MessagingException e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, "Failed to send email. Error: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
