package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.core.services.SmtpConfigService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * Implementation of SMTP configuration service
 * Manages SMTP credentials and session creation
 */
@Service("smtpConfigService")
public class SmtpConfigServiceImpl implements SmtpConfigService {

    private final MailSettingService mailSettingService;
    private Session smtpSession;
    private String smtpHost;
    private String smtpPort;
    private String senderEmail;
    private boolean smtpAuth;
    private boolean smtpStartTls;

    @Autowired
    public SmtpConfigServiceImpl(MailSettingService mailSettingService) {
        this.mailSettingService = mailSettingService;
        initializeSmtpConfig();
    }

    @PostConstruct
    public void postConstruct() {
        // Re-initialize after Spring context is fully loaded to ensure
        // MailSettingService is ready
        initializeSmtpConfig();
    }

    private void initializeSmtpConfig() {
        try {
            MailSetting mailSetting = mailSettingService.getMailSetting();

            if (mailSetting != null) {
                // Use settings from database
                this.smtpHost = mailSetting.getSmtpHost();
                this.smtpPort = mailSetting.getSmtpPort();
                this.senderEmail = mailSetting.getSenderEmail();
                this.smtpAuth = mailSetting.isSmtpAuth();
                this.smtpStartTls = mailSetting.isSmtpStartTls();

                this.smtpSession = createSmtpSession(mailSetting);

                CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                        "SMTP session initialized with database settings for host: " + smtpHost);
            } else {
                // Use default settings
                initializeWithDefaults();
            }
        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to initialize SMTP session with database settings: " + e.getMessage());
            initializeWithDefaults();
        }
    }

    private void initializeWithDefaults() {
        try {
            // Set default values first
            this.smtpHost = "smtp.gmail.com";
            this.smtpPort = "587";
            this.senderEmail = "systems@pahappa.com";
            this.smtpAuth = true;
            this.smtpStartTls = true;

            String username = null;
            String password = null;

            // Try to load properties from smtp-config.properties file
            try {
                java.util.Properties props = new java.util.Properties();
                java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("smtp-config.properties");

                if (input != null) {
                    props.load(input);
                    input.close();

                    // Get configuration from properties file
                    this.smtpHost = props.getProperty("smtp.host", smtpHost);
                    this.smtpPort = props.getProperty("smtp.port", smtpPort);
                    this.senderEmail = props.getProperty("sender.email", senderEmail);
                    this.smtpAuth = Boolean.parseBoolean(props.getProperty("smtp.auth", String.valueOf(smtpAuth)));
                    this.smtpStartTls = Boolean
                            .parseBoolean(props.getProperty("smtp.starttls", String.valueOf(smtpStartTls)));

                    username = props.getProperty("smtp.username");
                    password = props.getProperty("smtp.password");

                    CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                            "Loaded SMTP configuration from properties file");
                } else {
                    CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_WARN,
                            "smtp-config.properties file not found, using default values");
                }
            } catch (Exception e) {
                CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_WARN,
                        "Failed to load smtp-config.properties: " + e.getMessage() + ", using default values");
            }

            // Fallback to environment variables if properties not found
            if (username == null || password == null) {
                username = System.getenv("SMTP_USERNAME");
                password = System.getenv("SMTP_PASSWORD");
                if (username != null && password != null) {
                    CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                            "Using SMTP credentials from environment variables");
                }
            }

            // Fallback to system properties if environment variables not found
            if (username == null || password == null) {
                username = System.getProperty("smtp.username");
                password = System.getProperty("smtp.password");
                if (username != null && password != null) {
                    CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                            "Using SMTP credentials from system properties");
                }
            }

            // If no credentials found, throw an error
            if (username == null || password == null) {
                throw new RuntimeException(
                        "SMTP credentials not found. Please configure smtp.username and smtp.password in smtp-config.properties file, or set SMTP_USERNAME and SMTP_PASSWORD environment variables.");
            }

            // Create default mail setting
            MailSetting defaultSetting = new MailSetting();
            defaultSetting.setSmtpHost(smtpHost);
            defaultSetting.setSmtpPort(smtpPort);
            defaultSetting.setSenderEmail(senderEmail);
            defaultSetting.setSmtpAuth(smtpAuth);
            defaultSetting.setSmtpStartTls(smtpStartTls);
            defaultSetting.setSmtpUsername(username);
            defaultSetting.setSmtpPassword(password);

            this.smtpSession = createSmtpSession(defaultSetting);

            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_INFO,
                    "SMTP session initialized with configuration for host: " + smtpHost);

        } catch (Exception e) {
            CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
                    "Failed to initialize SMTP session: " + e.getMessage());
            throw new RuntimeException("Failed to initialize SMTP configuration", e);
        }
    }

    private Session createSmtpSession(MailSetting mailSetting) {
        Properties props = new Properties();

        // SMTP server configuration
        props.put("mail.smtp.host", mailSetting.getSmtpHost());
        props.put("mail.smtp.port", mailSetting.getSmtpPort());
        props.put("mail.smtp.auth", String.valueOf(mailSetting.isSmtpAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(mailSetting.isSmtpStartTls()));



            // Add timeout settings to prevent hanging
            props.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
            props.put("mail.smtp.timeout", "10000"); // 10 seconds
            props.put("mail.smtp.writetimeout", "10000"); // 10 seconds

            
        // Create a new session with an authenticator
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailSetting.getSmtpUsername(), mailSetting.getSmtpPassword());
            }
        });
    }

    @Override
    public Session getSmtpSession() {
        if (smtpSession == null) {
            initializeSmtpConfig();
        }
        return smtpSession;
    }

    @Override
    public String getSmtpHost() {
        return smtpHost;
    }

    @Override
    public String getSmtpPort() {
        return smtpPort;
    }

    @Override
    public String getSenderEmail() {
        return senderEmail;
    }
}