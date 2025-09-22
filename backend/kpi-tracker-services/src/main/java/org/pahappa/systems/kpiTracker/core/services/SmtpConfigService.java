package org.pahappa.systems.kpiTracker.core.services;

import javax.mail.Session;
import java.util.Properties;

/**
 * Service for managing SMTP configuration and session creation
 */
public interface SmtpConfigService {

    /**
     * Creates and returns an SMTP session with the configured credentials
     *
     * @return JavaMail Session
     */
    Session getSmtpSession();

    /**
     * Gets the configured SMTP host
     *
     * @return SMTP host
     */
    String getSmtpHost();

    /**
     * Gets the configured SMTP port
     *
     * @return SMTP port
     */
    String getSmtpPort();

    /**
     * Gets the configured sender email address
     *
     * @return sender email address
     */
    String getSenderEmail();
}