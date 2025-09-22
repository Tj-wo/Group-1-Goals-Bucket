package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.ApplicationSettingsUtils;
import org.sers.webutils.server.core.utils.DefaultApplicationSettings;
import org.sers.webutils.server.shared.CustomLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomApplicationSettings extends DefaultApplicationSettings {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final NumberFormat DOUBLE_FORMATTER = new DecimalFormat("##,###,###,###.0000");
	private static final NumberFormat INTEGER_FORMATER = new DecimalFormat("##,###,###,###");
	private static final boolean EXECUTE_BACKGROUND_TASKS = true;
	public static final String BG_JOBS_CHECKSUM = "KPI Tracker";
	public static MailSetting mailSetting;

	@PostConstruct
	public void init() {
		ApplicationSettingsUtils.Util.createApplicationSettings(this);
		super.setExecuteBackgroundJobs(EXECUTE_BACKGROUND_TASKS);
		super.setBgJobsCheckSum(BG_JOBS_CHECKSUM);
		log.info(this.getClass().getName() + " - Initialized default application settings");
		try {
			CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
					String.format("Creating instance for mail settings..."));
			mailSetting = ApplicationContextProvider.getBean(MailSettingService.class)
					.getMailSetting();
			CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR,
					String.format("Created instance for mail settings..."));
		} catch (Exception e) {
			CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, String.format(
					"Error: %s, while creating instance for application settings. We shall sleep and wake up later...",
					e.getMessage()));
		}
	}

	@Override
	public String getOrganizationName() {
		return "Pahappa Ltd";
	}

	@Override
	public int getMaximumRecordsPerPage() {
		return 10;
	}

	@Override
	public List<String> getErrorMessageRecipients() {
		return Arrays.asList(new String[] { "fred@pahappa.com" });
	}

	@Override
	public String getDefaultMailSenderAddress() {
		if (CustomApplicationSettings.mailSetting != null) {
			return CustomApplicationSettings.mailSetting.getSenderEmail();
		}
		return "systems@pahappa.com"; // Default fallback
	}

	@Override
	public String getDefaultMailSenderPassword() {
		if (CustomApplicationSettings.mailSetting != null) {
			return CustomApplicationSettings.mailSetting.getSmtpPassword();
		}
		return "SMTP_PASSWORD"; // Default fallback
	}

	@Override
	public String getDefaultMailSenderSmtpHost() {
		if (CustomApplicationSettings.mailSetting != null) {
			return CustomApplicationSettings.mailSetting.getSmtpHost();
		}
		return "smtp.gmail.com"; // Default Gmail SMTP
	}

	@Override
	public String getDefaultMailSenderSmtpPort() {
		if (CustomApplicationSettings.mailSetting != null) {
			return CustomApplicationSettings.mailSetting.getSmtpPort();
		}
		return "587"; // Default SMTP port
	}

	@Override
	public String getDefaultClientFeedbackMail() {
		return "xyz@gmail.com";
	}

	@Override
	public String getDefaultSuperUserEmail() {
		return "leonard@pahappa.com";
	}

	@Override
	public String getDefaultSuperUserPhoneNumber() {
		return "256700000000";
	}

	@Override
	public NumberFormat getDoubleNumberFormater() {
		return DOUBLE_FORMATTER;
	}

	@Override
	public NumberFormat getIntegerNumberFormater() {
		return INTEGER_FORMATER;
	}

	@Override
	public String getPasswordChangeToken() {
		return super.passwordChangeToken;
	}
}
