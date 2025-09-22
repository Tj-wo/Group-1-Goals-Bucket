package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "mail_settings")
@Inheritance(strategy = InheritanceType.JOINED)
public class MailSetting extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// SMTP Configuration
	private String smtpHost;
	private String smtpPort;
	private String smtpUsername;
	private String smtpPassword;
	private String senderEmail;
	private boolean smtpAuth;
	private boolean smtpStartTls;

	/**
	 * @return the smtpHost
	 */
	@Column(name = "smtp_host", length = 100)
	public String getSmtpHost() {
		return smtpHost;
	}

	/**
	 * @param smtpHost the smtpHost to set
	 */
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	/**
	 * @return the smtpPort
	 */
	@Column(name = "smtp_port", length = 10)
	public String getSmtpPort() {
		return smtpPort;
	}

	/**
	 * @param smtpPort the smtpPort to set
	 */
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	/**
	 * @return the smtpUsername
	 */
	@Column(name = "smtp_username", length = 100)
	public String getSmtpUsername() {
		return smtpUsername;
	}

	/**
	 * @param smtpUsername the smtpUsername to set
	 */
	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	/**
	 * @return the smtpPassword
	 */
	@Column(name = "smtp_password", length = 100)
	public String getSmtpPassword() {
		return smtpPassword;
	}

	/**
	 * @param smtpPassword the smtpPassword to set
	 */
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	/**
	 * @return the smtpAuth
	 */
	@Column(name = "smtp_auth")
	public boolean isSmtpAuth() {
		return smtpAuth;
	}

	/**
	 * @param smtpAuth the smtpAuth to set
	 */
	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	/**
	 * @return the smtpStartTls
	 */
	@Column(name = "smtp_starttls")
	public boolean isSmtpStartTls() {
		return smtpStartTls;
	}

	/**
	 * @param smtpStartTls the smtpStartTls to set
	 */
	public void setSmtpStartTls(boolean smtpStartTls) {
		this.smtpStartTls = smtpStartTls;
	}

	/**
	 * @return the senderEmail
	 */
	@Column(name = "sender_email", length = 100)
	public String getSenderEmail() {
		return senderEmail;
	}

	/**
	 * @param senderEmail the senderEmail to set
	 */
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof MailSetting && (super.getId() != null)
				? super.getId().equals(((MailSetting) object).getId())
				: (object == this);
	}

	@Override
	public int hashCode() {
		return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
	}
}
